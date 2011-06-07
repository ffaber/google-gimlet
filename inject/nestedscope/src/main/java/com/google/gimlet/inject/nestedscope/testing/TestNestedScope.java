// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope.testing;

import com.google.common.collect.Maps;
import com.google.gimlet.inject.nestedscope.NestedScope;
import com.google.gimlet.inject.nestedscope.ScopeId;
import com.google.inject.Key;
import com.google.inject.Provider;

import java.util.Map;

/**
 * This class provides a testing-based implementation of {@link NestedScope}. It
 * has identical functionality, but it doesn't care about {@link
 * #enter(ScopeId)}, {@link #enterNew()}, or {@link #exit()}, because these
 * methods are all no-ops.  As soon as an instance of this class is created,
 * scope is entered for the creating thread.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
// TODO(ffaber): restore functionality to this class
@Deprecated // to indicate to potential users that this class not operational
public class TestNestedScope implements NestedScope {

  private final ThreadLocal<Map<Key<?>, Object>> scopedObjects =
      new ThreadLocal<Map<Key<?>, Object>>();
  // private final SimpleScope delegateScope;

  public TestNestedScope() {
    // this.delegateScope = new SimpleScope();
    // delegateScope.enter();
    scopedObjects.set(Maps.<Key<?>, Object>newHashMap());
  }

  @Override
  public void enter(ScopeId scopeId) {
  }

  @Override
  public void enterNew() {
  }

  @Override
  public void exit() {
  }

  @Override
  public void enterNew(ScopeId scopeId) {
  }

  @Override
  public <T> void put(Key<T> key, T object) {
    scopedObjects.get().put(key, object);
    // delegateScope.seed(key, object);
  }

  @Override
  public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
    // return delegateScope.scope(key, unscoped);
    return null;
  }

  /** Returns all the objects scoped by the thread that created this instance */
  public Map<Key<?>, Object> getScopedObjects() {
    return scopedObjects.get();
  }
}
