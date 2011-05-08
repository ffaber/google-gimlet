// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.inject.legprovider;

import com.google.common.base.Objects;
import com.google.inject.Key;

/**
 * A container that holds a {@link Key} and its associated string label.
 *
 */
final class LabeledKey {

  private final Key<?> key;
  private final String label;

  static LabeledKey of(Key<?> key, String label) {
    return new LabeledKey(key, label);
  }

  private LabeledKey(Key<?> key, String label) {
    this.key = key;
    this.label = label;
  }

  public Key<?> getKey() {
    return key;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof LabeledKey)) {
      return false;
    }

    LabeledKey p = (LabeledKey) o;

    return Objects.equal(key, p.key) && Objects.equal(label, p.label);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, label);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("key", key)
        .add("label", label)
        .toString();
  }
}
