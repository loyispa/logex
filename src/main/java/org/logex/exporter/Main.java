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
package org.logex.exporter;

import org.logex.exporter.config.AppConfig;
import org.logex.exporter.config.ConfigLoader;
import org.logex.exporter.log.LogTailerManager;
import org.logex.exporter.metric.MetricsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    LOG.info("Starting Exporter...");

    // 1. Load app config
    String configPath = args.length > 0 ? args[0] : "config.yml";
    LOG.info("Loading application configuration from: {}", configPath);
    AppConfig config = ConfigLoader.loadConfig(configPath);
    LOG.info("Application configuration loaded successfully.");

    // 2. Initialize components
    LOG.info("Initializing LogTailerManager...");
    LogTailerManager tailerManager = new LogTailerManager(config);
    LOG.info("Initializing MetricsManager...");
    MetricsManager metricsManager = new MetricsManager(config, tailerManager);

    // 3. Start components
    LOG.info("Starting LogTailerManager...");
    tailerManager.start();
    LOG.info("LogTailerManager started.");

    LOG.info("Starting MetricsManager...");
    metricsManager.start();
    LOG.info("MetricsManager started.");
  }
}
