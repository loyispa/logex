/*
 * Copyright 2025 loyispa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.logex.exporter.log;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;
import org.logex.exporter.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Manages multiple LogTailer instances, starting and stopping them as needed. */
public class LogTailerManager {

  private static final Logger LOG = LoggerFactory.getLogger(LogTailerManager.class);
  private final AppConfig appConfig;
  private final Map<String, LogParser> parsers = new HashMap<>();
  private final Map<String, List<LogHandler>> handlers = new HashMap<>();
  private final Map<Path, LogTailer> activeTailers = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private final Predicate<Path> isFileInactive;

  public LogTailerManager(AppConfig appConfig) {
    this.appConfig = appConfig;
    this.isFileInactive =
        path -> {
          long inactivityThresholdMillis = appConfig.getFileInactivityTimeoutSeconds() * 1000L;
          long currentTime = System.currentTimeMillis();
          try {
            long lastModified = Files.getLastModifiedTime(path).toMillis();
            return (currentTime - lastModified) > inactivityThresholdMillis;
          } catch (IOException e) {
            LOG.error("Error getting last modified time for {}: {}", path, e.getMessage());
            return true; // Treat as inactive if we can't read last modified time
          }
        };
  }

  public void findAndTailFiles() {
    try {
      Path startingPath = Paths.get(appConfig.getPath()).getParent();
      while (startingPath != null && !Files.exists(startingPath)) {
        startingPath = startingPath.getParent();
      }
      if (startingPath == null) {
        LOG.warn("Warning: Base path for glob pattern does not exist: {}", appConfig.getPath());
        return;
      }

      final PathMatcher matcher =
          FileSystems.getDefault()
              .getPathMatcher("glob:" + Paths.get(appConfig.getPath()).getFileName().toString());

      Files.walk(startingPath)
          .filter(Files::isRegularFile)
          .filter(matcher::matches)
          .filter(path -> !isFileInactive.test(path))
          .forEach(
              path ->
                  activeTailers.computeIfAbsent(
                      path,
                      p -> {
                        LogTailer tailer =
                            new LogTailer(p, parsers, handlers, 500, appConfig.isTailFromEnd());
                        executor.submit(tailer);
                        LOG.info("Start tailing file: {}", p);
                        return tailer;
                      }));
    } catch (IOException e) {
      LOG.error("Error while scanning for log files: {}", e.getMessage());
    }
  }

  public void cleanupInactiveTailers() {
    activeTailers
        .entrySet()
        .removeIf(
            entry -> {
              Path filePath = entry.getKey();
              LogTailer tailer = entry.getValue();
              if (isFileInactive.test(filePath)) {
                tailer.stop();
                return true;
              }
              return false;
            });
  }

  public void register(String matchFormat, LogHandler handler) {
    parsers.computeIfAbsent(
        matchFormat, matchPattern -> new LogParser(matchPattern, appConfig.getPatterns()));
    handlers.computeIfAbsent(matchFormat, matchPattern -> new ArrayList<>()).add(handler);
  }

  public void start() {
    scheduler.scheduleAtFixedRate(
        this::findAndTailFiles, 0, appConfig.getFileScanIntervalSeconds(), TimeUnit.SECONDS);
    // Schedule cleanup task to run every minute
    scheduler.scheduleAtFixedRate(this::cleanupInactiveTailers, 1, 1, TimeUnit.MINUTES);
  }
}
