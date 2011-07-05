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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Provides the means to extract all the visible methods from a given class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class GetVisibleMethods implements Function<Class<?>, ImmutableList<Method>> {

  private static final Map<Class<?>, ClassGraph> HIERARCHY_GRAPHS =
      new MapMaker().softKeys().makeComputingMap(new GetClassGraph());

  private final Map<Class<?>, ImmutableList<Method>> VISIBLE_METHODS_CACHE =
      new MapMaker().softKeys().makeComputingMap(
          new Function<Class<?>, ImmutableList<Method>>() {
            @Override
            public ImmutableList<Method> apply(Class<?> from) {
              return innerApply(from);
            }
          });

  @Override public ImmutableList<Method> apply(Class<?> clazz) {
    return VISIBLE_METHODS_CACHE.get(clazz);
  }

  private ImmutableList<Method> innerApply(Class<?> clazz) {
    ClassGraph classGraph = HIERARCHY_GRAPHS.get(clazz);

     ImmutableList<Class<?>> classes =
         classGraph.getNodesViaBreadthFirstSearch(clazz);

     List<ImmutableSet<Method>> allDeclaredMethods = Lists.transform(
         classes,
         new GetDeclaredMethods());

    List<Method> allFlattenedDeclaredMethods =
        Lists.newArrayList(Iterables.concat(allDeclaredMethods));

    // We need to determine which ones are visible.
    List<Method> allVisibleMethods = Lists.newArrayList(Iterables.filter(
        allFlattenedDeclaredMethods,
        new VisibleMethodsPredicate(clazz)));

    Collections.sort(allVisibleMethods, new CompareByMethodName());

    return ImmutableList.copyOf(allVisibleMethods);
  }
}
