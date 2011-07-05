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