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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.logex.exporter.config.LabelConfig;
import org.logex.exporter.config.MetricConfig;

class LabelProcessorTest {

  @Test
  void processLabels_shouldReturnLabelValues() {
    // Given
    Map<String, String> logData = new HashMap<>();
    logData.put("field1", "value1");

    MetricConfig metricConfig = new MetricConfig();
    LabelConfig labelConfig = new LabelConfig();
    labelConfig.setSourceField("field1");
    metricConfig.setLabels(Collections.singletonList(labelConfig));

    // When
    List<String> labelValues = LabelProcessor.processLabels(logData, metricConfig);

    // Then
    assertEquals(1, labelValues.size());
    assertEquals("value1", labelValues.get(0));
  }

  @Test
  void processLabels_shouldUseDefaultValueWhenFieldIsMissing() {
    // Given
    Map<String, String> logData = new HashMap<>();
    MetricConfig metricConfig = new MetricConfig();
    LabelConfig labelConfig = new LabelConfig();
    labelConfig.setSourceField("missing_field");
    labelConfig.setDefaultValue("default");
    metricConfig.setLabels(Collections.singletonList(labelConfig));

    // When
    List<String> labelValues = LabelProcessor.processLabels(logData, metricConfig);

    // Then
    assertEquals(1, labelValues.size());
    assertEquals("default", labelValues.get(0));
  }

  @Test
  void processLabels_shouldApplyRegexReplaceAction() {
    // Given
    Map<String, String> logData = new HashMap<>();
    logData.put("field1", "some_value");

    MetricConfig metricConfig = new MetricConfig();
    LabelConfig labelConfig = new LabelConfig();
    labelConfig.setSourceField("field1");
    labelConfig.setAction("regex_replace");
    labelConfig.setRegex("some_(\\w+)");
    labelConfig.setReplacement("$1");
    metricConfig.setLabels(Collections.singletonList(labelConfig));

    // When
    List<String> labelValues = LabelProcessor.processLabels(logData, metricConfig);

    // Then
    assertEquals(1, labelValues.size());
    assertEquals("value", labelValues.get(0));
  }

  @Test
  void processLabels_shouldReturnEmptyStringForNullValue() {
    // Given
    Map<String, String> logData = new HashMap<>();
    MetricConfig metricConfig = new MetricConfig();
    LabelConfig labelConfig = new LabelConfig();
    labelConfig.setSourceField("missing_field");
    metricConfig.setLabels(Collections.singletonList(labelConfig));

    // When
    List<String> labelValues = LabelProcessor.processLabels(logData, metricConfig);

    // Then
    assertEquals(1, labelValues.size());
    assertEquals("", labelValues.get(0));
  }
}
