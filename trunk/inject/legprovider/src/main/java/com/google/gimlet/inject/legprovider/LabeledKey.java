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

package com.google.gimlet.inject.legprovider;

import com.google.common.base.Objects;
import com.google.inject.Key;

/**
 * A container that holds a {@link Key} and its associated string label.
 *
 * @author andrei.g.matveev@gmail.com
 */
final class LabeledKey<T> {

  private final Key<T> key;
  private final String label;

  static <T> LabeledKey of(Key<T> key, String label) {
    return new LabeledKey<T>(key, label);
  }

  private LabeledKey(Key<T> key, String label) {
    this.key = key;
    this.label = label;
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
