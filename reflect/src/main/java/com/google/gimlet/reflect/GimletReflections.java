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
import java.util.Map;

/**
 * Provides utility methods to facilitate reflection-based behaviors.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class GimletReflections {
  private GimletReflections() { }

  private static final MethodInvoker METHOD_INVOKER =
      new DefaultMethodInvoker();

  /**
   * @see MethodInvoker#invokeMethods(Iterable, Object) for details on this
   * method.
   * <p>
   * Prefer injection over this static reference where possible.
   */
  public static Map<Method, Object> invokeMethods(
      Iterable<Method> methods, Object target) {
    return METHOD_INVOKER.invokeMethods(methods, target);
  }

  private static final AnnotatedMethodExtractor ANNOTATED_METHOD_EXTRACTOR =
      new DefaultAnnotatedMethodExtractor();

  /**
   *@see AnnotatedMethodExtractor#extractAllAnnotatedMethods(Class, Class) for
   * details on this method.
   * <p>
   * Prefer injection over this static reference where possible.
   */
  public static ImmutableList<Method> extractAllAnnotatedMethods(
      Class<?> clazz, Class<? extends Annotation> annotationClass) {
    return ANNOTATED_METHOD_EXTRACTOR.extractAllAnnotatedMethods(
        clazz, annotationClass);
  }
}
