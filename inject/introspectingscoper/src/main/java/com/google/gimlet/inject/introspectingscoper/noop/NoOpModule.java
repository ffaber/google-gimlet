// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper.noop;

import com.google.gimlet.inject.introspectingscoper.IntrospectingScoper;
import com.google.inject.AbstractModule;

/**
 * Binds implementation classes for this package.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NoOpModule extends AbstractModule {

  @Override protected void configure() {
    bind(IntrospectingScoper.class).to(NoOpIntrospectingScoper.class);
  }
}
