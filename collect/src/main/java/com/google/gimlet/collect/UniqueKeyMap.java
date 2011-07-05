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
