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
package org.logex.exporter.log;

import io.krakens.grok.api.Grok;
import io.krakens.grok.api.GrokCompiler;
import io.krakens.grok.api.Match;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses log lines based on a specified format and extracts named capture groups using Java-Grok
 * library.
 */
public class LogParser {
  private static final Logger LOG = LoggerFactory.getLogger(LogParser.class);

  private final Grok grok;

  public LogParser(String matchFormat, Map<String, String> globalPatterns) {
    GrokCompiler grokCompiler = GrokCompiler.newInstance();
    grokCompiler.registerDefaultPatterns();

    // Add custom patterns from config
    if (globalPatterns != null) {
      for (Map.Entry<String, String> entry : globalPatterns.entrySet()) {
        grokCompiler.register(entry.getKey(), entry.getValue());
      }
    }

    // Compile the grok pattern
    LOG.debug("Before parse: {}", matchFormat);
    this.grok = grokCompiler.compile(matchFormat);
    LOG.debug("After  parse: {}", grok.getNamedRegex());
  }

  public Map<String, String> parse(String line) {
    Match gm = grok.match(line);

    if (gm.isNull()) {
      return Collections.emptyMap();
    }
    Map<String, Object> capture = gm.capture();

    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : capture.entrySet()) {
      if (entry.getValue() != null) {
        result.put(entry.getKey(), entry.getValue().toString());
      }
    }
    return result;
  }
}
