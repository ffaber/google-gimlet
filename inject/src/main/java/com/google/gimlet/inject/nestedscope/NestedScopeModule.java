// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Binds implementation classes for this package.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NestedScopeModule extends AbstractModule {

  @Override protected void configure() {
    bind(BindingFrame.class);
    bind(NestedScopeCallableTransform.class);
    bind(new TypeLiteral<Iterable<ScopeId>>() {})
        .toProvider(ScopeIdStackProvider.class);

    NestedScopeImpl nestedScopeImpl
        = new NestedScopeImpl(new SimpleBindingFrameProvider());

    bind(NestedScope.class).toInstance(nestedScopeImpl);
    bind(NestedScopeImpl.class).toInstance(nestedScopeImpl);
    bindScope(NestedScoped.class, nestedScopeImpl);
  }
}
