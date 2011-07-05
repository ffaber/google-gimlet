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
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Provides the means to extract the closest visible methods from a given
 * class, where "closest" is defined to mean the method that is
 * closest to the target class in terms of its class hierarchy.  For example,
 * if the target class overrides a method from its parent class, then the method
 * on the target class is returned because it is "closer."
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class GetClosestVisibleMethods
    implements Function<Class<?>, ImmutableList<Method>> {

  private final Map<Class<?>, ImmutableList<Method>> TIGHTEST_METHODS_CACHE =
      new MapMaker().softKeys().makeComputingMap(
          new Function<Class<?>, ImmutableList<Method>>() {
            @Override
            public ImmutableList<Method> apply(Class<?> from) {
              return innerApply(from);
            }
          });

  @Override
  public ImmutableList<Method> apply(Class<?> clazz) {
    return TIGHTEST_METHODS_CACHE.get(clazz);
  }

  private ImmutableList<Method> innerApply(Class<?> clazz) {
    ImmutableList<Method> allVisibleMethods =
        new GetVisibleMethods().apply(clazz);

    List<Method> classMethods = Lists.newArrayList(
        new ClosestMethodChooser().choseClosestMethods(allVisibleMethods));

    Collections.sort(classMethods, new CompareByMethodName());

    return ImmutableList.copyOf(classMethods);
  }
}
