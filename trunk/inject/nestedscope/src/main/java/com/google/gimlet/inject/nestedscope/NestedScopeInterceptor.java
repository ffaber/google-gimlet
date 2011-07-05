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

package com.google.gimlet.inject.nestedscope;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.annotation.Nullable;

/**
 * This class provides the functionality to intercept a method and to bind its
 * arguments in a {@link BindingFrame}. <p> Any method that is intercepted must
 * have a {@link NestedScoped} annotation present on it.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class NestedScopeInterceptor implements MethodInterceptor {

  /** The scope that is modified by this interceptor. */
  private NestedScopeImpl nestedScopeImpl;

  /** The factory used to create new instances of {@link BindingFrame}. */
  private Provider<BindingFrame> bindingFrameProvider;

  NestedScopeInterceptor() {
  }

  @Inject void initialize(
      NestedScopeImpl nestedScopeImpl,
      Provider<BindingFrame> bindingFrameProvider) {
    this.nestedScopeImpl = nestedScopeImpl;
    this.bindingFrameProvider = bindingFrameProvider;
  }

  @Override
  public Object invoke(MethodInvocation methodInvocation) throws Throwable {
    BindingFrame bindingFrame = before(methodInvocation);
    try {
      nestedScopeImpl.enterScope(bindingFrame);
      return methodInvocation.proceed();
    } finally {
      nestedScopeImpl.exit();
    }
  }

  /** A simple driver method to prepare for method interception. */
  private BindingFrame before(MethodInvocation methodInvocation) {
    BindingFrame bindingFrame = bindingFrameProvider.get();
    scopeMethodArguments(methodInvocation, bindingFrame);
    return bindingFrame;
  }

  /**
   * This method captures the values of scoped arguments on a method that is
   * annotated with {@link NestedScoped} and stores the values in the given
   * {@link BindingFrame}.  A <em>scoped argument</em> is defined as an argument
   * that has a {@link CaptureInNestedScope} annotation on it.
   *
   * @param methodInvocation the representation of the live method call that
   * provides the argument values to scope
   * @param bindingFrame the {@link BindingFrame} to populate with the arguments
   * that should be scoped
   */
  @VisibleForTesting void scopeMethodArguments(
      MethodInvocation methodInvocation, BindingFrame bindingFrame) {
    Method method = methodInvocation.getMethod();
    checkArgument(
        method.isAnnotationPresent(NestedScoped.class),
        "Unscoped method passed to the NestedScopeInterceptor: %s",
        method);

    Object[] methodArguments = methodInvocation.getArguments();
    Type[] genericParameterTypes = method.getGenericParameterTypes();
    Annotation[][] annotationsOfParameters = method.getParameterAnnotations();
    for (int i = 0; i < annotationsOfParameters.length; i++) {
      Annotation[] annotationsOfParameter = annotationsOfParameters[i];

      // We look at all the annotations of each parameter to see if we should
      // capture its value. If we determine that we should capture its value,
      // we then look for binding annotations.  We require that there be no
      // more than one binding annotation on any parameter that is captured.
      boolean foundScopeAnnotation =
          scopedAnnotationPresent(annotationsOfParameter);

      if (foundScopeAnnotation) {
        Annotation bindingAnnotation =
            findBindingAnnotation(annotationsOfParameter);

        Key<?> key = getKey(bindingAnnotation, genericParameterTypes[i]);

        addUniqueKey(
            key,
            methodArguments[i],
            bindingFrame);
      }
    }
  }

  /**
   * @return <tt>true</tt> if the <tt>annotationsOfParameter</tt> array contains
   *         an instance of the {@link CaptureInNestedScope} annotation.
   */
  private boolean scopedAnnotationPresent(Annotation[] annotationsOfParameter) {
    for (Annotation annotation : annotationsOfParameter) {
      if (CaptureInNestedScope.class.isInstance(annotation)) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method inspects the given array of annotations and returns the one
   * annotation that itself is annotated with {@link BindingAnnotation}.  If
   * such an annotation does not exist in the array, this method returns
   * <tt>null</tt>.
   */
  private Annotation findBindingAnnotation(
      Annotation[] annotationsOfParameter) {
    Annotation bindingAnnotation = null;
    for (Annotation annotation : annotationsOfParameter) {
      if (annotation.annotationType().isAnnotationPresent(
          BindingAnnotation.class)) {
        checkArgument(
            bindingAnnotation == null,
            "There must be no more than one binding annotation on an " +
                "argument that is captured in scope.");
        bindingAnnotation = annotation;
      }
    }
    return bindingAnnotation;
  }

  /**
   * Creates a key using a possibly <tt>null</tt> Annotation and the {@link
   * Type} of the given {@link TypeLiteral}.
   */
  @SuppressWarnings({ "unchecked" })
  private Key<?> getKey(
      @Nullable Annotation bindingAnnotation, Type type) {
    Key<?> key;
    if (bindingAnnotation != null) {
      key = Key.get(type, bindingAnnotation);
    } else {
      key = Key.get(type);
    }
    return key;
  }

  /**
   * Adds the {@code (Key, Object)} pair to the given {@link BindingFrame},
   * throwing an {@link IllegalArgumentException} if an identical key already
   * exists in the scope.
   */
  @VisibleForTesting
  @SuppressWarnings({ "unchecked" }) <T> void addUniqueKey(Key<?> key, T value,
      BindingFrame bindingFrame) {
    if (bindingFrame.get(key) != null) {
      throw new IllegalArgumentException(String.format(
          "The nested scope can not accept another value for key %s " +
              "because it is already bound to value %s", key, value));
    }
    bindingFrame.put((Key<T>) key, value);
  }
}
