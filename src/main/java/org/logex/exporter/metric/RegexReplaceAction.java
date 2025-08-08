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

import java.util.regex.Pattern;
import org.logex.exporter.config.LabelConfig;

public class RegexReplaceAction implements LabelAction {
  @Override
  public String process(String value, LabelConfig config) {
    if (value != null && config.getRegex() != null && config.getReplacement() != null) {
      if (config.getRegex().isEmpty()) {
        return value;
      }
      return Pattern.compile(config.getRegex()).matcher(value).replaceAll(config.getReplacement());
    }
    return value;
  }
}
