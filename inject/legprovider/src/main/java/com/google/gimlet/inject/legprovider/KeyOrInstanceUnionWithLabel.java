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
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

import java.lang.reflect.ParameterizedType;

import javax.annotation.Nullable;

/**
 * This class represents a C-style union that contains either a valid
 * {@link Key} or a valid instance, with a string label.  The label can't be
 * null, but it can be empty.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
final class KeyOrInstanceUnionWithLabel<T> {
  private static final String DEFAULT_LABEL_VALUE = Foot.DEFAULT_FOOT_LABEL;

  final String label;
  final TypeLiteral<T> typeLiteral;
  @Nullable final Key<T> key;
  @Nullable final T instance;

  static <T> KeyOrInstanceUnionWithLabel<T> ofKey(Key<T> key) {
    return ofKey(key, DEFAULT_LABEL_VALUE);
  }

  static <T> KeyOrInstanceUnionWithLabel<T> ofKey(Key<T> key, String label) {
    return new KeyOrInstanceUnionWithLabel<T>(
        key, null, key.getTypeLiteral(), label);
  }

  static <T> KeyOrInstanceUnionWithLabel<T> ofInstance(T instance) {
    return ofInstance(instance, DEFAULT_LABEL_VALUE);
  }

  static <T> KeyOrInstanceUnionWithLabel<T> ofInstance(
      T instance, TypeLiteral<T> typeLiteral) {
    return ofInstance(instance, typeLiteral, DEFAULT_LABEL_VALUE);
  }

  static <T> KeyOrInstanceUnionWithLabel<T> ofInstance(
      T instance, String label) {
    return new KeyOrInstanceUnionWithLabel<T>(
        null, instance, inferTypeLiteral(instance), label);
  }

  static <T> KeyOrInstanceUnionWithLabel<T> ofInstance(
      T instance, TypeLiteral<T> typeLiteral, String label) {
    return new KeyOrInstanceUnionWithLabel<T>(
        null, instance, typeLiteral, label);
  }

  private KeyOrInstanceUnionWithLabel(
      @Nullable Key<T> key, @Nullable T instance,
      TypeLiteral<T> typeLiteral, String label) {
    if (key == null && instance == null
        || key != null && instance != null) {
      throw new IllegalArgumentException(String.format(
          "Exactly one of [%s] and [%s] may be null", key, instance));
    }

    this.label = label;
    this.typeLiteral = typeLiteral;
    this.key = key;
    this.instance = instance;
  }

  @SuppressWarnings("unchecked") // we're casting to TypeLiteral<T>
  private static <T> TypeLiteral<T> inferTypeLiteral(T instance) {
    if (instance.getClass().getTypeParameters().length == 0) {
      return (TypeLiteral<T>) TypeLiteral.get(instance.getClass());
    }

    Class<?> parent = instance.getClass().getEnclosingClass();
    final ParameterizedType actualType = Types.newParameterizedTypeWithOwner(
        parent,
        instance.getClass(),
        instance.getClass().getTypeParameters());
    return (TypeLiteral<T>) TypeLiteral.get(actualType);
  }

  KeyOrInstanceUnionWithLabel<T> cloneAndAddLabel(String label) {
    return new KeyOrInstanceUnionWithLabel<T>(
        key, instance, typeLiteral, label);
  }

  TypeLiteral<T> getTypeLiteral() {
    return typeLiteral;
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof KeyOrInstanceUnionWithLabel)) {
      return false;
    }

    KeyOrInstanceUnionWithLabel that = (KeyOrInstanceUnionWithLabel) o;

    return Objects.equal(this.label, that.label)
        && Objects.equal(this.key, that.key)
        && Objects.equal(this.instance, that.instance);
  }

  @Override public int hashCode() {
    return Objects.hashCode(label, key, instance);
  }

  @Override public String toString() {
    return Objects.toStringHelper(this)
        .add("label", label)
        .add("key", key)
        .add("instance", instance)
        .toString();
  }

}
