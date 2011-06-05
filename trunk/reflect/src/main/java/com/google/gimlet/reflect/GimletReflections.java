// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Provides utility methods to facilitate reflection-based behaviors.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class GimletReflections {
  private GimletReflections() { }

  private static final MethodInvoker METHOD_INVOKER =
      new DefaultMethodInvoker();

  /**
   * @see MethodInvoker#invokeMethods(Iterable, Object) for details on this
   * method.
   * <p>
   * Prefer injection over this static reference where possible.
   */
  public static Map<Method, Object> invokeMethods(
      Iterable<Method> methods, Object target) {
    return METHOD_INVOKER.invokeMethods(methods, target);
  }
}
