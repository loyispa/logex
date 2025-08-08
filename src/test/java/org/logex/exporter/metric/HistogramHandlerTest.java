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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logex.exporter.config.LabelConfig;
import org.logex.exporter.config.MetricConfig;

class HistogramHandlerTest {

  private MetricConfig mockMetricConfig;
  private CollectorRegistry registry;

  @BeforeEach
  void setUp() {
    mockMetricConfig = mock(MetricConfig.class);
    registry = new CollectorRegistry();

    // Mock MetricConfig behavior
    when(mockMetricConfig.getName()).thenReturn("test_histogram");
    when(mockMetricConfig.getHelp()).thenReturn("Test Histogram Help");
    when(mockMetricConfig.getMaxLabelSets()).thenReturn(10);
    when(mockMetricConfig.getExpirationSeconds()).thenReturn(0);
    when(mockMetricConfig.getBuckets()).thenReturn(null); // Default buckets
  }

  @Test
  void testHandleObservesHistogramWithLabels() {
    // Arrange
    LabelConfig lc1 = new LabelConfig();
    lc1.setName("status");
    lc1.setSourceField("http_status");
    lc1.setAction("none");

    LabelConfig lc2 = new LabelConfig();
    lc2.setName("method");
    lc2.setSourceField("http_method");
    lc2.setAction("none");

    when(mockMetricConfig.getLabels()).thenReturn(Arrays.asList(lc1, lc2));
    when(mockMetricConfig.getValueField()).thenReturn("duration");

    HistogramHandler handler = new HistogramHandler(mockMetricConfig, registry);

    Map<String, String> logData = new HashMap<>();
    logData.put("http_status", "200");
    logData.put("http_method", "GET");
    logData.put("duration", "0.5");

    // Act
    handler.handle(logData);

    // Assert
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_bucket",
            new String[] {"status", "method", "le"},
            new String[] {"200", "GET", "0.75"}));
    assertEquals(
        0.5,
        registry.getSampleValue(
            "test_histogram_sum", new String[] {"status", "method"}, new String[] {"200", "GET"}));
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_count",
            new String[] {"status", "method"},
            new String[] {"200", "GET"}));
  }

  @Test
  void testHandleObservesHistogramWithDefaultValue() {
    // Arrange
    LabelConfig lc1 = new LabelConfig();
    lc1.setName("status");
    lc1.setSourceField("http_status");
    lc1.setAction("none");

    when(mockMetricConfig.getLabels()).thenReturn(Arrays.asList(lc1));
    when(mockMetricConfig.getValueField()).thenReturn(null); // Default value 1.0

    HistogramHandler handler = new HistogramHandler(mockMetricConfig, registry);

    Map<String, String> logData = new HashMap<>();
    logData.put("http_status", "200");

    // Act
    handler.handle(logData);

    // Assert
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_bucket", new String[] {"status", "le"}, new String[] {"200", "1.0"}));
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_sum", new String[] {"status"}, new String[] {"200"}));
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_count", new String[] {"status"}, new String[] {"200"}));
  }

  @Test
  void testHandleObservesHistogramWithCustomBuckets() {
    // Arrange
    when(mockMetricConfig.getLabels()).thenReturn(Collections.emptyList());
    when(mockMetricConfig.getValueField()).thenReturn("duration");
    when(mockMetricConfig.getBuckets()).thenReturn(Arrays.asList(0.1, 0.5, 1.0));

    HistogramHandler handler = new HistogramHandler(mockMetricConfig, registry);

    Map<String, String> logData = new HashMap<>();
    logData.put("duration", "0.2");

    // Act
    handler.handle(logData);

    // Assert
    assertEquals(
        0.0,
        registry.getSampleValue(
            "test_histogram_bucket", new String[] {"le"}, new String[] {"0.1"}));
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_bucket", new String[] {"le"}, new String[] {"0.5"}));
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_bucket", new String[] {"le"}, new String[] {"1.0"}));
    assertEquals(
        1.0,
        registry.getSampleValue(
            "test_histogram_bucket", new String[] {"le"}, new String[] {"+Inf"}));
    assertEquals(0.2, registry.getSampleValue("test_histogram_sum"));
    assertEquals(1.0, registry.getSampleValue("test_histogram_count"));
  }
}
