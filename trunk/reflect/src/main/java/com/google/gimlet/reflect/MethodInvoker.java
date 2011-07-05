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
import java.util.Map;

/**
 * An implementation of this interface provides the convenience functionality to
 * invoke methods on a given target, returning their output as a mapping from
 * {@link Method} to return value.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface MethodInvoker {

  class MethodInvocationException extends RuntimeException {
    MethodInvocationException(String msg) {
      super(msg);
    }
  }

  /**
   * Invokes the given method name on the given target, with the given
   * arguments. Casts the result to the given {@code returnClassType}.
   */
  <T> T invokeMethod(
      String methodName, Object target,
      Class<T> returnClassType, Object... args)
      throws MethodInvocationException;

  /**
   * Invokes the given methods on a given target, wrapping each checked
   * exception into a {@link RuntimeException}.  For this reason, use this
   * method only when you really don't care about recovering after an
   * invocation exception.
   * <p>
   * The return value is a mapping of {@link Method} to its return value,
   * provided as an unmodifiable Map.
   */
  Map<Method, Object> invokeMethods(Iterable<Method> methods, Object target)
    throws MethodInvocationException;

}