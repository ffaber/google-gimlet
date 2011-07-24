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

package com.google.gimlet.inject.legprovider;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import junit.framework.TestCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the {@link LegModuleBuilderTest} class.
 *
 */
public class LegModuleBuilderTest extends TestCase {

  private interface OneConfigurableParamInterface {
    Double getNonConfigurableParam();
    Integer getConfigurableParam();
  }

  // Classes below all have a non-configurable parameter injected into them.
  // This is done purposely in order to test that non configurable components
  // are injected normally in all cases.
  private static class OneConfigurableParam
      implements OneConfigurableParamInterface {

    Double nonConfigurableParam;
    Integer configurableParam;

    @Inject
    OneConfigurableParam(
        Double nonConfigurableParam,
        @Leg Integer configurableParam) {
      this.nonConfigurableParam = nonConfigurableParam;
      this.configurableParam = configurableParam;
    }

    @Override
    public Double getNonConfigurableParam() {
      return nonConfigurableParam;
    }

    @Override
    public Integer getConfigurableParam() {
      return configurableParam;
    }
  }

  private static class TwoConfigurableParams {

    Double nonConfigurableParam;
    Integer configurableParam1;
    String configurableParam2;

    @Inject TwoConfigurableParams(
        Double nonConfigurableParam,
        @Leg Integer configurableParam1,
        @Leg String configurableParam2) {
      this.nonConfigurableParam = nonConfigurableParam;
      this.configurableParam1 = configurableParam1;
      this.configurableParam2 = configurableParam2;
    }
  }

  private static class TwoConfigurableParamsOfSameType {

    Double nonConfigurableParam;
    Integer configurableParam1;
    Integer configurableParam2;

    @Inject TwoConfigurableParamsOfSameType(
        Double nonConfigurableParam,
        @Leg("one") Integer configurableParam1,
        @Leg("otherone") Integer configurableParam2) {
      this.nonConfigurableParam = nonConfigurableParam;
      this.configurableParam1 = configurableParam1;
      this.configurableParam2 = configurableParam2;
    }
  }

  private static class ConfigurableParameterThroughProvider {

    Double nonConfigurableParam;
    Integer configurableParam;

    @Inject ConfigurableParameterThroughProvider(
        Double nonConfigurableParam,
        @Leg Provider<Integer> configurableParamProvider) {
      this.nonConfigurableParam = nonConfigurableParam;
      this.configurableParam = configurableParamProvider.get();
    }
  }

  private static class MoreThanOneConstructorMarkedWithInject {

    @Inject MoreThanOneConstructorMarkedWithInject(
        @Leg Integer configurableParam) {}

