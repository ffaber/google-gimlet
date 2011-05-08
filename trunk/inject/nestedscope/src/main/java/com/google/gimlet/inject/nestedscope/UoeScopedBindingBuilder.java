// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.Scope;
import com.google.inject.binder.ScopedBindingBuilder;

import java.lang.annotation.Annotation;

/**
 * This class is a simple implementation of {@link ScopedBindingBuilder}
 * that throws an {@link UnsupportedOperationException} in each of its
 * implemented methods.
 *
 * @author ffaber@google.com (Fred Faber)
*/
class UoeScopedBindingBuilder implements ScopedBindingBuilder {

  @Override public void asEagerSingleton() {
    throw new UnsupportedOperationException();
  }

  @Override public void in(Scope scope) {
    throw new UnsupportedOperationException();
  }

  @Override public void in(Class<? extends Annotation> scopeAnnotation) {
    throw new UnsupportedOperationException();
  }
}
