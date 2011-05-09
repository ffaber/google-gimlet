// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.parallel;

import java.util.concurrent.Callable;

/**
 * Provides a simple interface that contracts the means to transform a
 * {@link Callable} into another {@link Callable}.  This
 * is helpful to use to define callable transforms that wrap underlying
 * callables without changing the type of data returned.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface CallableTransform {

  <T> Callable<T> transform(Callable<T> callable);
}
