/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.gimlet.inject.introspectingscoper;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is a candidate to have its return value be put into
 * scope by an instance of an {@link IntrospectingScoper}.  That is, a method
 * that is annotated with this annotation, and that is inspected by an {@code
 * IntrospectingScoper}, will be invoked, and its return value will be scoped.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CaptureInScope {

  /**
   * The value of this annotation should either be nothing, or a {@code
   * BindingAnnotation}.  This value will be used to form the guice {@code Key}
   * under which a value will be scoped.  If the annotation is not a {@code
   * BindingAnnotation}, then a runtime exception will be thrown.
   */
  Class<? extends Annotation> value() default Annotation.class;
}
