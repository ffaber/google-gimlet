// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import java.util.concurrent.Callable;

/**
 * A transformer that takes an Iterable of Callables and turns it into a
 * Callable of Iterables.
 *
 */
public interface IterableCallableTransformer {

  <T> Callable<Iterable<T>> apply(Iterable<Callable<T>> from);
}
