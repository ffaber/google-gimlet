// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.common.collect.Maps;
import com.google.inject.BindingAnnotation;
import com.google.inject.Key;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * This class is a simple container that holds a set of keyed values. It is
 * called a <em>BindingFrame</em> because it represents a frame of bindings
 * (similar to a frame that is pushed on the call stack on method invocation).
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
final class BindingFrame {
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ ElementType.FIELD, ElementType.PARAMETER })
  @BindingAnnotation
  private @interface PrivateAnnotationForScopeIdKey{ }
  final static Key<ScopeId> SCOPE_ID_KEY =
      Key.get(ScopeId.class, PrivateAnnotationForScopeIdKey.class);

  private final Map<Key<?>, Object> scopedValues;

  /** Creates a new frame, using the given name. */
  BindingFrame() {
    scopedValues = Maps.newHashMap();
  }

  /**
   * Puts the given key, value pair into the frame and returns the value
   * previously associated with the given key.  This method returns {@code null}
   * if it is the value associated wtih the key <em>or</em> if no value is
   * associated with the key.
   */
  @SuppressWarnings({ "unchecked" })
  <T> T put(Key<T> key, T value) {
    return (T) scopedValues.put(key, value);
  }

  /** Returns a scoped value for the given key, if one exists */
  @SuppressWarnings({ "unchecked" })
  <T> T get(Key<T> key) {
    return (T) scopedValues.get(key);
  }

  @Override public String toString() {
    return "BindingFrame consists of:\n" + scopedValues.toString();
  }
}
