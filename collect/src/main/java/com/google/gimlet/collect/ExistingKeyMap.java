// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import com.google.common.collect.ForwardingMap;

import java.util.Map;

/**
 * This implementation of {@link Map} ensures that each {@link #get(Object)}
 * method requests the value for a key that exists in the map.  If such a key
 * does not exist, then a {@link NullPointerException} is thrown.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class ExistingKeyMap<K, V> extends ForwardingMap<K, V> {

  private final Map<K, V> backingMap;

  ExistingKeyMap(Map<K, V> backingMap) {
    this.backingMap = backingMap;
  }

  @Override
  protected Map<K, V> delegate() {
    return backingMap;
  }

  @Override
  public V get(Object key) {
    if (!backingMap.containsKey(key)) {
      throw new NullPointerException(String.format(
          "Key %s not found in %s", key, backingMap));
    }
    return backingMap.get(key);
  }
}
