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

public class JavaScriptActionTest {

  private JavaScriptAction javaScriptAction;

  @BeforeEach
  public void setUp() {
    javaScriptAction = new JavaScriptAction();
  }

  @Test
  public void testProcess_withValidScript_shouldReturnTransformedValue() {
    LabelConfig config = new LabelConfig();
    config.setScript("return value.toUpperCase();");
    String result = javaScriptAction.process("test", config);
    assertEquals("TEST", result);
  }

  @Test
  public void testProcess_withComplexScript_shouldReturnCorrectValue() {
    LabelConfig config = new LabelConfig();
    config.setScript("return value.split('-')[1];");
    String result = javaScriptAction.process("data-123", config);
    assertEquals("123", result);
  }

  @Test
  public void testProcess_withNullScript_shouldReturnOriginalValue() {
    LabelConfig config = new LabelConfig();
    config.setScript(null);
    String result = javaScriptAction.process("test", config);
    assertEquals("test", result);
  }

  @Test
  public void testProcess_withEmptyScript_shouldReturnOriginalValue() {
    LabelConfig config = new LabelConfig();
    config.setScript("");
    String result = javaScriptAction.process("test", config);
    assertEquals("test", result);
  }

  @Test
  public void testProcess_withScriptError_shouldReturnOriginalValue() {
    LabelConfig config = new LabelConfig();
    config.setScript("return value.nonExistentMethod();");
    String result = javaScriptAction.process("test", config);
    assertEquals("test", result);
  }

  @Test
  public void testProcess_withNonStringReturnValue_shouldReturnOriginalValue() {
    LabelConfig config = new LabelConfig();
    config.setScript("return 123;");
    String result = javaScriptAction.process("test", config);
    assertEquals("123", result);
  }
}
