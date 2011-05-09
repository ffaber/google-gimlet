// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.parallel;

import java.util.concurrent.Callable;

/**
 * Contains utility functions related to the {@link CallableTransform}
 * interface.
 *
 */
public final class CallableTransforms {
  private CallableTransforms() {}

  public static CallableTransform getIdentityTransform() {
    return new CallableTransform() {
      @Override public <T> Callable<T> transform(Callable<T> callable) {
        return callable;
      }
    };
  }
}
