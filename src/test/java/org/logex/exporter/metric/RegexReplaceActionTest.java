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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logex.exporter.config.LabelConfig;

class RegexReplaceActionTest {

  private RegexReplaceAction action;
  private LabelConfig config;

  @BeforeEach
  void setUp() {
    action = new RegexReplaceAction();
    config = new LabelConfig();
  }

  @Test
  void testProcessWithMatchingRegex() {
    config.setRegex("\\d+");
    config.setReplacement("NUM");
    String result = action.process("text123text", config);
    assertEquals("textNUMtext", result);
  }

  @Test
  void testProcessWithNoMatchingRegex() {
    config.setRegex("abc");
    config.setReplacement("XYZ");
    String result = action.process("text123text", config);
    assertEquals("text123text", result);
  }

  @Test
  void testProcessWithNullValue() {
    config.setRegex("\\d+");
    config.setReplacement("NUM");
    String result = action.process(null, config);
    assertEquals(null, result);
  }

  @Test
  void testProcessWithNullRegex() {
    config.setReplacement("NUM");
    String result = action.process("text123text", config);
    assertEquals("text123text", result);
  }

  @Test
  void testProcessWithNullReplacement() {
    config.setRegex("\\d+");
    String result = action.process("text123text", config);
    assertEquals("text123text", result);
  }

  @Test
  void testProcessWithEmptyRegex() {
    config.setRegex("");
    config.setReplacement("NUM");
    String result = action.process("text123text", config);
    assertEquals("text123text", result);
  }

  @Test
  void testProcessWithEmptyReplacement() {
    config.setRegex("\\d+");
    config.setReplacement("");
    String result = action.process("text123text", config);
    assertEquals("texttext", result);
  }

  @Test
  void testProcessWithCaptureGroup() {
    config.setRegex("(\\w+)-(\\w+)");
    config.setReplacement("$2-$1");
    String result = action.process("hello-world", config);
    assertEquals("world-hello", result);
  }
}
