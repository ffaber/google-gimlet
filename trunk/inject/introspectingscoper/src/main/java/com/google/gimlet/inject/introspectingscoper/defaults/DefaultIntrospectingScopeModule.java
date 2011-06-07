// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper.defaults;

import com.google.gimlet.inject.introspectingscoper.IntrospectingScoper;
import com.google.inject.AbstractModule;

/**
 * Binds implementation classes for this package.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class DefaultIntrospectingScopeModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IntrospectingScoper.class).to(DefaultIntrospectingScoper.class);
  }
}
