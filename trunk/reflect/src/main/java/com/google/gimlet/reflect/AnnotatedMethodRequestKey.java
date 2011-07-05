/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.gimlet.reflect;

import com.google.common.base.Objects;

import java.lang.annotation.Annotation;

/**
 * This class is a simple struct that represents the unique parameters of a
 * request to extract the annotated methods from a class.
 *
 * @author ffaber@gmail.com (Fred Faber)
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
