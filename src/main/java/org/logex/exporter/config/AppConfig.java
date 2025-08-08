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

public class AppConfig {
  @JsonProperty("path")
  private String path;

  private Map<String, String> patterns;
  private List<MetricConfig> metrics;

  @JsonProperty("port")
  private int port = 9090;

  @JsonProperty("file_scan_interval_seconds")
  private int fileScanIntervalSeconds = 10; // Default to 10 seconds

  @JsonProperty("file_inactivity_timeout_seconds")
  private int fileInactivityTimeoutSeconds = 3600; // Default to 3600 seconds (1 hour)

  @JsonProperty("tail_from_end")
  private boolean tailFromEnd = false;

  // Getters and Setters
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Map<String, String> getPatterns() {
    return patterns;
  }

  public void setPatterns(Map<String, String> patterns) {
    this.patterns = patterns;
  }

  public List<MetricConfig> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<MetricConfig> metrics) {
    this.metrics = metrics;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getFileScanIntervalSeconds() {
    return fileScanIntervalSeconds;
  }

  public void setFileScanIntervalSeconds(int fileScanIntervalSeconds) {
    this.fileScanIntervalSeconds = fileScanIntervalSeconds;
  }

  public int getFileInactivityTimeoutSeconds() {
    return fileInactivityTimeoutSeconds;
  }

  public void setFileInactivityTimeoutSeconds(int fileInactivityTimeoutSeconds) {
    this.fileInactivityTimeoutSeconds = fileInactivityTimeoutSeconds;
  }

  public boolean isTailFromEnd() {
    return tailFromEnd;
  }

  public void setTailFromEnd(boolean tailFromEnd) {
    this.tailFromEnd = tailFromEnd;
  }
}
