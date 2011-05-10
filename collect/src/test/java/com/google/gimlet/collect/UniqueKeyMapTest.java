// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import junit.framework.TestCase;

import java.util.Map;

/**
 * Tests the {@link UniqueKeyMap} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class UniqueKeyMapTest extends TestCase {

  Map<Integer, String> backingMap;
  UniqueKeyMap<Integer, String> uniqueKeyMap;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    backingMap = Maps.newHashMap();
    uniqueKeyMap = new UniqueKeyMap<Integer, String>(backingMap);
  }

  public void testPut_withUniqueKey() {
    uniqueKeyMap.put(1, "one");
    assertEquals("one", uniqueKeyMap.get(1));
  }

  public void testPut_withDuplicateKey() {
    backingMap.put(1, "one");
    try {
      uniqueKeyMap.put(1, "one");
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

  public void testPutAll_withUniqueKey() {
    Map<Integer, String> mapToCopy = ImmutableMap.of(1, "one_copy");
    uniqueKeyMap.putAll(mapToCopy);
    assertEquals("one_copy", uniqueKeyMap.get(1));
  }

  public void testPutAll_withDuplicateKey() {
    backingMap.put(1, "one");
    Map<Integer, String> mapToCopy = ImmutableMap.of(1, "one_copy");
    try {
      uniqueKeyMap.putAll(mapToCopy);
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }
}
