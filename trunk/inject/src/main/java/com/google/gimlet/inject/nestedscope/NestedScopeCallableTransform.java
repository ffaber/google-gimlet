// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.Callable;

/**
 * This class provides the functionality to create new instances of {@link
 * NestedScopeCallable}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
@Singleton
public class NestedScopeCallableTransform<T> {

  private final NestedScopeImpl nestedScopeImpl;

  @Inject NestedScopeCallableTransform(NestedScopeImpl nestedScopeImpl) {
    this.nestedScopeImpl = nestedScopeImpl;
  }

  /**
   * This method returns a new instance of a {@link NestedScopeCallable}. This
   * new callable is given the {@link NestedScopeImpl} that is passed into the
   * constructor of this factory class, and also the {@link Callable} that is
   * passed into this method as an argument.
   *
   * @param innerCallable the callable that is called by the {@link
   * NestedScopeCallable} returned by this method
   * @return a new instance of a {@link NestedScopeCallable}
   */
  public <T> Callable<T> apply(Callable<T> innerCallable) {
    return new NestedScopeCallable<T>(nestedScopeImpl, innerCallable);
  }
}
