// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Compares methods by their names, deferring to the value of
 * {@link String#compareTo(Object)} for a return value.
 *
 * @author ffaber@google.com (Fred Faber)
 */
class CompareByMethodName implements Comparator<Method> {

  @Override
  public int compare(Method method1, Method method2) {
    return method1.getName().compareTo(method2.getName());
  }
}
