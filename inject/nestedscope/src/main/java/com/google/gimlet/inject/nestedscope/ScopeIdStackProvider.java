// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.inject.nestedscope;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides the stack of scope ids that have been accumulated via the entering
 * of scopes for the current thread. The head (beginning) of the deck is the
 * most recently added scope id.
 */
class ScopeIdStackProvider implements Provider<Iterable<ScopeId>> {

  private final NestedScopeImpl nestedScopeImpl;

  @Inject ScopeIdStackProvider(NestedScopeImpl nestedScopeImpl) {
    this.nestedScopeImpl = nestedScopeImpl;
  }

  @Override public Iterable<ScopeId> get() {
    List<ScopeId> scopeIds = new LinkedList<ScopeId>();
    BindingFrameStack bindingFrameStack
        = nestedScopeImpl.getBindingFrameStack();
    for (BindingFrame bindingFrame : bindingFrameStack.getBindingFrames()) {
      ScopeId scopeId =
          checkNotNull(
              bindingFrame.get(BindingFrame.SCOPE_ID_KEY),
              "Bindingframe %s did not have scope key in binding stack %s",
              bindingFrame, bindingFrameStack);
      scopeIds.add(scopeId);
    }
    return scopeIds;
  }
}

