// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper.defaults;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.gimlet.inject.introspectingscoper.CaptureInScopeConstants.DEFAULT_ANNOTATION_VALUE;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gimlet.inject.introspectingscoper.CaptureInScope;
import com.google.gimlet.inject.introspectingscoper.IntrospectingScoper;
import com.google.gimlet.inject.nestedscope.NestedScope;
import com.google.gimlet.reflect.AnnotatedMethodExtractor;
import com.google.gimlet.reflect.MethodInvoker;
import com.google.inject.Inject;
import com.google.inject.Key;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Provides a default implementation of {@link IntrospectingScoper}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class DefaultIntrospectingScoper implements IntrospectingScoper {

  private final AnnotatedMethodExtractor annotatedMethodExtractor;
  private final MethodInvoker methodInvoker;
  private final NestedScope nestedScope;

  @Inject DefaultIntrospectingScoper(
      AnnotatedMethodExtractor annotatedMethodExtractor,
      MethodInvoker methodInvoker,
      NestedScope nestedScope) {
    this.annotatedMethodExtractor = annotatedMethodExtractor;
    this.methodInvoker = methodInvoker;
    this.nestedScope = nestedScope;
  }

  @Override @SuppressWarnings("unchecked")
  public void scopeIntrospectively(Object target) {
    checkNotNull(target);
    ImmutableList<Method> annotatedMethods =
        annotatedMethodExtractor.extractAllAnnotatedMethods(
            target.getClass(), CaptureInScope.class);
    Map<Method, Object> invocationResults =
        methodInvoker.invokeMethods(annotatedMethods, target);

    for (Entry<Method, Object> entry : invocationResults.entrySet()) {
      Method method = entry.getKey();
      Object result = entry.getValue();

      CaptureInScope annotation;
      annotation = Preconditions
          .checkNotNull(method.getAnnotation(CaptureInScope.class));
      Class<? extends Annotation> bindingAnnotation = annotation.value();
      // The construction of the key will fail if the annotation is not an
      // instance of BindingAnnotation. We could do a Preconditions check here,
      // but it'd be simply redundant.
      Key key = bindingAnnotation == DEFAULT_ANNOTATION_VALUE ?
                Key.get(method.getGenericReturnType()) :
                Key.get(method.getGenericReturnType(), bindingAnnotation);

      nestedScope.put(key, result);
    }
  }
}
