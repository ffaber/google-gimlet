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

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Compares methods by their names, deferring to the value of
 * {@link String#compareTo(Object)} for a return value.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class CompareByMethodName implements Comparator<Method> {

  @Override
  public int compare(Method method1, Method method2) {
    return method1.getName().compareTo(method2.getName());
  }
}
