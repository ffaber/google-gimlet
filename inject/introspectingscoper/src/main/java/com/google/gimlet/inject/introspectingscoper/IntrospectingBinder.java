// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.gimlet.inject.introspectingscoper.CaptureInScopeConstants.DEFAULT_ANNOTATION_VALUE;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gimlet.reflect.GimletReflections;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class is a complement to the {@link IntrospectingScoper} class.  It
 * provides the means to introspect on a target in order to create bindings
 * for annotated methods.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class IntrospectingBinder {

  private static final Logger logger =
      Logger.getLogger(IntrospectingBinder.class.getCanonicalName());

  private static final Set<Key<?>> seenKeys = Sets.newHashSet();
  private final Binder binder;

  public static IntrospectingBinder newIntrospectingBinder(Binder binder) {
    binder.requestStaticInjection(IntrospectingBinder.class);
    return new IntrospectingBinder(binder);
  }

  private IntrospectingBinder(Binder binder) {
    this.binder = binder;
  }

  /**
   * Sames as {@link #bindIntrospectively(Class, Class, Provider)},
   * but uses a default out-of-scope provider which offers a reasonably
   * useful error message.
   */
  public void bindIntrospectivelyInScope(
      Class target,
      Class<? extends Annotation> scopeAnnotation) {
    Provider<?> provider = 
        new IntrospectingScopeOutOfScopeProvider(target, scopeAnnotation);
    bindIntrospectively(target, scopeAnnotation, provider);
  }

  /**
   * Introspects the given target in order to locate methods that are
   * annotated with {@link CaptureInScope}.  For each such method, a
   * {@link Key} is created using the annotation that was found.  This key
   * is then bound to the given {@link Provider}, which will be used as the
   * "unscoped provider" within the scope that corresponds to the given
   * scope annotation (e.g., {@code RequestScoped}).
   */
  public void bindIntrospectively(
      Class target,
      Class<? extends Annotation> scopeAnnotation,
      Provider<?> outOfScopeProvider) {
    ImmutableList<Method> annotatedMethods =
        GimletReflections.extractAllAnnotatedMethods(
            target, CaptureInScope.class);

    for (Method method : annotatedMethods) {
      CaptureInScope annotation = checkNotNull(
          method.getAnnotation(CaptureInScope.class));
      Class<? extends Annotation> bindingAnnotation = annotation.value();
      Key key = bindingAnnotation == DEFAULT_ANNOTATION_VALUE ?
                Key.get(method.getGenericReturnType()) :
                Key.get(method.getGenericReturnType(), bindingAnnotation);

      if (!seenKeys.add(key)) {
        logger.fine("Would have attempted to bind the same key twice: " + key);
      } else {
        binder.bind(key).toProvider(outOfScopeProvider).in(scopeAnnotation);
      }
    }
  }

  @Inject @VisibleForTesting
  static void clearKeys() {
    seenKeys.clear();
  }
}
