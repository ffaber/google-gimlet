// Copyright 2009 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.introspectingscoper;

import java.lang.annotation.Annotation;

/**
 * Holds constants related to the {@link CaptureInScope} annotation, which
 * can't hold constants on its own.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class CaptureInScopeConstants {
  private CaptureInScopeConstants() { }

  /**
   * This should always be the same as the default value of
   * {@link CaptureInScope#value()}.
   */
  public static final Class<? extends Annotation> DEFAULT_ANNOTATION_VALUE =
      Annotation.class;
}