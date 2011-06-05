// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Predicate;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This predicate accepts all methods that are:
 * <ol>
 *  <li> private, and belong to the given source class
 *  <li> public
 *  <li> protected, and are on a parent class, or within the same package as
 *       the given source class
 *  <li> package private, and exist in the package of the given source class
 * </ol>
 * <br>
 * All other methods are rejected.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class AcceptVisibleMethods implements Predicate<Method> {

  private final Class<?> sourceClass;

  AcceptVisibleMethods(Class<?> sourceClass) {
    this.sourceClass = sourceClass;
  }

  @Override public boolean apply(Method method) {
    int methodModifiers = method.getModifiers();
    if (Modifier.isPrivate(methodModifiers)) {
      return sourceClass.equals(method.getDeclaringClass());
    }

    if (Modifier.isPublic(methodModifiers)) {
      return true;
    }

    Boolean samePackage = method.getDeclaringClass().getPackage().equals(
        sourceClass.getPackage());

    if (Modifier.isProtected(methodModifiers)) {
      return method.getDeclaringClass().isAssignableFrom(sourceClass) ||
             samePackage;
    }

    return samePackage;
  }
}