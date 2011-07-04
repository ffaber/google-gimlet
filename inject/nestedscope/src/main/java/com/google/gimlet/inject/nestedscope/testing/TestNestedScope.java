// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope.testing;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Maps;
import com.google.gimlet.inject.nestedscope.NestedScope;
import com.google.gimlet.inject.nestedscope.ScopeId;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class provides a testing-based implementation of {@link NestedScope}. It
 * has identical functionality, but it doesn't care about {@link
 * #enter(ScopeId)}, {@link #enterNew()}, or {@link #exit()}, because these
 * methods are all no-ops.  As soon as an instance of this class is created,
 * scope is entered for the creating thread.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class TestNestedScope implements NestedScope {

  private final ThreadLocal<Map<Key<?>, Object>> scopedObjects =
      new ThreadLocal<Map<Key<?>, Object>>();

  class SimpleScope implements Scope {
    final AtomicBoolean isInScope = new AtomicBoolean(false);

    @Override public <T> Provider<T> scope(
        final Key<T> key, final Provider<T> unscoped) {
      return new Provider<T>() {
        @Override public T get() {
          @SuppressWarnings("unchecked")
          T scopedObject = (T) scopedObjects.get().get(key);
          if (scopedObject == null) {
            scopedObject = checkNotNull(unscoped.get());
            scopedObjects.get().put(key, scopedObject);
          }
          return scopedObject;
        }
      };
    }

    <T> void seed(Key<T> key, T value) {
      checkState(isInScope.get());
      scopedObjects.get().put(key, value);
    }

    void enter() {
      checkState(!isInScope.getAndSet(true));
      scopedObjects.set(new HashMap<Key<?>, Object>());
    }

    void exit() {
      checkState(isInScope.getAndSet(false));
      scopedObjects.remove();
    }
  }

  private final SimpleScope delegateScope;

  public TestNestedScope() {
    delegateScope = new SimpleScope();
    delegateScope.enter();
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
    delegateScope.seed(key, object);
  }

  @Override
  public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
    return delegateScope.scope(key, unscoped);
  }

  /** Returns all the objects scoped by the thread that created this instance */
  public Map<Key<?>, Object> getScopedObjects() {
    return scopedObjects.get();
  }
}
