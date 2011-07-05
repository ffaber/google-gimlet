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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * This predicate accepts all methods that are:
 * <ol>
 *  <li> private, and belong to the given source class
 *  <li> public
 *  <li> protected, and are on a parent class, or within the same package as
 *       the given source class
 *  <li> package private, and exist in the package of the given source class
 * </ol>
 * <br>
 * All other methods are rejected.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class VisibleMethodsPredicate implements Predicate<Method> {

  private final Class<?> sourceClass;

  VisibleMethodsPredicate(Class<?> sourceClass) {
    this.sourceClass = sourceClass;
  }

  @Override public boolean apply(Method method) {
    int methodModifiers = method.getModifiers();
    if (Modifier.isPrivate(methodModifiers)) {
      return sourceClass.equals(method.getDeclaringClass());
    }

    if (Modifier.isPublic(methodModifiers)) {
      return true;
    }

    Boolean samePackage = method.getDeclaringClass().getPackage().equals(
        sourceClass.getPackage());

    if (Modifier.isProtected(methodModifiers)) {
      return method.getDeclaringClass().isAssignableFrom(sourceClass) ||
             samePackage;
    }

    return samePackage;
  }
}