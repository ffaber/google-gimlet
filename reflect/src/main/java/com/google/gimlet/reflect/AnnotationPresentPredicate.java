// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Predicate;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Provides a predicate that is able to determine whether a given annotation
 * class is present on a given {@link AnnotatedElement}.
 *
 * @author ffaber@gmail.com (Fred Faber)
*/
class AnnotationPresentPredicate
    implements Predicate<AnnotatedElement> {

  private final Class<? extends Annotation> annotationClass;

  AnnotationPresentPredicate(Class<? extends Annotation> annotationClass) {
    this.annotationClass = annotationClass;
  }

  @Override
  public boolean apply(AnnotatedElement annotatedElement) {
    return annotatedElement.isAnnotationPresent(annotationClass);
  }
}
