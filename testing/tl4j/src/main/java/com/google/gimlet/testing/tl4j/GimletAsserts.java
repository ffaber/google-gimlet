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

package com.google.gimlet.testing.tl4j;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class contains test-related assertion methods that mainly act to
 * embellish those on {@link JUnitAsserts}.
 * 
 * @author ffaber@gmail.com (Fred Faber) 
 */
public final class GimletAsserts {
  private GimletAsserts() { }

  /**
    * A passthrough to {@link #assertContentsAnyOrder(String, Iterable,
    * Object[])} which uses an empty string as the message argument.
    */
   public static void assertContentsAnyOrder(
       Iterable<?> actual, Object... expected) {
     assertContentsAnyOrder("", actual, expected);
   }

   /**
    * A passthrough to {@link JUnitAsserts#assertContentsAnyOrder(String,
    * Iterable, Object[])}, which uses an embellished message when the
    * comparison fails.
    */
   public static void assertContentsAnyOrder(
       String message, Iterable<?> actual, Object... expected) {
     try {
       JUnitAsserts.assertContentsAnyOrder(actual, expected);
     } catch (AssertionFailedError afe) {
       String fullMessage = renderFullContentsComparisonFailedMessage(
           message, actual, afe, expected);
       AssertionFailedError rethrownError =
           new AssertionFailedError(fullMessage);
       rethrownError.setStackTrace(afe.getStackTrace());
       throw rethrownError;
     }
   }

   /**
    * A passthrough to {@link #assertContentsAnyOrder(String, Iterable,
    * Object[])}, which uses an embellished message when the comparison fails.
    */
   public static void assertContentsAnyOrder(
       String message, Iterable<?> expected, Iterable<?> actual) {
     assertContentsAnyOrder(message, actual,
         Lists.newArrayList(expected).toArray());
   }

   /**
    * A passthrough to {@link #assertContentsAnyOrder(String, Iterable,
    * Iterable)}, which uses an empty string.
    */
   public static void assertContentsAnyOrder(
       Iterable<?> expected, Iterable<?> actual) {
     assertContentsAnyOrder("", expected, actual);
   }

   /**
    * A passthrough to
    * {@link #assertContentsInOrder(String, Iterable, Object[])},
    * which uses an empty string as the message argument.
    */
   public static void assertContentsInOrder(
       Iterable<?> actual, Object... expected) {
     assertContentsInOrder("", actual, expected);
   }

   /**
    * A passthrough to
    * {@link JUnitAsserts#assertContentsInOrder(String, Iterable, Object[])},
    * which uses an embellished message when the comparison fails.
    */
   public static void assertContentsInOrder(
       String message, Iterable<?> actual, Object... expected) {
     try {
       JUnitAsserts.assertContentsInOrder(actual, expected);
     } catch (AssertionFailedError afe) {
       String fullMessage = renderFullContentsComparisonFailedMessage(
           message, actual, afe, expected);
       AssertionFailedError rethrownError =
           new AssertionFailedError(fullMessage);
       rethrownError.setStackTrace(afe.getStackTrace());
       throw rethrownError;
     }
   }

   /** Asserts that the given collection has the given size. */
   public static void assertSize(int size, Collection<?> actual) {
     Assert.assertEquals(
         String.format("Expected size of %s but got %s. Elements were %s",
             size, actual.size(), actual),
         size, actual.size());
   }

   private static String renderFullContentsComparisonFailedMessage(
       String message,
       Iterable<?> actual,
       AssertionFailedError afe,
       Object... expected) {
     return String.format(
         "%s%n%s%n%s%n",
         message,
         renderContentsComparisonFailedMessage(actual, expected),
         afe.getMessage());
   }

  @VisibleForTesting static String renderContentsComparisonFailedMessage(
       Iterable<?> actual, Object... expected) {

     List<String> actualAsStrings = Lists.newArrayList(Lists.transform(
         Lists.newArrayList(actual), Functions.toStringFunction()));
     List<String> expectedAsStrings = Lists.newArrayList(Lists.transform(
         Arrays.asList(expected), Functions.toStringFunction()));

     String msg = String.format(
         "Comparison failed.\nExpected:\n%s\nActual:\n%s\n",
         Joiner.on("\n").join(expected), Joiner.on("\n").join(actual));
     try {
       Assert.assertEquals(
           expectedAsStrings.toString(), actualAsStrings.toString());
       msg += "(no identifyable differences found)";
     } catch (AssertionFailedError afe) {
       msg += String.format(
           "The expected list had size %d but the actual had size %d.\n" +
               "The differences in elements was: %s\n",
           expectedAsStrings.size(), actualAsStrings.size(), afe.getMessage());
     }
     return msg;
   }
}
