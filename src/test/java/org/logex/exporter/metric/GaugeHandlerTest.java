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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import io.prometheus.client.CollectorRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logex.exporter.config.MetricConfig;

class GaugeHandlerTest {

  private MetricConfig mockConfig;
  private CollectorRegistry registry;
  private GaugeHandler gaugeHandler;

  @BeforeEach
  void setUp() {
    mockConfig = mock(MetricConfig.class);
    when(mockConfig.getName()).thenReturn("test_gauge");
    when(mockConfig.getHelp()).thenReturn("test help");
    when(mockConfig.getLabels()).thenReturn(Collections.emptyList());
    registry = new CollectorRegistry();
    gaugeHandler = new GaugeHandler(mockConfig, registry);
  }

  @Test
  void handle_shouldSetGauge() {
    // Given
    Map<String, String> data = new HashMap<>();

    // When
    gaugeHandler.handle(data);

    // Then
    assertEquals(1.0, registry.getSampleValue("test_gauge"));
  }

  @Test
  void handle_shouldSetGaugeToValue() {
    // Given
    Map<String, String> data = new HashMap<>();
    data.put("value", "123.45");
    when(mockConfig.getValueField()).thenReturn("value");

    // When
    gaugeHandler.handle(data);

    // Then
    assertEquals(123.45, registry.getSampleValue("test_gauge"));
  }
}
