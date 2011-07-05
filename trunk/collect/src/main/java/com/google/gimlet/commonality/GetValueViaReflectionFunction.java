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

package com.google.gimlet.commonality;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is an implementation of {@link Function} which expects a given
 * no-arg method to be present on its target.  It invokes this function and
 * returns its value.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class GetValueViaReflectionFunction<S> implements Function<Object, S> {

  private final String methodName;

  GetValueViaReflectionFunction(String methodName){
    this.methodName = methodName;
  }

  @Override @SuppressWarnings("unchecked")
  public S apply(Object item) {
    try {
      Method getValueMethod = item.getClass().getMethod(methodName);
      getValueMethod.setAccessible(true);
      return (S) getValueMethod.invoke(item);
    } catch (InvocationTargetException e) {
      throw Throwables.propagate(e);
    } catch (NoSuchMethodException e) {
      throw Throwables.propagate(e);
    } catch (IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
  }
}
