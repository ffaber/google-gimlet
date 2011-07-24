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

package com.google.gimlet.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;

/**
 * A simple container to use to store non-null fields.
 * <p>
 * This class is handy to use as a simple struct when more than one value needs
 * to be passed around.  For instance, within a class, imagine that one method
 * calls another to get database connection info:
 * <pre>{@code
 *   static DatabaseConnectionParams extends NonNullFieldContainer {
 *      String username;
 *      String password;
 *      String hostname;
 *   }
 * }</pre>
 * <p>
 * The method that receives this value should call {@link #checkInitialized()}
 * to ensure that all fields were set to non-null values. 
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public abstract class NonNullFieldContainer {

  /**
   * Ensures all declared fields are non-null.  If a field is found to be null,
   * then a {@link NullPointerException} is thrown.
   */
  public void checkInitialized() {
    try {
      for (Field field : this.getClass().getDeclaredFields()) {
        field.setAccessible(true);
        checkNotNull(field.get(this), "Field is null: " + field.getName());
      }
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
