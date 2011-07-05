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
import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Method;

/**
 * Provides the means to canonicalize a method signature.  Please see the
 * javadoc on {@link #apply(Method)} for details.
 * 
 * @author ffaber@gmail.com (Fred Faber)
 */
class CanonicalizeMethodSignature implements Function<Method, String> {

  /**
   * {@inheritDoc}
   *
   * This method creates a canonical signature of a method by using the
   * following steps:
   * <ol>
   *  <li> get the string representation of the method name.  This is something
   *       like:
   *  {@code public com.google.ClassName.methodName(com.google.ArgClass)}
   *  <li> remove all modifiers to strip the signature to
   *  {@code com.google.ClassName.methodName(com.google.ArgClass)}
   *  <li> remove the prefix to return {@code methodName(com.google.ArgClass)}
   * </ol>
   */
  @Override
  public String apply(Method from) {
    // This strips the method modifiers
    String[] tokens = from.toString().split(" ");
    String classAndMethodParams = tokens[tokens.length - 1];

    // This strips the package and class path of the method name
    Integer indexOfLeftParen = classAndMethodParams.indexOf('(');

    // This finds the last location of '.' in the package and class and
    // returns the string to the right of this dot.
    int dotIndex = classAndMethodParams.lastIndexOf('.', indexOfLeftParen);
    checkArgument(
        dotIndex >= 0,
        "Method signature can not be canonicalized: " + from.toString());
    return classAndMethodParams.substring(dotIndex + 1);
  }
}
