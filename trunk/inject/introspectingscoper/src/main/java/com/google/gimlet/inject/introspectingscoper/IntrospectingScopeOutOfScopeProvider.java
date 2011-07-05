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

import com.google.inject.Provider;

import java.lang.annotation.Annotation;

/**
 * This class is an implementation of {@link Provider} which should be used
 * to offer a reasonably useful error message when its {@link #get()} method
 * is called, because it will always throw an
 * {@link UnsupportedOperationException}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
class IntrospectingScopeOutOfScopeProvider implements Provider {

  private final Class target;
  private final Class<? extends Annotation> scopeAnnotationClass;

  IntrospectingScopeOutOfScopeProvider(
      Class target,
      Class<? extends Annotation> scopeAnnotationClass) {
    this.target = target;
    this.scopeAnnotationClass = scopeAnnotationClass;
  }

  @Override public Object get() {
    String msg = String.format(
        "Class %s expected to be bound in scope %s.",
        target.getSimpleName(), scopeAnnotationClass.getSimpleName());
    throw new UnsupportedOperationException(msg);
  }
}
