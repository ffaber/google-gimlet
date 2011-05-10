// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertEmpty;

import com.google.common.collect.Maps;

import junit.framework.TestCase;

import java.util.Map;

/**
 * Tests the {@link ExistingKeyMap} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class ExistingKeyMapTest extends TestCase {

  Map<Integer, String> backingMap;
  ExistingKeyMap<Integer, String> existingKeyMap;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    backingMap = Maps.newHashMap();
    existingKeyMap = new ExistingKeyMap<Integer, String>(backingMap);
  }

  public void testGet_withExistingKey() {
    existingKeyMap.put(1, "one");
    assertEquals("one", existingKeyMap.get(1));
  }

  public void testGet_withNonExistingKey() {
    assertEmpty(
        "No keys are expected in the backingMap",
        backingMap.keySet());
    try {
      existingKeyMap.get(1);
      fail("Expected a NullPointerException");
    } catch (NullPointerException npe) {
      // expected
    }
  }
}