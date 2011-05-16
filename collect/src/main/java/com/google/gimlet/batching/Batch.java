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

package com.google.gimlet.batching;

import java.util.Collection;

/**
 * A {@code Batch} is functionally equivalent to a {@link Collection}, but is
 * renamed as to facilitate distinct naming for batch-related classes.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface Batch<T> extends Collection<T> {
}