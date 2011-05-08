// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.Provider;

/**
 * This class represents a provider that retrieves its value from a given {@link
 * NestedScope}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class NestedScopeBasedProvider<T> implements Provider<T> {

  @Override public T get() {
    throw new UnsupportedOperationException("Provider should never be called");
  }
}
