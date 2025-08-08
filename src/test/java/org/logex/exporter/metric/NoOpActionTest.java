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

class NoOpActionTest {

  private NoOpAction action;
  private LabelConfig config;

  @BeforeEach
  void setUp() {
    action = new NoOpAction();
    config = new LabelConfig(); // Config is not used by NoOpAction, but passed for consistency
  }

  @Test
  void testProcessReturnsSameValue() {
    String testValue = "some_value";
    String result = action.process(testValue, config);
    assertEquals(testValue, result);
  }

  @Test
  void testProcessReturnsNullForNullInput() {
    String result = action.process(null, config);
    assertEquals(null, result);
  }

  @Test
  void testProcessReturnsEmptyStringForEmptyStringInput() {
    String testValue = "";
    String result = action.process(testValue, config);
    assertEquals(testValue, result);
  }
}
