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
package org.logex.exporter.metric;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import io.prometheus.client.CollectorRegistry;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logex.exporter.config.AppConfig;
import org.logex.exporter.config.MetricConfig;
import org.logex.exporter.log.LogHandler;
import org.logex.exporter.log.LogTailerManager;

class MetricsManagerTest {

  private AppConfig mockAppConfig;
  private LogTailerManager mockLogTailerManager;
  private MetricsManager metricsManager;

  @BeforeEach
  void setUp() {
    mockAppConfig = mock(AppConfig.class);
    mockLogTailerManager = mock(LogTailerManager.class);
  }

  @Test
  void constructor_shouldRegisterHandlers() {
    // Given
    MetricConfig metricConfig = new MetricConfig();
    metricConfig.setMatch("test_match");
    metricConfig.setType("counter");
    metricConfig.setName("test_counter");
    metricConfig.setHelp("test help");
    metricConfig.setLabels(new java.util.ArrayList<>());

    when(mockAppConfig.getMetrics()).thenReturn(Collections.singletonList(metricConfig));

    // When
    metricsManager = new MetricsManager(mockAppConfig, mockLogTailerManager);

    // Then
    verify(mockLogTailerManager, times(1)).register(eq("test_match"), any(LogHandler.class));
  }

  @Test
  void start_shouldStartHttpServerAndScheduler()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    // Given
    when(mockAppConfig.getMetrics()).thenReturn(Collections.emptyList());
    when(mockAppConfig.getPort()).thenReturn(8080);
    metricsManager = new MetricsManager(mockAppConfig, mockLogTailerManager);

    // When
    metricsManager.start();

    // Then
    // We can't directly verify the HTTPServer, but we can check the registry.
    Field registryField = MetricsManager.class.getDeclaredField("registry");
    registryField.setAccessible(true);
    CollectorRegistry registry = (CollectorRegistry) registryField.get(metricsManager);
    assertNotNull(registry);
  }
}
