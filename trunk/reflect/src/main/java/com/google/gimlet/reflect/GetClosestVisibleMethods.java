// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Provides the means to extract the closest visible methods from a given
 * class, where "closest" is defined to mean the method that is
 * closest to the target class in terms of its class hierarchy.  For example,
 * if the target class overrides a method from its parent class, then the method
 * on the target class is returned because it is "closer."
 *
 * @author ffaber@google.com (Fred Faber)
 */
public class GetClosestVisibleMethods
    implements Function<Class<?>, ImmutableList<Method>> {

  private final Map<Class<?>, ImmutableList<Method>> TIGHTEST_METHODS_CACHE =
      new MapMaker().softKeys().makeComputingMap(
          new Function<Class<?>, ImmutableList<Method>>() {
            @Override
            public ImmutableList<Method> apply(Class<?> from) {
              return innerApply(from);
            }
          });

  @Override
  public ImmutableList<Method> apply(Class<?> clazz) {
    return TIGHTEST_METHODS_CACHE.get(clazz);
  }

  private ImmutableList<Method> innerApply(Class<?> clazz) {
    ImmutableList<Method> allVisibleMethods =
        new GetVisibleMethods().apply(clazz);

    List<Method> classMethods = Lists.newArrayList(
        new ClosestMethodChooser().choseClosestMethods(allVisibleMethods));

    Collections.sort(classMethods, new CompareByMethodName());

    return ImmutableList.copyOf(classMethods);
  }
}
