// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used in two ways: <ol> <li> it is used as a {@link
 * ScopeAnnotation} to indicate that a particular binding should be memoized in
 * nested scope <li> it is used as a method annotation to indicate that the
 * method should be intercepted to have some of its arguments captured within
 * nested scope </ol> <p> For the interception use case, this annotation is only
 * effective if any of the parameters of the method are annotated with {@link
 * CaptureInNestedScope}. If this is not the case, then none of the arguments
 * will be captured and placed into scope.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@ScopeAnnotation
public @interface NestedScoped { }
