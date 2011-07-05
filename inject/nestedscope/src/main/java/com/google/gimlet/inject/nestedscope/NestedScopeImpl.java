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

package com.google.gimlet.inject.nestedscope;

import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Key;
import com.google.inject.Provider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a default implementation of {@link NestedScope}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
final class NestedScopeImpl implements NestedScope {

  private static final Logger logger =
      Logger.getLogger(NestedScopeImpl.class.getCanonicalName());

  /** The per-thread collection of active stacks */
  private final ThreadLocal<BindingFrameStack> bindingFrameStacks =
      new ThreadLocal<BindingFrameStack>() {
        @Override protected BindingFrameStack initialValue() {
          return new BindingFrameStack();
        }
      };

  /** Used to create new instances of {@link BindingFrame} within a new scope */
  private final Provider<BindingFrame> bindingFrameProvider;

  NestedScopeImpl(Provider<BindingFrame> bindingFrameProvider) {
    this.bindingFrameProvider = bindingFrameProvider;
  }

  /**
   * {@inheritDoc} This method returns a {@link Provider} that will look for the
   * given key within the nested scopes of the requesting thread when its
   * <tt>get</tt> method is called.  If found, the value that is associated with
   * that key will be returned.  If not found, then <tt>null</tt> will be
   * returned.
   */
  @Override public synchronized <T> Provider<T> scope(
      final Key<T> key, final Provider<T> unscoped) {
    return new Provider<T>() {
      @Override
      public T get() {
        BindingFrameStack bindingFrameStack = getBindingFrameStack();
        T scopedObject = bindingFrameStack.lookup(key);
        if (scopedObject == null) {
          scopedObject = unscoped.get();
          put(key, scopedObject);
        }
        return scopedObject;
      }
    };
  }

  @Override public synchronized <T> void put(Key<T> key, T object) {
    T objectAlreadyInScope = getBindingFrameStack().peek().get(key);
    checkState(
        objectAlreadyInScope == null,
        String.format(
            "Can not put another object in scope for the same key: " +
                "key(%s), old_value(%s), new_value(%s)",
            key, objectAlreadyInScope, object));

    getBindingFrameStack().put(key, object);
  }

  @Override public void enterNew() {
    enterNew(ScopeId.DEFAULT);
  }

  /**
   * {@inheritDoc}
   * <p> This implementation of {@link #enterNew()} of the {@link
   * NestedScope} interface guards against any unclosed nested scopes.
   * Specifically, it resets the nested scope to contain a new binding frame.
   */
  @Override public void enterNew(ScopeId scopeId) {
    if (getBindingFrameStack().peek() != null) {
      logger.log(
          Level.WARNING,
          String.format(
              "Unclosed nested scopes were detected. Binding Frame Stack: %s",
              getBindingFrameStack().getName()),
          new Exception());
    }

    bindingFrameStacks.remove();
    enter(scopeId);
  }

  @Override public void enter(ScopeId scopeId) {
    BindingFrame newBindingFrame = bindingFrameProvider.get();
    for (BindingFrame bindingFrame :
        getBindingFrameStack().getBindingFrames()) {
      ScopeId outerScopeId = bindingFrame.get(BindingFrame.SCOPE_ID_KEY);
      if (scopeId.equals(outerScopeId)) {
        throw new IllegalArgumentException(String.format(
            "ScopeId %s already used within frame %s", scopeId, bindingFrame));
      }
    }

    // Store the scope id in a special key in the binding frame. since the
    // annotation is package private, no one should have access to it.
    newBindingFrame.put(BindingFrame.SCOPE_ID_KEY, scopeId);
    enterScope(newBindingFrame);
  }

  /**
   * This method should be called when a thread enters a new nested scope.
   *
   * @param bindingFrame the scope, potentially populated with scoped values, to
   * add to the stack of {@link BindingFrame}s.
   */
  void enterScope(BindingFrame bindingFrame) {
    getBindingFrameStack().push(bindingFrame);
  }

  @Override public void exit() {
    BindingFrame bindingFrame = getBindingFrameStack().pop();
    checkState(
        bindingFrame != null,
        "Exiting a scope for which no frame exists is illegal");
  }

  /** Returns the entire {@link BindingFrameStack} of the current thread */
  BindingFrameStack getBindingFrameStack() {
    return bindingFrameStacks.get();
  }

  /**
   * Sets the {@link BindingFrameStack} of the current thread. Useful to use
   * when one thread is inheriting a stack from another thread.
   */
  void setBindingFrameStack(BindingFrameStack bindingFrameStack) {
    this.bindingFrameStacks.set(bindingFrameStack);
  }
}