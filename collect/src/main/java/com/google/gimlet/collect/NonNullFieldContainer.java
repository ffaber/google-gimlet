// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;

/**
 * A simple container to use to store non-null fields.
 * <p>
 * This class is handy to use as a simple struct when more than one value needs
 * to be passed around.  For instance, within a class, imagine that one method
 * calls another to get database connection info:
 * {@code
 *   static DatabaseConnectionParams extends NonNullFieldContainer {
 *      String username;
 *      String password;
 *      String hostname;
 *   }
 * }
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
