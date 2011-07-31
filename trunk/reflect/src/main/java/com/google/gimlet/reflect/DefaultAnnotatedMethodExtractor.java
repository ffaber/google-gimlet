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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Provides a default implementation of {@link AnnotatedMethodExtractor}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class DefaultAnnotatedMethodExtractor implements AnnotatedMethodExtractor {

  private Map<AnnotatedMethodRequestKey, ImmutableList<Method>>
      ALL_ANNOTATED_METHODS_CACHE =
      new MapMaker().softValues().makeComputingMap(
          new Function<AnnotatedMethodRequestKey, ImmutableList<Method>>() {

            @Override
            public ImmutableList<Method> apply(AnnotatedMethodRequestKey key) {
              return innerExtractAllAnnotatedMethods(
                  key.getClassToIntrospect(), key.getAnnotationClass());
            }
          });

  @Override public ImmutableList<Method> extractAllAnnotatedMethods(
      Class<?> clazz, Class<? extends Annotation> annotationClass) {
    return ALL_ANNOTATED_METHODS_CACHE.get(
        AnnotatedMethodRequestKey.of(clazz, annotationClass));
  }

  ImmutableList<Method> innerExtractAllAnnotatedMethods(
      Class<?> clazz, Class<? extends Annotation> annotationClass) {

    ImmutableList<Method> visibleMethods =
        new GetVisibleMethods().apply(clazz);

    // We need to determine which ones are visible.
    Iterable<Method> visibleAnnotatedMethods = Iterables.filter(
        visibleMethods,
        new AnnotationPresentPredicate(annotationClass));

    Iterable<Method> closestAnnotatedMethods =
        new ClosestMethodChooser().choseClosestMethods(visibleAnnotatedMethods);

    return ImmutableList.copyOf(closestAnnotatedMethods);
  }
}
