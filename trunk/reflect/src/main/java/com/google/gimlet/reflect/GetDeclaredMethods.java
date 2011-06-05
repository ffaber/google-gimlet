// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Provides a function that returns the methods that are declared by a given
 * class.
 *
 * @author ffaber@gmail.com (Fred Faber)
*/
class GetDeclaredMethods implements Function<Class<?>, ImmutableSet<Method>> {

  @Override public ImmutableSet<Method> apply(Class<?> from) {
    return ImmutableSet.copyOf(Arrays.asList(from.getDeclaredMethods()));
  }
}