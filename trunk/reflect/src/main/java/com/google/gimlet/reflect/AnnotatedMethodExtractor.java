// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.collect.ImmutableList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * An instance of this interface is able to extract annotated methods from a
 * given class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface AnnotatedMethodExtractor {

  /**
   * Extracts all methods, inherited and declared, on the given class and all of
   * its superclasses and implementing interfaces, that are annotated with an
   * annotation of class {@code annotationClass}. The methods are returned in a
   * stable order, such that two calls to this method with equivalent
   * arguments are guaranteed to return equivalent lists.
   */
  ImmutableList<Method> extractAllAnnotatedMethods(
      Class<?> clazz, Class<? extends Annotation> annotationClass);
}