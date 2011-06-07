// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper.noop;

import com.google.gimlet.inject.introspectingscoper.IntrospectingScoper;

/**
 * A no-op implementation of {@link IntrospectingScoper}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class NoOpIntrospectingScoper implements IntrospectingScoper {

  @Override public void scopeIntrospectively(Object target) { }
}
