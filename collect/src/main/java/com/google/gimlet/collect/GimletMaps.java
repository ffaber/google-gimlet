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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains utility functions for dealing with Maps.
 *
 */
public final class GimletMaps {
  private GimletMaps() { }

  /**
   * Returns a map that ensures a key exists for every {@link Map#get(Object)}
   * operation, and that uses a new {@code HashMap} as the underlying source of
   * data.  If a requested key does not exist in the backing map, then a
   * {@link NullPointerException} is thrown.
   */
  public static <K, V> Map<K, V> newEnsureKeyExistsMap() {
    Map<K, V> backingMap = Maps.newHashMap();
    return GimletMaps.newEnsureKeyExistsMap(backingMap);
  }

  /**
   * Returns a map that ensures a key exists for every {@link Map#get(Object)}
   * operation, and that uses the given map as the underlying source of data.
   * If a requested key does not exist in the backnig map, then a
   * {@link NullPointerException} is thrown.
   */
  public static <K, V> Map<K, V> newEnsureKeyExistsMap(Map<K, V> protectedMap) {
    return new ExistingKeyMap<K, V>(protectedMap);
  }

  /**
   * Returns a map that ensures that no duplicate keys are inserted into it.
   * If a duplicate insertion is detected, then a
   * {@link IllegalArgumentException} is thrown.  "Insertion" is defined as
   * an invocation of {@link Map#put(Object, Object)} or
   * {@link Map#putAll(Map)}.
   */
  public static <K, V> Map<K, V> newUniqueKeyMap() {
    return new UniqueKeyMap<K, V>(Maps.<K, V>newHashMap());
  }

  /**
   * Returns a map that ensures that no duplicate keys are inserted into it.
   * The given map is used as the underlying data source.  If a duplicate
   * insertion is detected, then a {@link IllegalArgumentException} is thrown.
   * "Insertion" is defined as an invocation of {@link Map#put(Object, Object)}
   * or  {@link Map#putAll(Map)}.
   */
  public static <K, V> Map<K, V> newUniqueKeyMap(Map<K, V> backingMap) {
    return new UniqueKeyMap<K, V>(backingMap);
  }

  /**
   * Returns a map that ensures that no duplicates are inserted as well as
   * making sure than on every get, the key exists.
   * @see #newUniqueKeyMap()
   * @see #newEnsureKeyExistsMap()
   */
  public static <K,V> Map<K,V> newExistingUniqueKeyMap() {
    return GimletMaps.newEnsureKeyExistsMap(
        GimletMaps.<K, V>newUniqueKeyMap());
  }

  /**
   * Returns a map that ensures that no duplicates are inserted as well as
   * making sure than on every get, the key exists. Uses the given map as a
   * source of data.
   * @see #newUniqueKeyMap()
   * @see #newEnsureKeyExistsMap()
   */
  public static <K,V> Map<K,V> newExistingUniqueKeyMap(Map<K,V> backingMap) {
    return GimletMaps.newUniqueKeyMap(
        GimletMaps.<K, V>newEnsureKeyExistsMap(backingMap));
  }

  /**
   * Merges an iterable of maps of key to list of values, such that for
   * the overlapping keys in the maps, the value is combined into a single
   * list. 
   */
  public static <K, V> Map<K, List<V>> mergeMaps(
      Iterable<Map<K, List<V>>> mapsToMerge) {
    Map<K, List<V>> finalMap = GimletMaps.newUniqueKeyMap(
        DefaultingMaps.<K, V>newListValuedMap());

    for (Map<K, List<V>> integerListMap : mapsToMerge) {
      for (Entry<K, List<V>> integerListEntry : integerListMap
          .entrySet()) {
        finalMap.get(integerListEntry.getKey()).addAll(
            integerListEntry.getValue());
      }
    }
    return finalMap;
  }

  /**
   * Look at {@link #mergeMaps(Iterable)} for more info.
   */
  public static <K, V> Map<K, List<V>> mergeMaps(
      Map<K, List<V>>... mapsToMerge) {
    return mergeMaps(ImmutableList.copyOf(mapsToMerge));
  }

  /**
   * Converts a List valued Map into an ImmutableMap valued by ImmutableLists.
   */
  public static <K,V> ImmutableMap<K, ImmutableList<V>> makeImmutable(
      Map<K, List<V>> map) {
    Builder<K, ImmutableList<V>> builder = ImmutableMap.builder();
    for (Entry<K, List<V>> entry : map.entrySet()) {
      builder.put(entry.getKey(), ImmutableList.copyOf(entry.getValue()));
    }
    return builder.build();
  }
}
