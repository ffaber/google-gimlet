/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.gimlet.testing.tl4j;

import com.google.common.collect.ImmutableList;

import junit.framework.TestCase;

import java.util.List;

/**
 * Tests the {@link GimletAssertsTest} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class GimletAssertsTest extends TestCase {

  public void testRenderContentsComparisonFailedMessage_noDiffsFound() {
    List<String> actualItems = ImmutableList.of("1");
    String actualMessage =
        GimletAsserts.renderContentsComparisonFailedMessage(actualItems, 1);
    String expectedMessage = ""
        + "Comparison failed.\n"
        + "Expected:\n"
        + "1\n"
        + "Actual:\n"
        + "1\n"
        + "(no identifyable differences found)";
    assertEquals(expectedMessage, actualMessage);
  }

  public void testRenderContentsComparisonFailedMessage_diffsFound() {
    List<String> actualItems = ImmutableList.of("a", "b", "c");
    String actualMessage =
        GimletAsserts.renderContentsComparisonFailedMessage(actualItems, "b");
    String expectedMessage = ""
        + "Comparison failed.\n"
        + "Expected:\n"
        + "b\n"
        + "Actual:\n"
        + "a\n"
        + "b\n"
        + "c\n"
        + "The expected list had size 1 but the actual had size 3.\n"
        + "The differences in elements was: "
        + "expected:<...b...> but was:<...a, b, c...>\n";
    assertEquals(expectedMessage, actualMessage);
  }
}