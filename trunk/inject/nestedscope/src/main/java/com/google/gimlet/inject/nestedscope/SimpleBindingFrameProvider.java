// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.Provider;

/**
 * An implementation of {@link Provider} that creates a new instance of {@link
 * BindingFrame} with each call to {@link #get()}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
final class SimpleBindingFrameProvider implements Provider<BindingFrame> {

  @Override public BindingFrame get() {
    return new BindingFrame();
  }
}
