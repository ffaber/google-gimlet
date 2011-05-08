// Copyright 2011 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.legprovider;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A constructor parameter annotated with {@code Leg} indicates that the param
 * is a "robot leg."
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
// TODO(ffaber): elaborate on what this means
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@BindingAnnotation
public @interface Leg {
  String value() default "";
}
