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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.logex.exporter.config.AppConfig;

class LogTailerManagerTest {

  @TempDir Path tempDir;

  private AppConfig mockAppConfig;
  private LogTailerManager logTailerManager;
  private Map<String, LogParser> parsers;
  private Map<String, List<LogHandler>> handlers;
  private Map<Path, LogTailer> activeTailers;

  @BeforeEach
  void setUp() throws Exception {
    mockAppConfig = mock(AppConfig.class);
    when(mockAppConfig.getFileInactivityTimeoutSeconds()).thenReturn(1);
    when(mockAppConfig.getFileScanIntervalSeconds()).thenReturn(1);

    Map<String, String> patterns = new HashMap<>();
    patterns.put("WORD", "\\w+");
    when(mockAppConfig.getPatterns()).thenReturn(patterns);

    logTailerManager = new LogTailerManager(mockAppConfig);

    // Use reflection to access private fields for testing
    Field parsersField = LogTailerManager.class.getDeclaredField("parsers");
    parsersField.setAccessible(true);
    parsers = (Map<String, LogParser>) parsersField.get(logTailerManager);

    Field handlersField = LogTailerManager.class.getDeclaredField("handlers");
    handlersField.setAccessible(true);
    handlers = (Map<String, List<LogHandler>>) handlersField.get(logTailerManager);

    Field activeTailersField = LogTailerManager.class.getDeclaredField("activeTailers");
    activeTailersField.setAccessible(true);
    activeTailers = (Map<Path, LogTailer>) activeTailersField.get(logTailerManager);
  }

  @Test
  void register_shouldAddParserAndHandler() {
    // Given
    String matchFormat = "%{WORD:word}";
    LogHandler mockHandler = mock(LogHandler.class);

    // When
    logTailerManager.register(matchFormat, mockHandler);

    // Then
    assertTrue(parsers.containsKey(matchFormat));
    assertNotNull(parsers.get(matchFormat));
    assertTrue(handlers.containsKey(matchFormat));
    assertEquals(1, handlers.get(matchFormat).size());
    assertEquals(mockHandler, handlers.get(matchFormat).get(0));
  }

  @Test
  void shouldStartTailerForNewFile() throws IOException {
    // Given
    Path logFile = tempDir.resolve("test.log");
    Files.createFile(logFile);

    when(mockAppConfig.getPath()).thenReturn(tempDir.resolve("*.log").toString());

    // When
    logTailerManager.findAndTailFiles();

    // Then
    assertEquals(1, activeTailers.size());
    assertTrue(activeTailers.containsKey(logFile));
  }

  @Test
  void shouldRemoveInactiveTailer() throws Exception {
    // Given
    LogTailer mockTailer = mock(LogTailer.class);
    Path inactiveFile = tempDir.resolve("inactive.log");
    Files.createFile(inactiveFile);
    activeTailers.put(inactiveFile, mockTailer);

    // When
    // Manually trigger cleanup after a delay
    Thread.sleep(1100); // Simulate time passing for inactivity
    logTailerManager.cleanupInactiveTailers();

    // Then
    assertTrue(activeTailers.isEmpty());
    verify(mockTailer).stop();
  }

  @Test
  void shouldNotAddExistingTailer() throws IOException {
    // Given
    Path logFile = tempDir.resolve("test.log");
    Files.createFile(logFile);
    activeTailers.put(logFile, mock(LogTailer.class)); // Pre-add the tailer

    when(mockAppConfig.getPath()).thenReturn(tempDir.toString() + File.separatorChar +  "*.log");

    // When
    logTailerManager.findAndTailFiles();

    // Then
    assertEquals(1, activeTailers.size()); // Should not add a new one
  }

  @Test
  void shouldKeepActiveTailer() throws Exception {
    // Given
    LogTailer mockTailer = mock(LogTailer.class);
    Path activeFile = tempDir.resolve("active.log");
    Files.createFile(activeFile);
    activeTailers.put(activeFile, mockTailer);

    // When
    // Simulate file being active (no sleep needed as it's not inactive)
    logTailerManager.cleanupInactiveTailers();

    // Then
    verify(mockTailer, never()).stop();
    assertEquals(1, activeTailers.size());
  }
}