    @Inject MoreThanOneConstructorMarkedWithInject(
        @Leg String configurableParam) {}
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.PARAMETER})
  @BindingAnnotation
  private @interface IsAnotherInteger {}

  private static final Integer INTEGER_VALUE = 0;
  private static final Integer ANOTHER_INTEGER_VALUE = 1;
  private static final String STRING_VALUE = "This is a string value";
  private static final Double DOUBLE_VALUE = 0.1;

  private static final Key<Integer> INTEGER_KEY = Key.get(Integer.class);
  private static final Key<Integer> ANOTHER_INTEGER_KEY =
      Key.get(Integer.class, IsAnotherInteger.class);
  private static final Key<String> STRING_KEY = Key.get(String.class);

  // This module provides test bindings, whose keys can be used as input for
  // the configurable parameters of test classes.
  private static final Module SEED_MODULE = new AbstractModule() {
    @Override
    protected void configure() {
      bind(INTEGER_KEY).toInstance(INTEGER_VALUE);
      bind(ANOTHER_INTEGER_KEY).toInstance(ANOTHER_INTEGER_VALUE);
      bind(STRING_KEY).toInstance(STRING_VALUE);
      bind(Double.class).toInstance(DOUBLE_VALUE);
    }
  };

  public void testOneConfigurableParam() {
    Injector injector = getInjector(
        new LegModuleBuilder()
            .implement(OneConfigurableParam.class)
            .using(INTEGER_KEY)
            .build());
    OneConfigurableParam instance =
        injector.getInstance(OneConfigurableParam.class);

    assertEquals(DOUBLE_VALUE, instance.nonConfigurableParam);
    assertEquals(INTEGER_VALUE, instance.configurableParam);
  }

  public void testOneConfigurableParam_multipleConfigurations() {
    Named named1 = Names.named("1");
    Named named2 = Names.named("2");

    Injector injector = getInjector(
        new LegModuleBuilder()
            .implement(OneConfigurableParam.class)
            .using(INTEGER_KEY)
            .build(named1),
        new LegModuleBuilder()
            .implement(OneConfigurableParam.class)
            .using(INTEGER_KEY)
            .build(named2));

    OneConfigurableParam instance1 =
        injector.getInstance(Key.get(OneConfigurableParam.class, named1));
    assertEquals(DOUBLE_VALUE, instance1.nonConfigurableParam);
    assertEquals(INTEGER_VALUE, instance1.configurableParam);

    OneConfigurableParam instance2 =
        injector.getInstance(Key.get(OneConfigurableParam.class, named2));
    assertEquals(DOUBLE_VALUE, instance2.nonConfigurableParam);
    assertEquals(INTEGER_VALUE, instance2.configurableParam);
  }

  public void testTwoConfigurableParams() {
    Injector injector = getInjector(
        new LegModuleBuilder()
            .implement(TwoConfigurableParams.class)
            .using(INTEGER_KEY)
            .using(STRING_KEY)
            .build());
    TwoConfigurableParams instance =
        injector.getInstance(TwoConfigurableParams.class);

    assertEquals(DOUBLE_VALUE, instance.nonConfigurableParam);
    assertEquals(INTEGER_VALUE, instance.configurableParam1);
    assertEquals(STRING_VALUE, instance.configurableParam2);
  }

  public void testTwoConfigurableParamsOfSameType() {
    Injector injector = getInjector(
        new LegModuleBuilder()
            .implement(TwoConfigurableParamsOfSameType.class)
            .using(INTEGER_KEY).forLeg("one")
            .using(ANOTHER_INTEGER_KEY).forLeg("otherone")
            .build());
    TwoConfigurableParamsOfSameType instance =
        injector.getInstance(TwoConfigurableParamsOfSameType.class);

    assertEquals(DOUBLE_VALUE, instance.nonConfigurableParam);
    assertEquals(INTEGER_VALUE, instance.configurableParam1);
    assertEquals(ANOTHER_INTEGER_VALUE, instance.configurableParam2);
  }

  public void testConfigurableParameterThroughProvider() {
    Injector injector = getInjector(
        new LegModuleBuilder()
            .implement(ConfigurableParameterThroughProvider.class)
            .using(INTEGER_KEY)
            .build());
    ConfigurableParameterThroughProvider instance =
        injector.getInstance(ConfigurableParameterThroughProvider.class);

    assertEquals(DOUBLE_VALUE, instance.nonConfigurableParam);
    assertEquals(INTEGER_VALUE, instance.configurableParam);
  }

  public void testBindingInterfaceToConfigurableClass() {
    Injector injector = getInjector(
        new LegModuleBuilder()
            .implement(
                OneConfigurableParamInterface.class, OneConfigurableParam.class)
            .using(INTEGER_KEY)
            .build());
    OneConfigurableParamInterface instance =
        injector.getInstance(OneConfigurableParamInterface.class);

    assertEquals(DOUBLE_VALUE, instance.getNonConfigurableParam());
    assertEquals(INTEGER_VALUE, instance.getConfigurableParam());
  }

  public void testConfigurationKeysInMixedOrder() {
    Injector injector = getInjector(
        new LegModuleBuilder()
            .implement(TwoConfigurableParams.class)
            .using(STRING_KEY)
            .using(INTEGER_KEY)
            .build());
    TwoConfigurableParams instance =
        injector.getInstance(TwoConfigurableParams.class);

    assertEquals(DOUBLE_VALUE, instance.nonConfigurableParam);
    assertEquals(INTEGER_VALUE, instance.configurableParam1);
    assertEquals(STRING_VALUE, instance.configurableParam2);
  }

  public void testNotEnoughValueKeysSupplied() {
    try {
      getInjector(
          new LegModuleBuilder()
            .implement(TwoConfigurableParams.class)
            .using(INTEGER_KEY)
            .build());
      fail();
    } catch (IllegalStateException expected) {}
  }

  public void testTooManyValueKeys() {
    try {
      getInjector(
          new LegModuleBuilder()
            .implement(OneConfigurableParam.class)
            .using(STRING_KEY)
            .using(INTEGER_KEY)
            .build());
      fail();
    } catch (IllegalStateException expected) {}
  }

  public void testClassHasMoreThanOneCtr() {
    try {
      getInjector(
          new LegModuleBuilder()
            .implement(MoreThanOneConstructorMarkedWithInject.class)
            .using(STRING_KEY)
            .build());
      fail();
    } catch (ConfigurationException expected) {}
  }

  public void testChangingProviderValuesReturnSameConfiguredInstance() {
    Module customSeedModule = new AbstractModule() {
      @Override
      protected void configure() {
        bind(Double.class).toInstance(DOUBLE_VALUE);
        bind(INTEGER_KEY)
            .toProvider(new Provider<Integer>() {

              int i = 0;

              @Override
              public Integer get() {
                return i++;
              }
            })
            .in(Singleton.class);
      }
    };

    Injector injector = Guice.createInjector(
        customSeedModule,
        new LegModuleBuilder()
            .implement(OneConfigurableParam.class)
            .using(INTEGER_KEY)
            .build());

    OneConfigurableParam instance1 =
        injector.getInstance(OneConfigurableParam.class);
    assertEquals(DOUBLE_VALUE, instance1.nonConfigurableParam);
    assertEquals(0, instance1.configurableParam.intValue());

    OneConfigurableParam instance2 =
        injector.getInstance(OneConfigurableParam.class);
    assertEquals(DOUBLE_VALUE, instance2.nonConfigurableParam);
    assertEquals(0, instance2.configurableParam.intValue());
  }

  private <T> Injector getInjector(Module... modules) {
    List<Module> allModules = Lists.newArrayList(Arrays.asList(modules));
    allModules.add(SEED_MODULE);
    return Guice.createInjector(allModules);
  }
}
