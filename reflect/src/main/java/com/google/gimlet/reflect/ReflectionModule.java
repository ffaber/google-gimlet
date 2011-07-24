// Copyright 2011 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.inject.AbstractModule;

/**
 * This module binds implementation classes for the public interfaces exposed
 * within this package.
 *
 * @author ffaber@google.com (Fred Faber)
 */
public class ReflectionModule extends AbstractModule {

  @Override protected void configure() {
    bind(AnnotatedMethodExtractor.class)
        .to(DefaultAnnotatedMethodExtractor.class);
    bind(MethodInvoker.class).to(DefaultMethodInvoker.class);
  }
}
