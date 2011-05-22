// Copyright 2011 Google Inc.  All Rights Reserved

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