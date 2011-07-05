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

package com.google.gimlet.parallel;

import java.util.concurrent.Callable;

/**
 * Provides a simple interface that contracts the means to transform a
 * {@link Callable} into another {@link Callable}.  This
 * is helpful to use to define callable transforms that wrap underlying
 * callables without changing the type of data returned.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public interface CallableTransform {

  <T> Callable<T> transform(Callable<T> callable);
}
