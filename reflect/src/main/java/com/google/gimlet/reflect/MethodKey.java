// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Represents a key that stores the identifying attributes of a method, without
 * caring about on what class or with what visibility such a method was
 * declared.  This is useful when a collection of methods has been identified
 * as visible, and duplicate methods within this set are sought.
 * <p>
 * To be specific, the only attributes stored in the key are:
 * <ol>
 *  <li> the method name
 *  <li> the generic types of the parameters
 *  <li> the return value
 * </ol>
 *
 * @author ffaber@gmail.com (Fred Faber)
*/
@SuppressWarnings({ "FieldCanBeLocal", "UnusedDeclaration" })
class MethodKey {

  static final Function<Method, MethodKey> CREATOR =
      new Function<Method, MethodKey>() {
        public MethodKey apply(Method from) {
          return MethodKey.of(from);
        }
      };

  static final Function<MethodKey, Method> GET_METHOD =
      new Function<MethodKey, Method>() {
        public Method apply(MethodKey methodKey) {
          return methodKey.getMethod();
        }
      };

  private final Method method;

  private final String methodName;
  private final ImmutableList<Type> genericParameterTypes;
  private final Class<?> returnType;

  static MethodKey of(Method method) {
    return new MethodKey(method);
  }

  private MethodKey(Method method) {
    this.method = method;
    this.methodName = method.getName();
    this.genericParameterTypes = ImmutableList.copyOf(
        Arrays.asList((method.getGenericParameterTypes())));
    this.returnType = method.getReturnType();
  }

  /** Returns the method represented by this key. */
  Method getMethod() {
    return method;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MethodKey)) {
      return false;
    }

    MethodKey that = (MethodKey) o;

    return Objects.equal(this.genericParameterTypes, that.genericParameterTypes)
        && Objects.equal(this.methodName, that.methodName)
        && Objects.equal(this.returnType, that.returnType);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(genericParameterTypes, methodName, returnType);
  }
}
