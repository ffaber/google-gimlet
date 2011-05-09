// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import com.google.inject.AbstractModule;

/**
 * Binds implementations in this package.
 *
 */
public class ParallelModule extends AbstractModule {

  @Override protected void configure() {
    // this is an empty module but is here in case we decide in the future we
    // want to add bindings for this package.
  }
}
