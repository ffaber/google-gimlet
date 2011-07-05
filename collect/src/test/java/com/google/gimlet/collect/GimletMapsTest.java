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

import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertContentsAnyOrder;
import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertEmpty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

/**
 * Tests {@link GimletMaps}.
 *
 */
public class GimletMapsTest extends TestCase {

  public void testNewEnsureKeyExistsMap() throws Exception {
    String key = "a";
    String value = "b";
    String nullKey = "c";
    Map<String, String> map = ImmutableMap.of(key, value);
    Map<String, String> decoratedMap =
        GimletMaps.newEnsureKeyExistsMap(map);
    assertEquals(value, decoratedMap.get(key));
    assertFalse(decoratedMap.containsKey(nullKey));
    try {
      decoratedMap.get(nullKey);
      fail("Expected a null pointer exception");
    } catch (NullPointerException e) {
      // expected the exception to happen;
    }
  }

  public void testNewEnsureKeyExistsMap_keyExists() {
    Map<Integer, String> integersToStrings =
        GimletMaps.newEnsureKeyExistsMap();
    integersToStrings.put(1, "one");
    String value = integersToStrings.get(1);
    assertEquals("one", value);
  }

  public void testNewEnsureKeyExistsMap_keyDoesNotExist() {
    Map<Integer, String> integersToStrings =
        GimletMaps.newEnsureKeyExistsMap();
    try {
      integersToStrings.get(1);
      fail("Expected a NullPointerException");
    } catch (NullPointerException npe) {
      // expected
    }
  }

  public void testNewEnsureKeyExistsMap_keyExistsInBackingMap() {
    Map<Integer, String> backingMap = ImmutableMap.of(1, "one");
    Map<Integer, String> integersToStrings =
        GimletMaps.newEnsureKeyExistsMap(backingMap);
    String value = integersToStrings.get(1);
    assertEquals("one", value);
  }

  public void testNewEnsureKeyExistsMap_keyDoesNotExistInBackingMap() {
    Map<Integer, String> backingMap = ImmutableMap.of();
    Map<Integer, String> integersToStrings =
        GimletMaps.newEnsureKeyExistsMap(backingMap);
    try {
      integersToStrings.get(1);
      fail("Expected a NullPointerException");
    } catch (NullPointerException npe) {
      // expected
    }
  }

  public void testNewUniqueKeyMap_withUniqueKey() {
    Map<Integer, String> uniqueKeyMap = GimletMaps.newUniqueKeyMap();
    uniqueKeyMap.put(1, "one");
    assertEquals("one", uniqueKeyMap.get(1));
  }

  public void testNewUniqueKeyMap_withNonUniqueKey() {
    Map<Integer, String> uniqueKeyMap = GimletMaps.newUniqueKeyMap();
    uniqueKeyMap.put(1, "one");
    try {
      uniqueKeyMap.put(1, "one_again");
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

  public void testNewUniqueKeyMap_withUniqueKeyInBackingMap() {
    Map<Integer, String> backingMap = Maps.newHashMap();
    Map<Integer, String> uniqueKeyMap =
        GimletMaps.newUniqueKeyMap(backingMap);
    uniqueKeyMap.put(1, "one");
    assertEquals("one", uniqueKeyMap.get(1));
  }

  public void testNewUniqueKeyMap_withNonUniqueKeyInBackingMap() {
    Map<Integer, String> backingMap = Maps.newHashMap();
    backingMap.put(1, "one");
    Map<Integer, String> uniqueKeyMap =
        GimletMaps.newUniqueKeyMap(backingMap);
    try {
      uniqueKeyMap.put(1, "one_again");
      fail("Expected an IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // expected
    }
  }

  public void testNewExistingUniqueKeyMap() {
    Map<Integer, String> map = GimletMaps.newExistingUniqueKeyMap();
    map.put(1, "one");
    assertEquals("one", map.get(1));
    try {
      map.get(2);
      fail("Expected a NullPointerException");
    } catch (NullPointerException e) {
      // expected
    }

    map.put(2, "two");
    try {
      map.put(1, "two");
      fail("Expected illegal argument exception");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  public void testMergeMaps() {
    Map<Integer, List<Integer>> map1 = DefaultingMaps.newListValuedMap();
    map1.get(1).add(1);
    map1.get(2).add(1);
    map1.get(2).add(2);
    map1.get(3).add(3);

    Map<Integer, List<Integer>> map2 = DefaultingMaps.newListValuedMap();
    map2.get(1).add(1);
    map2.get(2).add(3);

    Map<Integer, List<Integer>> map3 = DefaultingMaps.newListValuedMap();
    map2.get(4).add(5);

    Map<Integer, List<Integer>> map4 = GimletMaps.mergeMaps(map1, map2);
    // make sure the all the values exist correctly merged.
    assertEquals("Number of entries in final map", 4, map4.size());
    assertContentsAnyOrder(map4.get(1), 1, 1);
    assertContentsAnyOrder(map4.get(2), 1, 2, 3);
    assertContentsAnyOrder(map4.get(3), 3);
    assertContentsAnyOrder(map4.get(4), 5);
  }

  public void testMakeImmutable_empyMap() {
    Map<Integer, List<Integer>> map = DefaultingMaps.newListValuedMap();
    ImmutableMap<Integer, ImmutableList<Integer>> immutableMap =
        GimletMaps.makeImmutable(map);
    assertNull(immutableMap.get(1));
    assertEmpty(immutableMap.keySet());
    try {
      immutableMap.put(1, ImmutableList.<Integer>of());
      fail("Can't add to the map");
    } catch (UnsupportedOperationException expected) {
    }
  }

  public void testMakeImmutable() {
    Map<Integer, List<Integer>> map = DefaultingMaps.newListValuedMap();
    map.get(1).add(2);
    ImmutableMap<Integer, ImmutableList<Integer>> immutableMap =
        GimletMaps.makeImmutable(map);
    assertEquals(Integer.valueOf(2), immutableMap.get(1).get(0));
    assertNull(immutableMap.get(2));
    assertEquals(1, immutableMap.keySet().size());
    try {
      immutableMap.put(1, ImmutableList.<Integer>of());
      fail("Can't modify the the map");
    } catch (UnsupportedOperationException expected) {
    }

    try {
      immutableMap.remove(1);
      fail("Can't modify the the map");
    } catch (UnsupportedOperationException expected) {
    }

    try {
      immutableMap.get(1).add(3);
      fail("Can't modify the list");
    } catch (UnsupportedOperationException expected) {
    }

    try {
      immutableMap.get(1).remove(0);
      fail("Can't modify the list");
    } catch (UnsupportedOperationException expected) {
    }
  }
}
