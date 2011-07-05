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

package com.google.gimlet.commonality.subpackage;

import com.google.common.base.Function;

/**
 * Test-based class that provides the simple means to extract the value from a
 * {@link TestItem}.
 *
 * @author ffaber@google.com (Fred Faber)
 */
public class GetValueFromTestItemFunction
    implements Function<TestItem, String> {

  @Override public String apply(TestItem testItem) {
    return testItem.getTestValue();
  }
}
