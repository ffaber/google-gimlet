// Copyright 2011 Google Inc.  All Rights Reserved

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
