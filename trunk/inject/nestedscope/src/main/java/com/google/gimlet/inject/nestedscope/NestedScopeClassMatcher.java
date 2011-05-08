// Copyright 2011 Google Inc.  All Rights Reserved

package com.google.gimlet.inject.nestedscope;

import com.google.inject.matcher.AbstractMatcher;

/**
 * This class matches classes that have methods that are eligible for nested
 * scoping.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
// TODO(ffaber) - move to be a Matchers.inPackageRecursive(Package) class.
public class NestedScopeClassMatcher extends AbstractMatcher<Class<?>> {

  /**
   * A nullable package name to use as the parent package, equal to and below
   * which any package will be eligible to provide a scoped method.
   */
  private final String packageName;

  /**
   * Creates a {@link NestedScopeClassMatcher} that matches any class, without
   * prejudice.
   */
  public NestedScopeClassMatcher() {
    packageName = null;
  }

  /**
   * Creates a {@link NestedScopeClassMatcher} that matches only classes that
   * live in the given package, or within a subpackage of the given package.
   */
  public NestedScopeClassMatcher(Package aPackage) {
    packageName = aPackage.getName();
  }

  @SuppressWarnings({ "SimplifiableIfStatement" })
  @Override
  public boolean matches(Class<?> clazz) {
    if (packageName == null) {
      return true;
    }

    return clazz.getPackage().getName().startsWith(packageName);
  }
}
