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

class CounterHandlerTest {

  private MetricConfig mockConfig;
  private CollectorRegistry registry;
  private CounterHandler counterHandler;

  @BeforeEach
  void setUp() {
    mockConfig = mock(MetricConfig.class);
    when(mockConfig.getName()).thenReturn("test_counter");
    when(mockConfig.getHelp()).thenReturn("test help");
    when(mockConfig.getLabels()).thenReturn(Collections.emptyList());
    registry = new CollectorRegistry();
    counterHandler = new CounterHandler(mockConfig, registry);
  }

  @Test
  void handle_shouldIncrementCounter() {
    // Given
    Map<String, String> data = new HashMap<>();

    // When
    counterHandler.handle(data);

    // Then
    assertEquals(1.0, registry.getSampleValue("test_counter_total"));
  }

  @Test
  void handle_shouldIncrementCounterByValue() {
    // Given
    Map<String, String> data = new HashMap<>();
    data.put("value", "5.0");
    when(mockConfig.getValueField()).thenReturn("value");

    // When
    counterHandler.handle(data);

    // Then
    assertEquals(5.0, registry.getSampleValue("test_counter_total"));
  }
}
