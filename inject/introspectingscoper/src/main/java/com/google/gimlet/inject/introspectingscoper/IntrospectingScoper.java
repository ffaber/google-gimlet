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

/**
 * An {@link IntrospectingScoper} is able to introspect a target object and
 * to scope values that can be provided by the target.  The values that are
 * extracted, and the scope that is used, is implementation dependent.  Such
 * configuration is expected to be present in the creation of an implementation,
 * or embedded into the behavior of the {@link #scopeIntrospectively(Object)}
 * method itself.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface IntrospectingScoper {

  /** Looks at the given target and scopes values related to it. */
  void scopeIntrospectively(Object target);
}