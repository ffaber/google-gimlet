// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper;

import com.google.inject.Provider;

import java.lang.annotation.Annotation;

/**
 * This class is an implementation of {@link Provider} which should be used
 * to offer a reasonably useful error message when its {@link #get()} method
 * is called, because it will always throw an
 * {@link UnsupportedOperationException}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class IntrospectingScopeOutOfScopeProvider implements Provider {

  private final Class target;
  private final Class<? extends Annotation> scopeAnnotationClass;

  IntrospectingScopeOutOfScopeProvider(
      Class target,
      Class<? extends Annotation> scopeAnnotationClass) {
    this.target = target;
    this.scopeAnnotationClass = scopeAnnotationClass;
  }

  @Override public Object get() {
    String msg = String.format(
        "Class %s expected to be bound in scope %s.",
        target.getSimpleName(), scopeAnnotationClass.getSimpleName());
    throw new UnsupportedOperationException(msg);
  }
}
