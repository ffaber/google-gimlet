// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.inject.nestedscope;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to put the ScopeId into the binding frame map.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@BindingAnnotation @interface ScopeIdKey { }
