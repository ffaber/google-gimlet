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

package com.google.gimlet.batching;

import com.google.common.base.Objects;

/**
 * This class is a container for a {@link Batch} that has a key associated with
 * it.
 *
 */
public final class KeyedBatch<K,V> {

  private final K key;
  private final Batch<? extends V> batch;

  private KeyedBatch(K key, Batch<? extends V> batch) {
    this.key = key;
    this.batch = batch;
  }

  public static <K,V> KeyedBatch<K,V> of(K key, Batch<? extends V> batch) {
    return new KeyedBatch<K,V>(key, batch);
  }

  public K getKey() {
    return key;
  }

  public Batch<? extends V> getBatch() {
    return batch;
  }

  @Override public int hashCode() {
    return Objects.hashCode(key, batch);
  }

  @Override public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (!(o instanceof KeyedBatch)) {
      return false;
    }

    KeyedBatch that = (KeyedBatch) o;
    return Objects.equal(key, that.key)
        && Objects.equal(batch, that.batch);
  }

  @Override public String toString() {
    return Objects.toStringHelper(getClass().getSimpleName())
        .add("key", key)
        .add("batch", batch)
        .toString();
  }
}
