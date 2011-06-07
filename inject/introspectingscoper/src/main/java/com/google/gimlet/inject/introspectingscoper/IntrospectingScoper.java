// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper;

/**
 * An {@link IntrospectingScoper} is able to introspect a target object and
 * to scope values that can be provided by the target.  The values that are
 * extracted, and the scope that is used, is implementation dependent.  Such
 * configuration is expected to be present in the creation of an implementation,
 * or embedded into the behavior of the {@link #scopeIntrospectively(Object)}
 * method itself.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface IntrospectingScoper {

  /** Looks at the given target and scopes values related to it. */
  void scopeIntrospectively(Object target);
}