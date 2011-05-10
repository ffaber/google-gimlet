// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ForwardingMap;

import java.util.Map;

/**
 * This implementation of {@link Map} ensures that no duplicate keys are
 * inserted into its backing map.  If a duplicate key is inserted, an
 * {@link IllegalArgumentException} is thrown.
 *
 * @author ffaber@gmail.com (Fred Faber)
*/
class UniqueKeyMap<K, V> extends ForwardingMap<K, V> {

  private final Map<K, V> backingMap;

  UniqueKeyMap(Map<K, V> backingMap) {
    this.backingMap = backingMap;
  }

  @Override
  public V put(K key, V value) {
    V originalValue = super.put(key, value);
    checkArgument(originalValue == null,
        "Map already contained a value for key: %s which was: %s when trying"
            + " to insert %s", key, originalValue, value);

    // will always be null
    return originalValue;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> map) {
    for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  protected Map<K, V> delegate() {
    return backingMap;
  }
}
