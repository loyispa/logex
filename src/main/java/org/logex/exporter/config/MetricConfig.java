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
package org.logex.exporter.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class MetricConfig {
  private String name;
  private String help;
  private String type;
  private String match;

  @JsonProperty("value_field")
  private String valueField;

  @JsonProperty("max_label_sets")
  private int maxLabelSets = 0;

  @JsonProperty("expiration_seconds")
  private int expirationSeconds = 0; // Default to 0, meaning no expiration

  private List<LabelConfig> labels;

  private List<Double> buckets;

  private Map<Double, Double> quantiles;

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMatch() {
    return match;
  }

  public void setMatch(String match) {
    this.match = match;
  }

  public String getValueField() {
    return valueField;
  }

  public void setValueField(String valueField) {
    this.valueField = valueField;
  }

  public int getMaxLabelSets() {
    return maxLabelSets;
  }

  public void setMaxLabelSets(int maxLabelSets) {
    this.maxLabelSets = maxLabelSets;
  }

  public int getExpirationSeconds() {
    return expirationSeconds;
  }

  public void setExpirationSeconds(int expirationSeconds) {
    this.expirationSeconds = expirationSeconds;
  }

  public List<LabelConfig> getLabels() {
    return labels;
  }

  public void setLabels(List<LabelConfig> labels) {
    this.labels = labels;
  }

  public List<Double> getBuckets() {
    return buckets;
  }

  public void setBuckets(List<Double> buckets) {
    this.buckets = buckets;
  }

  public Map<Double, Double> getQuantiles() {
    return quantiles;
  }

  public void setQuantiles(Map<Double, Double> quantiles) {
    this.quantiles = quantiles;
  }
}
