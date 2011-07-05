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
