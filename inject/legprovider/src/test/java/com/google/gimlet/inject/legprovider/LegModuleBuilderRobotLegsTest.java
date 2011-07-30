// Copyright 2011 Google Inc.  All Rights Reserved 

package com.google.gimlet.inject.legprovider;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import junit.framework.TestCase;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Tests the {@link LegModuleBuilder} class.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public class LegModuleBuilderRobotLegsTest extends TestCase {

  public void testRobotLegsWithLegModuleBuilder() {
    Injector injector = Guice.createInjector(
        new LegModuleBuilder()
            .bind(Leg.class)
            .annotatedWith(Left.class)
            .usingInstance(new Foot("leftie"))
            .build(),
        new LegModuleBuilder()
            .bind(Leg.class)
            .annotatedWith(Right.class)
            .usingInstance(new Foot("righty"))
            .build());

    Robot robot = injector.getInstance(Robot.class);
    assertSame("leftie", robot.leftLeg.foot.name);
    assertSame("righty", robot.rightLeg.foot.name);
  }

  static class Robot {
    private final Leg leftLeg;
    private final Leg rightLeg;

    @Inject Robot(@Left Leg leftLeg, @Right Leg rightLeg) {
      this.leftLeg = leftLeg;
      this.rightLeg = rightLeg;
    }

    @Override public String toString() {
      return "ROBOT\n" + "  " + leftLeg + "\n" + "  " + rightLeg + "\n";
    }
  }

  @Retention(RUNTIME)
  @Target(PARAMETER)
  @BindingAnnotation
  @interface Left {}

  @Retention(RUNTIME)
  @Target(PARAMETER)
  @BindingAnnotation
  @interface Right {}

  static class Foot {
    final String name;

    Foot(String name) {
      this.name = name;
    }

    @Override public String toString() {
      return "foot(" + name + ")";
    }
  }

  static class Leg {
    private final Foot foot;

    @Inject Leg(@com.google.gimlet.inject.legprovider.Foot Foot foot) {
      this.foot = foot;
    }

    @Override public String toString() {
      return "thigh-knee-calf-" + foot;
    }
  }
}