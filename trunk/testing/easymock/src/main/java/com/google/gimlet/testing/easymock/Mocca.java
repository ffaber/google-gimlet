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

package com.google.gimlet.testing.easymock;

import com.google.common.collect.Sets;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import java.util.Set;

/**
 * This class facilitates creation and management of mocks.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class Mocca {
  private final Set<IMocksControl> controls = Sets.newLinkedHashSet();

  public <T> T createMock(Class<T> clazz) {
    return createControl().createMock(clazz);
  }

  public IMocksControl createControl() {
    return addControl(EasyMock.createControl());
  }

  public <C extends IMocksControl> C addControl(C control) {
    controls.add(control);
    return control;
  }

  public void replayAll() {
    for (IMocksControl control : controls) {
      control.replay();
    }
  }

  public void verifyAll() {
    for (IMocksControl control : controls) {
      control.verify();
    }
  }

  public void resetAll() {
    for (IMocksControl control : controls) {
      control.reset();
    }
  }
}
