// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.gimlet.inject.nestedscope;

/**
 * A marker interface to determine what scope we are in.
 *
 */
public interface ScopeId {

  ScopeId DEFAULT = new ScopeId() {
    @Override
    public String toString() {
      return "DEFAULT_SCOPE_ID";
    }
  };
}
