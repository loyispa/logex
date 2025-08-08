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

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.logex.exporter.config.AppConfig;
import org.logex.exporter.config.LabelConfig;
import org.logex.exporter.config.MetricConfig;
import org.logex.exporter.log.LogHandler;
import org.logex.exporter.log.LogTailerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Manages Prometheus metrics, including registration and starting the HTTP server. */
public class MetricsManager {
  private static final Logger LOG = LoggerFactory.getLogger(MetricsManager.class);
  private final CollectorRegistry registry;
  private final AppConfig config;
  private final List<LogHandler> logHandlers = new ArrayList<>();

  public MetricsManager(AppConfig config, LogTailerManager tailer) {
    this.config = config;
    this.registry = new CollectorRegistry();
    for (MetricConfig metric : config.getMetrics()) {
      // Ensure labels list is not null
      if (metric.getLabels() == null) {
        metric.setLabels(new ArrayList<>());
      }

      // Check if 'path' label already exists
      boolean pathLabelExists =
          metric.getLabels().stream().anyMatch(label -> "path".equals(label.getName()));

      // If 'path' label does not exist, add it
      if (!pathLabelExists) {
        LabelConfig pathLabel = new LabelConfig();
        pathLabel.setName("path");
        pathLabel.setSourceField("path");
        pathLabel.setAction("NO_OP"); // Assuming NO_OP is a valid action for direct mapping
        metric.getLabels().add(pathLabel);
      }

      LogHandler handler = LogHandlerFactory.createHandler(metric, registry);
      tailer.register(metric.getMatch(), handler);
      logHandlers.add(handler);
    }
  }

  public void start() throws IOException {
    HTTPServer server = new HTTPServer(new InetSocketAddress(config.getPort()), registry);
    LOG.info("Listening on :{}", server.getPort());
  }
}
