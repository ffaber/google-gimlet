// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation may be used on a method parameter in order to capture the
 * value of the parameter in a {@link BindingFrame}.  In order to be relevant,
 * the method must be annotated with a {@link NestedScoped} annotation.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
@Retention(RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CaptureInNestedScope { }
