// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.reflect;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Provides the means to chose the closest method in a given iterable of methods
 * that are sorted in order of closest to farthest from the source.
 * 
 * @author ffaber@gmail.com (Fred Faber)
 */
class ClosestMethodChooser {

  Iterable<Method> choseClosestMethods(Iterable<Method> methods) {
    return Iterables.transform(
        Iterables.filter(
            Iterables.transform(methods, MethodKey.CREATOR),
            new FirstSeenPredicate<MethodKey>()),
        MethodKey.GET_METHOD);
  }

  static class FirstSeenPredicate<T> implements Predicate<T> {
    private final Set<T> seenElements = Sets.newHashSet();

    @Override public boolean apply(T input) {
      return seenElements.add(input);
    }
  }
}