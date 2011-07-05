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

package com.google.gimlet.reflect;

import static com.google.gimlet.testing.tl4j.GimletAsserts.assertContentsInOrder;

import junit.framework.TestCase;

import java.util.List;

/**
 * Tests the {@link GetClassGraph} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class GetClassGraphTest extends TestCase {

  /**
   * The hierarchy is:
   *                   GrandFather
   *                        ^
   *            GreatAunt   |   GreatUncle
   *                ^  ^   ^   ^
   *                |   \  |  /
   *              Aunt  Father   Cousin
   *                ^     ^     ^
   *                 \    |   /
   *                    Child
   */
  class GrandFatherClass { }
  interface GreatAuntInterface { }
  interface GreatUncleInterface { }
  class FatherClass
      extends GrandFatherClass
      implements GreatAuntInterface, GreatUncleInterface { }
  interface AuntInterface extends GreatAuntInterface { }
  interface CousinInterface { }
  class ChildClass
      extends FatherClass
      implements AuntInterface, CousinInterface { }

  public void testGetClassGraph_grandFatherClass() {
    List<Class<?>> graphLabels =
        getBreadthFirstVistedLabels(GrandFatherClass.class);
    assertContentsInOrder(
        graphLabels,
        GrandFatherClass.class,
        Object.class);
  }

  public void testGetClassGraph_fatherClass() {
    List<Class<?>> graphLabels = getBreadthFirstVistedLabels(FatherClass.class);
    assertContentsInOrder(
        graphLabels,
        FatherClass.class,
        GrandFatherClass.class,
        GreatAuntInterface.class,
        GreatUncleInterface.class,
        Object.class);
  }

  public void testGetClassGraph_childClass() {
    List<Class<?>> graphLabels = getBreadthFirstVistedLabels(ChildClass.class);
    assertContentsInOrder(
        graphLabels,
        ChildClass.class,
        FatherClass.class,
        AuntInterface.class,
        CousinInterface.class,
        GrandFatherClass.class,
        GreatAuntInterface.class,
        GreatUncleInterface.class,
        Object.class);
  }

  public void testGetClassGraph_degenerativeCaseOfObject() {
    List<Class<?>> graphLabels = getBreadthFirstVistedLabels(Object.class);
    assertContentsInOrder(graphLabels, Object.class);
  }

  private List<Class<?>> getBreadthFirstVistedLabels(Class<?> rootClass) {
    GetClassGraph getClassGraph = new GetClassGraph();
    ClassGraph classGraph = getClassGraph.apply(rootClass);
    return classGraph.getNodesViaBreadthFirstSearch(rootClass);
  }
}