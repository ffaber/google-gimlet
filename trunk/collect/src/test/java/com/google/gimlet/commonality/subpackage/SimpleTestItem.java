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

/**
 * Provides a simple implementation of {@link TestItem}.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class SimpleTestItem implements TestItem {

  private final String value;

  public static SimpleTestItem of(String value) {
    return new SimpleTestItem(value);
  }

  private SimpleTestItem(String value) {
    this.value = value;
  }

  @Override public String getTestValue() {
    return value;
  }
}
