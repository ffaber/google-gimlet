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

import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * Provides a default implementation of {@link MethodInvoker}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class DefaultMethodInvoker implements MethodInvoker {

  @Override public <T> T invokeMethod(
      String methodName, Object target,
      Class<T> returnClassType, Object... args) {
    Method method;
    try {
      method = target.getClass().getMethod(methodName);
      method.setAccessible(true);
      Object result = method.invoke(target, args);

      if (result != null && !returnClassType.isInstance(result)) {
        throw new MethodInvocationException(String.format(
            "[%s] is not an instance of [%s]",
            result.getClass(), returnClassType));
      }
      return returnClassType.cast(result);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(String.format(
          "Couldn't invoke method [%s] on target [%s] of class [%s]",
          methodName, target, target.getClass()));
    } catch (InvocationTargetException e) {
      throw new RuntimeException(String.format(
          "Couldn't invoke method [%s] on target [%s] of class [%s]",
          methodName, target, target.getClass()));
    } catch (IllegalAccessException e) {
      throw new RuntimeException(String.format(
          "Couldn't invoke method [%s] on target [%s] of class [%s]",
          methodName, target, target.getClass()));
    }
  }

  @Override public Map<Method, Object> invokeMethods(
      Iterable<Method> methods, Object target) {

    Map<Method, Object> invocationResults = Maps.newHashMap();
    for (Method method : methods) {
      method.setAccessible(true);
      try {
        Object result = method.invoke(target);
        invocationResults.put(method, result);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(method.toString(), e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(method.toString(), e);
      }
    }

    return Collections.unmodifiableMap(invocationResults);
  }
}
