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

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogParserTest {

  private Map<String, String> globalPatterns;

  @BeforeEach
  void setUp() {
    globalPatterns = new HashMap<>();
    // These are now custom patterns, as Java-Grok has its own built-in ones.
    globalPatterns.put("MY_WORD", "\\b\\w+\\b");
    globalPatterns.put("MY_NUMBER", "\\d+");
  }

  @Test
  void testParseWithBuiltInGrokPatterns() {
    // Test with built-in Grok patterns like IP, HTTPDATE, WORD
    String matchFormat =
        "%{IPORHOST:client_ip} - - \\[%{HTTPDATE:timestamp}\\] \"%{WORD:method} %{URIPATH:request} HTTP/%{NUMBER:http_version}\" %{NUMBER:status} %{NUMBER:bytes}";
    LogParser parser = new LogParser(matchFormat, globalPatterns);
    Map<String, String> result =
        parser.parse(
            "192.168.1.1 - - [07/Aug/2025:10:00:00 +0800] \"GET /index.html HTTP/1.1\" 200 1234");

    assertNotNull(result);
    assertEquals("192.168.1.1", result.get("client_ip"));
    assertEquals("07/Aug/2025:10:00:00 +0800", result.get("timestamp"));
    assertEquals("GET", result.get("method"));
    assertEquals("/index.html", result.get("request"));
    assertEquals("1.1", result.get("http_version"));
    assertEquals("200", result.get("status"));
    assertEquals("1234", result.get("bytes"));
  }

  @Test
  void testParseWithCustomPatterns() {
    // Test with custom patterns defined in globalPatterns
    String matchFormat = "My custom log: %{MY_WORD:word_field} %{MY_NUMBER:number_field}";
    LogParser parser = new LogParser(matchFormat, globalPatterns);
    Map<String, String> result = parser.parse("My custom log: hello 123");

    assertNotNull(result);
    assertEquals("hello", result.get("word_field"));
    assertEquals("123", result.get("number_field"));
  }

  @Test
  void testParseNoMatch() {
    String matchFormat = "%{IPORHOST:client_ip} %{WORD:message}";
    LogParser parser = new LogParser(matchFormat, globalPatterns);
    Map<String, String> result = parser.parse("");

    assertNotNull(result);
    assertNull(result.get("client_ip"));
    assertNull(result.get("message"));
  }

  @Test
  void testParseWithMissingPattern() {
    String matchFormat = "%{UNKNOWN_PATTERN:field}";
    assertThrows(
        IllegalArgumentException.class,
        () -> new LogParser(matchFormat, globalPatterns),
        "Pattern 'UNKNOWN_PATTERN' not found");
  }

  @Test
  void testParseWithNestedGrokPatterns() {
    // Java-Grok handles nesting automatically
    globalPatterns.put("MY_LOG_ENTRY", "%{IPORHOST:ip_addr}:%{NUMBER:port}");
    String matchFormat = "Connection from %{MY_LOG_ENTRY:connection_info}";
    LogParser parser = new LogParser(matchFormat, globalPatterns);
    Map<String, String> result = parser.parse("Connection from 192.168.1.1:8080");

    assertNotNull(result);
    assertEquals("192.168.1.1:8080", result.get("connection_info"));
  }
}
