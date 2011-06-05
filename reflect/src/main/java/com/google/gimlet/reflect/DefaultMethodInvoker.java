// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * Provides a default implementation of {@link MethodInvoker}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class DefaultMethodInvoker implements MethodInvoker {

  @Override public Map<Method, Object> invokeMethods(
      Iterable<Method> methods, Object target) {

    Map<Method, Object> invocationResults = Maps.newHashMap();
    for (Method method : methods) {
      method.setAccessible(true);
      try {
        Object result = method.invoke(target);
        invocationResults.put(method, result);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(method.toString(), e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(method.toString(), e);
      }
    }

    return Collections.unmodifiableMap(invocationResults);
  }
}
