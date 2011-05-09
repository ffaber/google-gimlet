// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.parallel;

import com.google.common.base.Function;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * An {@code ExecutionStrategy} is class that knows how to execute a transform
 * from {@link Callable} to {@link Future}.  This transform defines
 * exactly how an execution of an algorithm happens. This transform can be
 * synchronous or asynchronous.  In this sense, what an
 * {@link ExecutionStrategy} does is to separate an algorithm from the means
 * of its execution.  That is the main point of an {@code ExecutionStrategy}.
 * That is, it need not be explicit in code how an algorithm is executed, as
 * long as a {@link ExecutionStrategy} is used to execute it.  As such,
 * parallelism can become a means of configuring code with different
 * implementations of a {@code ExecutionStrategy}.
 *
 * @author ffaber@google.com (Fred Faber)
 */
public interface ExecutionStrategy {

  /**
   * Returns the function that dictates how this instance transforms a
   * {@link Callable<T>} to a {@link Future<T>}.
   */
  <T> Function<Callable<T>, Future<T>> getTransform();

  /**
   * Returns the transform used by this instance in a potentially asynchronous
   * form.  Here, <em>getParallelTransform</em> effectively means to get the
   * parallel version of the transform returned by
   * {@link #getTransform()}.
   */
  <T> Function<Callable<T>, Callable<T>> getParallelTransform();

  /**
   * This method accepts a given mapping function and returns a function that
   * applies the given function to all elements in an iterable, potentially
   * in parallel (the parallelism of course is dictated by the function that
   * is returned in {@link #getParallelTransform()}.
   */
  <L,T> Function<Iterable<L>, Callable<Iterable<T>>> getParallelMapTransform(
      Function<L, T> mappingFunction);
}