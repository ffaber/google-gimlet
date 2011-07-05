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
