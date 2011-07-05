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

