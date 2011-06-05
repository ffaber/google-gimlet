// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Objects;

import java.lang.annotation.Annotation;

/**
 * This class is a simple struct that represents the unique parameters of a
 * request to extract the annotated methods from a class.
 *
 * @author ffaber@google.com (Fred Faber)
 */
class AnnotatedMethodRequestKey {

  private final Class<?> classToIntrospect;
  private final Class<? extends Annotation> annotationClass;

  public static AnnotatedMethodRequestKey of(
      Class<?> classToIntrospect,
      Class<? extends Annotation> annotationClass) {
    return new AnnotatedMethodRequestKey(classToIntrospect, annotationClass);
  }

  private AnnotatedMethodRequestKey(
      Class<?> classToIntrospect,
      Class<? extends Annotation> annotationClass) {
    this.classToIntrospect = classToIntrospect;
    this.annotationClass = annotationClass;
  }

  Class<? extends Annotation> getAnnotationClass() {
    return annotationClass;
  }

  Class<?> getClassToIntrospect() {
    return classToIntrospect;
  }

  @Override public boolean equals(Object o) {
    if (o == null || !(o instanceof AnnotatedMethodRequestKey)) {
      return false;
    }

    AnnotatedMethodRequestKey that = (AnnotatedMethodRequestKey) o;

    return Objects.equal(this.classToIntrospect, that.classToIntrospect)
        && Objects.equal(this.annotationClass, that.annotationClass);
  }

  @Override public int hashCode() {
    return Objects.hashCode(classToIntrospect, annotationClass);
  }

  @Override public String toString() {
    return Objects.toStringHelper(getClass())
        .add("classToIntrospect", classToIntrospect)
        .add("annotationClass", annotationClass)
        .toString();
  }
}
