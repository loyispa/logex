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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Monitors a log file for new entries and processes them using a LogHandler. */
public class LogTailer extends TailerListenerAdapter implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(LogTailer.class);
  private final Path filePath;
  private final Tailer tailer;
  private final Map<String, LogParser> parsers;
  private final Map<String, List<LogHandler>> handlers;

  public LogTailer(
      Path filePath,
      Map<String, LogParser> parsers,
      Map<String, List<LogHandler>> handlers,
      int delayMillis,
      boolean end) {
    this.filePath = filePath;
    this.parsers = parsers;
    this.handlers = handlers;
    this.tailer = new Tailer(filePath.toFile(), this, delayMillis, end);
  }

  @Override
  public void handle(String line) {
    LOG.debug("Handle: {}", line);
    parsers.forEach(
        (matchPattern, parser) -> {
          Map<String, String> data = parser.parse(line);
          LOG.info("Parse: {}", data);
          if (data.isEmpty()) {
            return;
          }
          data.putIfAbsent("path", filePath.toString());
          handlers.get(matchPattern).forEach(handler -> handler.handle(data));
        });
  }

  @Override
  public void handle(Exception ex) {
    LOG.error("Error tailing file {}: {}", filePath, ex.getMessage());
  }

  public void stop() {
    if (tailer != null) {
      tailer.stop();
      LOG.warn("Stopped tailing inactive file: {}", filePath);
    }
  }

  @Override
  public void run() {
    tailer.run();
  }
}
