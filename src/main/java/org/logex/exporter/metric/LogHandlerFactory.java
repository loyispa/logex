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
import java.util.HashMap;
import java.util.Map;
import org.logex.exporter.config.MetricConfig;
import org.logex.exporter.log.LogHandler;

public class LogHandlerFactory {
  private static final Map<String, Class<? extends LogHandler>> handlers = new HashMap<>();

  static {
    handlers.put("counter", CounterHandler.class);
    handlers.put("histogram", HistogramHandler.class);
    handlers.put("gauge", GaugeHandler.class);
    handlers.put("summary", SummaryHandler.class);
  }

  public static LogHandler createHandler(MetricConfig config, CollectorRegistry registry) {
    Class<? extends LogHandler> handlerClass = handlers.get(config.getType());
    if (handlerClass == null) {
      throw new IllegalArgumentException("Unsupported metric type: " + config.getType());
    }
    try {
      return handlerClass
          .getConstructor(MetricConfig.class, CollectorRegistry.class)
          .newInstance(config, registry);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to create metric handler for type: " + config.getType(), e);
    }
  }
}
