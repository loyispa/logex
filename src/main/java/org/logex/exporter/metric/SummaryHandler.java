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
import io.prometheus.client.Summary;
import java.util.List;
import java.util.Map;
import org.logex.exporter.config.LabelConfig;
import org.logex.exporter.config.MetricConfig;
import org.logex.exporter.log.LogHandler;

public class SummaryHandler implements LogHandler {
  private final Summary summary;
  private final MetricConfig config;
  private final LabelCache<Summary.Child> labelCache;

  public SummaryHandler(MetricConfig config, CollectorRegistry registry) {
    this.config = config;
    List<String> labelNames = config.getLabels().stream().map(LabelConfig::getName).toList();
    Summary.Builder builder =
        Summary.build()
            .name(config.getName())
            .help(config.getHelp())
            .labelNames(labelNames.toArray(new String[0]));

    if (config.getQuantiles() != null && !config.getQuantiles().isEmpty()) {
      config.getQuantiles().forEach((quantile, error) -> builder.quantile(quantile, error));
    }

    this.summary = builder.register(registry);
    this.labelCache =
        new LabelCache<>(
            config.getMaxLabelSets(),
            config.getExpirationSeconds(),
            (labels) -> summary.remove(labels.toArray(new String[0])));
  }

  @Override
  public void handle(Map<String, String> data) {
    List<String> labelValues = LabelProcessor.processLabels(data, config);
    double value =
        (config.getValueField() != null && data.containsKey(config.getValueField()))
            ? Double.parseDouble(data.get(config.getValueField()))
            : 1.0;
    labelCache
        .getOrCreate(labelValues, () -> summary.labels(labelValues.toArray(new String[0])))
        .observe(value);
  }
}
