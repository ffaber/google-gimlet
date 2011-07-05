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
