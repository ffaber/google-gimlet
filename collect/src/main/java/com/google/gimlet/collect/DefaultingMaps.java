// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides convenience methods to create maps that use a default value when
 * no value exists for a given key.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class DefaultingMaps {
  private DefaultingMaps() { }

  private static abstract class ZeronaryFunction<T>
      implements Function<Object, T> {

    @Override
    public final T apply(Object from) {
      return create();
    }

    /** Creates something without needing any input arguments. */
    abstract T create();
  }

  private static final Function CREATE_ARRAY_LIST =
      new ZeronaryFunction<List>() {
        @Override
        List<?> create() {
          return Lists.newArrayList();
        }
      };

  /** Returns a map that uses an {@code ArrayList} as a default value. */
  @SuppressWarnings({ "unchecked" })
  public static <K, V> Map<K, List<V>> newListValuedMap() {
    return new MapMaker().makeComputingMap(
        (Function<K, List<V>>) CREATE_ARRAY_LIST);
  }

  /**
   * Converts the map into a defaulting map that uses an {@code ArrayList} as a
   * default value. Updates made to the original map, after this has been called
   * will not been seen by the returned map.
   */
  public static <K, V> Map<K, List<V>> newListValuedMap(
      Map<K, List<V>> originalMap) {
    Map<K, List<V>> newMap = newListValuedMap();
    newMap.putAll(originalMap);
    return newMap;
  }

  private static final Function CREATE_MAP =
      new ZeronaryFunction() {
        @Override
        Object create() {
          return Maps.newHashMap();
        }
      };

  /**
   * Returns a "double-keyed" map that allows one to have an empty map as a
   * default value for the outer map.
   */
  @SuppressWarnings({"unchecked"})
  public static <K1, K2, V> Map<K1, Map<K2, V>>
  newDoubleKeyedValueMap() {
    return new MapMaker().makeComputingMap(
        (Function<K1, Map<K2, V>>) CREATE_MAP);
  }

  /**
   * Returns a "double-keyed" map that uses the given defaulting function to set
   * values.
   */
  @SuppressWarnings({"unchecked"})
  public static <K1, K2, V> Map<K1, Map<K2, V>>
  newDoubleKeyedDefaultingValueMap(final Function<K2, V> defaultingFunction) {
    return new MapMaker().makeComputingMap(
        new Function<K1, Map<K2, V>>() {
          @Override
          public Map<K2, V> apply(K1 obj) {
            return new MapMaker().makeComputingMap(defaultingFunction);
          }
        });
  }

  private static final Function CREATE_LIST_VALUED_MAP =
      new ZeronaryFunction() {
        @Override
        Object create() {
          return newListValuedMap();
        }
      };

  /**
   * Returns a "double-keyed" map that uses a {@code ArrayList} as a default
   * value.
   */
  @SuppressWarnings({ "unchecked" })
  public static <K1, K2, V> Map<K1, Map<K2, List<V>>>
  newDoubleKeyedListValuedMap() {
    return new MapMaker().makeComputingMap(
        (Function<K1, Map<K2, List<V>>>) CREATE_LIST_VALUED_MAP);
  }

  private static final Function CREATE_DOUBLE_LIST_VALUED_MAP =
      new ZeronaryFunction() {
        @Override
        Object create() {
          return newDoubleKeyedListValuedMap();
        }
      };

  /**
   * Returns a "triple-keyed map" that uses a {@code ArrayList} as a default
   * value.
   */
  @SuppressWarnings({ "unchecked" })
  public static <K1, K2, K3, V> Map<K1, Map<K2, Map<K3, List<V>>>>
  newTripleKeyedListValuedMap() {
    return new MapMaker().makeComputingMap(
        (Function<K1, Map<K2, Map<K3, List<V>>>>)
            CREATE_DOUBLE_LIST_VALUED_MAP);
  }

  private static final Function CREATE_HASH_SET =
      new ZeronaryFunction<Set<?>>() {
        @Override Set<?> create() {
          return Sets.newHashSet();
        }
      };

  /** Returns a map that uses a {@code HashSet} as a default value. */
  @SuppressWarnings({ "unchecked" })
  public static <K, V> Map<K, Set<V>> newSetValuedMap() {
    return new MapMaker().makeComputingMap(
        (Function<K, Set<V>>) CREATE_HASH_SET);
  }

  @SuppressWarnings({ "unchecked" })
  public static <K> Map<K, AtomicLong> newAtomicLongValueMap() {
    return new MapMaker().makeComputingMap(
          new Function<K, AtomicLong>() {
            @Override
            public AtomicLong apply(K from) {
              return new AtomicLong(0L);
            }
          }
      );
  }

  @SuppressWarnings({ "unchecked" })
  public static <K> Map<K, AtomicInteger> newAtomicIntValuedMap() {
    return new MapMaker().makeComputingMap(
          new Function<K, AtomicInteger>() {
            @Override
            public AtomicInteger apply(K from) {
              return new AtomicInteger(0);
            }
          }
      );
  }

  public static <K> Map<K, AtomicBoolean> newAtomicBooleanValuedMap(
      final Boolean initialDefaultValue) {
    return new MapMaker().makeComputingMap(
          new Function<K, AtomicBoolean>() {
            @Override
            public AtomicBoolean apply(K from) {
              return new AtomicBoolean(initialDefaultValue);
            }
          }
      );
  }

  private static final Function CREATE_EXISTING_UNIQUE_KEY_MAP =
      new ZeronaryFunction() {
        @Override
        Object create() {
          return GimletMaps.newExistingUniqueKeyMap();
        }
      };

  /**
   * Returns a "double-keyed" map that allows one to have an empty map as a
   * default value for the outer map.  The inner maps that are returned are
   * "unique-key" maps, which follow the contract of
   * {@link GimletMaps#newExistingUniqueKeyMap()}.
   */
  @SuppressWarnings({"unchecked"})
  public static <K1, K2, V> Map<K1, Map<K2, V>>
  newExistingUniqueKeyMapValuedMap() {
    return new MapMaker().makeComputingMap(
        (Function<K1, Map<K2, V>>) CREATE_EXISTING_UNIQUE_KEY_MAP);
  }

  /**
   * Takes a set of keys, and puts them as keys into a immutable map with an
   * empty list as the value for the key. This list is mutable so that more
   * elements can be added to it.
   */
  @SuppressWarnings({"unchecked"})
  public static <K, V> ImmutableMap<K, List<V>> newSeededListValuedMap(
      Set<K> keys) {
    return newSeededMap(keys, CREATE_ARRAY_LIST);
  }

  /**
   * Takes a set of keys, and puts them as keys into a immutable map using the
   * provided function to create as the value for the key.
   */
  public static <K, V> ImmutableMap<K, V> newSeededMap(
      Set<K> keys, Function<K, V> function) {
    ImmutableMap.Builder<K, V> mapBuilder =
        ImmutableMap.builder();
    for (K key : keys) {
      mapBuilder.put(key, function.apply(key));
    }
    return mapBuilder.build();
  }
}
