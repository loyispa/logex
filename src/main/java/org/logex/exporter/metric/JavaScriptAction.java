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

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.logex.exporter.config.LabelConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaScriptAction implements LabelAction {
  private static final Logger logger = LoggerFactory.getLogger(JavaScriptAction.class);

  @Override
  public String process(String value, LabelConfig config) {
    if (config.getScript() == null || config.getScript().isEmpty()) {
      return value;
    }

    try (Context context = Context.create("js")) {
      Value jsFunction = context.eval("js", "(function(value) { " + config.getScript() + " })");
      Value result = jsFunction.execute(value);
      if (result.isNull()) {
        logger.warn("JavaScript action did not return a string value.");
        return value;
      } else {
        return result.toString();
      }
    } catch (Exception e) {
      logger.error("Error executing JavaScript action", e);
      return value;
    }
  }
}
