// Copyright 2011 Google Inc. All Rights Reserved.

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
