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
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Provides the means to chose the closest method in a given iterable of methods
 * that are sorted in order of closest to farthest from the source.
 * 
 * @author ffaber@gmail.com (Fred Faber)
 */
class ClosestMethodChooser {

  Iterable<Method> choseClosestMethods(Iterable<Method> methods) {
    return Iterables.transform(
        Iterables.filter(
            Iterables.transform(methods, MethodKey.CREATOR),
            new FirstSeenPredicate<MethodKey>()),
        MethodKey.GET_METHOD);
  }

  static class FirstSeenPredicate<T> implements Predicate<T> {
    private final Set<T> seenElements = Sets.newHashSet();

    @Override public boolean apply(T input) {
      return seenElements.add(input);
    }
  }
}