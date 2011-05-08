// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is a simple implementation of {@link ScopeId} that uses a given
 * value to determine equality with other scope ids.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class ValueBasedScopeId implements ScopeId {

  /** The non-null value that represents the identity of this scope id. */
  private final Object value;

  public static ScopeId of(Object value) {
    return new ValueBasedScopeId(value);
  }

  private ValueBasedScopeId(Object value) {
    this.value = checkNotNull(value);
  }

  @Override public boolean equals(Object that) {
    return ValueBasedScopeId.class.isInstance(that)
        && ((ValueBasedScopeId) that).value.equals(this.value);
  }

  @Override public int hashCode() {
    return value.hashCode();
  }

  @Override public String toString() {
    return "ValueBasedScopeId{" + value + "}";
  }
}
