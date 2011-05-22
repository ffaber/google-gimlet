// Copyright 2011 Google Inc.  All Rights Reserved 

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