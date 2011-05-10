// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertContentsAnyOrder;
import static com.google.gimlet.testing.tl4j.JUnitAsserts.assertEmpty;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the {@link DefaultingMaps} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class DefaultingMapsTest extends TestCase {

  public void testNewListValuedMap_useDefaultValue() {
    Map<Integer, List<String>> integersToStringLists =
        DefaultingMaps.newListValuedMap();

    List<String> stringList = integersToStringLists.get(1);
    assertEmpty(stringList);

    stringList.add("one");
    stringList = integersToStringLists.get(1);
    assertContentsAnyOrder(stringList, "one");
  }

  public void testNewListValuedMap_useInsertedValue() {
    Map<Integer, List<String>> integersToStringLists =
        DefaultingMaps.newListValuedMap();

    List<String> insertedList = Lists.newArrayList("one");
    integersToStringLists.put(1, insertedList);

    List<String> retrievedList = integersToStringLists.get(1);
    assertEquals(insertedList, retrievedList);

    insertedList.add("one_again");
    retrievedList = integersToStringLists.get(1);
    assertContentsAnyOrder(retrievedList, "one", "one_again");
  }

  public void testNewListValuedMap_withOriginalMap() {
    Map<Integer, List<String>> originalMap =
        ImmutableMap.<Integer, List<String>>of(1, ImmutableList.of("a"));
    Map<Integer, List<String>> defaultingMap =
        DefaultingMaps.newListValuedMap(originalMap);
    assertEquals("a", defaultingMap.get(1).get(0));

    // the fact that that we can get and then add an element means we are
    // now defaulting unknown keys.
    defaultingMap.get(2).add("b");
    assertEquals("b", defaultingMap.get(2).get(0));
  }

  public void testDoubleKeyedValuedMap() {
    Map<Integer, Map<Integer, String>> map =
        DefaultingMaps.newDoubleKeyedValueMap();

    // getting from the empty map should get us a default map, to which we
    // can put in stuff.
    map.get(1).put(2, "2");
    assertEquals("2", map.get(1).get(2));

    // try to get something from the "inner map" should return a null value
    // when we haven't inserted the key.
    assertNull(map.get(1).get(1));

    // we should be able to change the value of the inner map for a given key
    map.get(1).put(2, "3");
    assertEquals("3", map.get(1).get(2));
  }

  public void testDoubleKeyedDefaultingValueMap() {
    Map<Integer, Map<Integer, String>> map =
        DefaultingMaps.newDoubleKeyedDefaultingValueMap(
            new Function<Integer, String>() {
              @Override
              public String apply(Integer obj) {
                return "stringValue";
              }
            });

    // test that getting a non-existing key yields the seed value
    assertEquals("stringValue", map.get(1).get(1));

    // test that the value can be modified
    map.get(1).put(1, "newStringValue");
    assertEquals("newStringValue", map.get(1).get(1));
  }

  public void testNewDoubleListValuedMap_useDefaultValue() {
    Map<Integer, Map<Integer, List<String>>> integerToIntegerToStringLists =
        DefaultingMaps.newDoubleKeyedListValuedMap();

    List<String> stringList = integerToIntegerToStringLists.get(1).get(1);

    stringList.add("one-one");
    stringList = integerToIntegerToStringLists.get(1).get(1);
    assertContentsAnyOrder(stringList, "one-one");

    List<String> emptyList = integerToIntegerToStringLists.get(1).get(2);
    assertEmpty(emptyList);
  }

  public void testNewDoubleListValuedMap_useInsertedValue() {
    Map<Integer, Map<Integer, List<String>>> integerToIntegerToStringLists =
        DefaultingMaps.newDoubleKeyedListValuedMap();

    List<String> insertedList = Lists.newArrayList("one-one");
    ImmutableMap<Integer, List<String>> secondLevelMap =
        ImmutableMap.of(1, insertedList);
    integerToIntegerToStringLists.put(1, secondLevelMap);

    List<String> retrievedList = integerToIntegerToStringLists.get(1).get(1);
    assertEquals(insertedList, retrievedList);

    insertedList.add("one-one_again");
    retrievedList = integerToIntegerToStringLists.get(1).get(1);
    assertContentsAnyOrder(retrievedList, "one-one", "one-one_again");
  } 

  public void testNewTripleListValuedMap_useDefaultValue() {
    Map<Integer, Map<Integer, Map<Integer, List<String>>>>
        integerToIntegerToIntegerToStringLists =
        DefaultingMaps.newTripleKeyedListValuedMap();

    List<String> stringList =
        integerToIntegerToIntegerToStringLists.get(1).get(1).get(1);

    stringList.add("one-one-one");
    stringList = integerToIntegerToIntegerToStringLists.get(1).get(1).get(1);
    assertContentsAnyOrder(stringList, "one-one-one");

    List<String> emptyList =
        integerToIntegerToIntegerToStringLists.get(1).get(1).get(2);
    assertEmpty(emptyList);
  }

  public void testNewTripleListValuedMap_useInsertedValue() {
    Map<Integer, Map<Integer, Map<Integer, List<String>>>>
        integerToIntegerToIntegerToStringLists =
        DefaultingMaps.newTripleKeyedListValuedMap();

    List<String> insertedList = Lists.newArrayList("one-one-one");
    Map<Integer, List<String>> thirdLevelMap =
        ImmutableMap.of(1, insertedList);
    Map<Integer, Map<Integer, List<String>>> secondLevelMap =
        ImmutableMap.of(1, thirdLevelMap);
    integerToIntegerToIntegerToStringLists.put(1, secondLevelMap);

    List<String> retrievedList =
        integerToIntegerToIntegerToStringLists.get(1).get(1).get(1);
    assertEquals(insertedList, retrievedList);

    insertedList.add("one-one-one_again");
    retrievedList = integerToIntegerToIntegerToStringLists.get(1).get(1).get(1);
    assertContentsAnyOrder(retrievedList, "one-one-one", "one-one-one_again");
  }

  public void testNewSetValuedMap_useDefaultValue() {
    Map<Integer, Set<String>> integerToStringSets =
        DefaultingMaps.newSetValuedMap();

    Set<String> stringSet = integerToStringSets.get(1);

    stringSet.add("one");
    stringSet = integerToStringSets.get(1);
    assertContentsAnyOrder(stringSet, "one");

    Set<String> emptySet = integerToStringSets.get(2);
    assertEmpty(emptySet);
  }

  public void testNewSetValuedMap_useInsertedValue() {
    Map<Integer, Set<String>> integerToStringSets =
        DefaultingMaps.newSetValuedMap();

    Set<String> insertedSet = Sets.newHashSet("one");
    integerToStringSets.put(1, insertedSet);

    Set<String> retrievedSet = integerToStringSets.get(1);
    assertEquals(insertedSet, retrievedSet);

    insertedSet.add("one_again");
    retrievedSet = integerToStringSets.get(1);
    assertContentsAnyOrder(retrievedSet, "one", "one_again");
  }

  public void testUniqueKeyExistingMap() {
    Map<Integer, Map<Integer, String>> map =
        DefaultingMaps.newExistingUniqueKeyMapValuedMap();

    // getting from the empty map should get us a default map, in which we
    // can put stuff.
    map.get(1).put(2, "2");
    assertEquals("2", map.get(1).get(2));

    try {
      map.get(1).get(1);
      fail("Expected an exception since 1 doesn't exist in the inner map");
    } catch (Exception e) {
    }

    try {
      map.get(1).put(2, "3");
      fail("Expected an exception since we are trying to put a duplicate 2 in"
          + "the inner map");
    } catch (Exception e) {
    }
  }

  public void testMakeEmptyDefaultMap() {
    ImmutableMap<String, List<Integer>> map =
        DefaultingMaps.newSeededListValuedMap(ImmutableSet.of("a", "b"));

    assertContentsAnyOrder(map.keySet(), "a", "b");
    assertNull(map.get("c"));

    map.get("a").add(1);
    assertEquals(1, Iterables.getOnlyElement(map.get("a")).intValue());
  }
}