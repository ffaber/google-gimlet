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

package com.google.gimlet.inject.nestedscope;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Key;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

/**
 * A {@link BindingFrameStack} represents a stack of {@link BindingFrame}s for a
 * given thread.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
final class BindingFrameStack implements Cloneable {

  private final Deque<BindingFrame> bindingFrames =
      new ArrayDeque<BindingFrame>();

  /**
   * Pops the most current {@link BindingFrame} from the stack. If the stack is
   * empty, it returns null.
   */
  synchronized BindingFrame pop() {
    return bindingFrames.pollFirst();
  }

  /** Peeks at the first {@link BindingFrame} in the stack. */
  synchronized BindingFrame peek() {
    return bindingFrames.peek();
  }

  /** Pushes the given {@link BindingFrame} onto the stack. */
  synchronized void push(BindingFrame bindingFrame) {
    bindingFrames.push(bindingFrame);
  }

  @VisibleForTesting
  /** Returns the binding frames kept in this stack, as an {@link Iterable}. */
  Iterable<BindingFrame> getBindingFrames() {
    return Collections.unmodifiableCollection(bindingFrames);
  }

  /**
   * Looks up the given {@link Key} in the frames of this stack. If the key is
   * found, then the value associated with that key is returned.  Else,
   * <tt>null</tt> is returned.
   */
  synchronized <T> T lookup(Key<T> key) {
    for (BindingFrame currentBindingFrame : bindingFrames) {
      T value = currentBindingFrame.get(key);
      if (value != null) {
        return value;
      }
    }

    return null;
  }

  /**
   * Puts the given {@code key, object} pair into the first frame on the stack.
   * This is a convenience method that is analogous to {@link #lookup(Key)}.
   */
  synchronized <T> void put(Key<T> key, T object) {
    checkState(
        bindingFrames.size() > 0,
        "Can not add an element when no frames exist on the deque");
    peek().put(key, object);
  }

  @Override public String toString() {
    return getName();
  }

  /** Returns a name that is derived from the composite binding frames */
  synchronized String getName() {
    StringBuilder stringBuilder = new StringBuilder(16 * bindingFrames.size());
    for (BindingFrame bindingFrame : bindingFrames) {
      stringBuilder.append(String.format("[%s] ", bindingFrame.toString()));
    }
    return stringBuilder.toString();
  }

  /**
   * {@inheritDoc} Creates a shallow copy of the frames of this stack and
   * returns a new stack with those copies pushed onto it.
   */
  @Override
  protected synchronized BindingFrameStack clone()
      throws CloneNotSupportedException {
    super.clone();
    BindingFrameStack bindingFrameStack = new BindingFrameStack();
    bindingFrameStack.bindingFrames.addAll(this.bindingFrames);
    return bindingFrameStack;
  }
}
