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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Sets;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * This class builds modules for a leg of a configurable class. There is a clear
 * conceptual overlap between this class and {@code FactoryModuleBuilder}. In
 * the same way that {@code FactoryModuleBuilder} offers factories that use
 * runtime args to configure an injectable instance, {@link LegModuleBuilder}
 * offers providers that use args available within a {@code configure()} method.
 * As such, there might be room for future integration between this package
 * and the Guice extension, {@code assistedinject}.
 *
 * <p>For the use case when an implementation of an interface requires runtime
 * paramaters in order to get initialized, {@code FactoryModuleBuilder} allows
 * the client to bind an interface to a factory, which will accept those runtime
 * parameters and return an instance of the desired interface.
 *
 * <p>The use case for {@code LegModuleBuilder} is similar, however, there is no
 * runtime component. Instead the client is trying to set configurable
 * components of a class to a particular set of {@link Key}s.
 *
 * <p>An example is worth a thousand words. So lets take a look at one.
 *
 * <p>Lets say that we have to ways of generating strings. One simply generates
 * unique ids and the other generates slightly more clever names.
 *
 * <pre>
 * public class BoringNameProvider implements Provider<String> {
 *   int count = 0;
 *
 *   {@literal @}Override
 *   public String get() {
 *     return String.valueOf(count++);
 *   }
 * }
 *
 * public class FunNameProvider implements Provider<String> {
 *
 *   final static String[] COOL_NAMES = {
 *     "B. A. Baracus", "Dirty Harry", "James Bond"};
 *
 *   int count = 0;
 *   int[] NAME_SUFFIX = new int[COOL_NAMES.length];
 *
 *   FunNameProvider() {
 *     Arrays.fill(NAME_SUFFIX, 0);
 *   }
 *
 *   {@literal @}Override
 *   public String get() {
 *     int index = count++ % COOL_NAMES.length
 *     return COOL_NAMES[index] + COUNT[index]++;
 *   }
 * }
 * </pre>
 *
 * <p>The naming providers are bound in their own module and can be used
 * anywhere in the application.
 *
 * <pre>
 * public NamingModule extends AbstractModule {
 *   {@literal @}Override
 *   protected void configure() {
 *     bind(String.class)
 *       .annotatedWith(IsBoringName.class)
 *       .toProvider(BoringNameProvider);
 *     bind(String.class)
 *       .annotatedWith(IsFunName.class)
 *       .toProvider(FunNameProvider.class);
 *   }
 * }
 * </pre>
 *
 * <h3>Configuring with one configurable parameter</h3>
 *
 * <p>Let's consider a class with configurable components:
 *
 * <pre>
 * public interface Dog {...}
 *
 * public class DogImpl {
 *
 *   {@literal @}Inject
 *   public DogImpl(
 *     Neighborhood neighborhood,
 *     Date date,
 *     {@literal @}Leg String name) {
 *      ...
 *    }
 * }
 * </pre>
 *
 * <p>For argument's sake we'll pretend that this class is used in some
 * application that monitors the streets for dogs and assigns a name to each dog
 * that walks by. {@code Neighborhood} and {@code Date} classes have no role in
 * this example, except to demonstrate that a configurable class can easily
 * support the injection of already bound elements.
 *
 * <p>Now lets see if we can configure our {@code Dog}s to be named in different
 * ways.
 *
 * <pre>
 * public DogObservationModule extends AbstractModule {
 *   {@literal @}Override
 *   protected void configure() {
 *    LegModuleBuilder{@literal <}Dog{@literal >} legModuleBuilder =
 *      new LegModuleBuilder{@literal <}Dog{@literal >}();
 *
 *    install(
 *      legModuleBuilder
 *       .implement(Dog.class, DogImpl.class)
 *       .using(Key.get(String.class, IsBoringName.class))
 *       .build(IsBoringDog.class));
 *
 *    install(
 *      legModuleBuilder
 *       .implement(Dog.class, DogImpl.class)
 *       .using(Key.get(String.class, IsFunName.class))
 *       .build(IsFunDog.class));
 *   }
 * }
 * </pre>
 *
 * <p>We've now successfully been able to reuse the same {@code Dog} class for
 * two different naming schemes.
 *
 * <h3>Configuring multiple with multiple configurable parameters</h3>
 *
 * <p>Let's slightly change the example. Suppose that we would like each dog to
 * have both names associated with it. In other words, our implementation class
 * would look something like this.
 *
 * <pre>
 * public interface Dog {...}
 *
 * public class DogImpl {
 *
 *   {@literal @}Inject
 *   public DogImpl(
 *     Neighborhood neighborhood,
 *     Date date,
 *     {@literal @}Leg("one") String name1,
 *     {@literal @}Leg("two") String name2) {
 *      ...
 *    }
 * }
 * </pre>
 *
 * <p>For the sake of convenience lets say that we would like to have one of the
 * names to be a boring id and the other one to be a slightly more fun name,
 * then we could use the following configuration.
 *
 * <pre>
 * public DogObservationModule extends AbstractModule {
 *   {@literal @}Override
 *   protected void configure() {
 *    LegModuleBuilder{@literal <}Dog{@literal >} legModuleBuilder =
 *      new LegModuleBuilder{@literal <}Dog{@literal >}();
 *
 *    install(
 *      legModuleBuilder
 *       .implement(Dog.class, DogImpl.class)
 *       .using(Key.get(String.class, IsBoringName.class), "one")
 *       .using(Key.get(String.class, IsFunName.class), "two")
 *       .build());
 *   }
 * }
 * </pre>
 *
 * <p>Notice that this time we didn't provide any annotation to the build
 * method. This means that we are configuring an annotated version of
 * {@code Dog}.
 *
 */
public class LegModuleBuilder {

  /** See the leg configuration examples at {@link LegModuleBuilder}. */
  public <T> UsingBuilder<T> implement(Class<T> implementationType) {
    return implement(TypeLiteral.get(implementationType));
  }

  /** See the leg configuration examples at {@link LegModuleBuilder}. */
  public <T> UsingBuilder<T> implement(TypeLiteral<T> implementationType) {
    return implement(implementationType, implementationType);
  }

  /** See the leg configuration examples at {@link LegModuleBuilder}. */
  public <T> UsingBuilder<T> implement(
      Class<T> returnType, Class<? extends T> implementationType) {
    return implement(returnType, TypeLiteral.get(implementationType));
  }

  /** See the leg configuration examples at {@link LegModuleBuilder}. */
  public <T> UsingBuilder<T> implement(
      Class<T> returnType, TypeLiteral<? extends T> implementationType) {
    return implement(TypeLiteral.get(returnType), implementationType);
  }

  /** See the leg configuration examples at {@link LegModuleBuilder}. */
  public <T> UsingBuilder<T> implement(
      TypeLiteral<T> returnType, Class<? extends T> implementationType) {
    return implement(returnType, TypeLiteral.get(implementationType));
  }

  /** See the leg configuration examples at {@link LegModuleBuilder}. */
  public <T> UsingBuilder<T> implement(
      TypeLiteral<T> returnType, TypeLiteral<? extends T> implementationType) {
    checkState(
        returnType != null && implementationType != null,
        "Cannot set implementation more than once.");

    return new UsingBuilder<T>(returnType, implementationType);
  }

  public static class UsingBuilder<T> {
    private final TypeLiteral<T> returnType;
    private final TypeLiteral<? extends T> implementationType;

    final Set<KeyOrInstanceUnionWithLabel<?>> valueSet =
        Sets.newHashSet();

    private UsingBuilder(
        TypeLiteral<T> returnType,
        TypeLiteral<? extends T> implementationType) {
      this.returnType = returnType;
      this.implementationType = implementationType;
    }

    /** See the leg configuration examples at {@link LegModuleBuilder}. */
    public UsingBuilderWithFor<T> using(Class<?> clazz) {
      return using(TypeLiteral.get(clazz));
    }

    public UsingBuilderWithFor<T> usingInstance(Object instance) {
      return using(KeyOrInstanceUnionWithLabel.ofInstance(instance));
    }

    /** See the leg configuration examples at {@link LegModuleBuilder}. */
    public UsingBuilderWithFor<T> using(
        Class<?> clazz, Class<? extends Annotation> annotationClazz) {
      return using(TypeLiteral.get(clazz), annotationClazz);
    }

    /** See the leg configuration examples at {@link LegModuleBuilder}. */
    public UsingBuilderWithFor<T> using(Class<?> clazz, Annotation annotation) {
      return using(TypeLiteral.get(clazz), annotation);
    }

    /** See the leg configuration examples at {@link LegModuleBuilder}. */
    public UsingBuilderWithFor<T> using(TypeLiteral<?> valueTypeLiteral) {
      return using(Key.get(valueTypeLiteral));
    }

    /** See the leg configuration examples at {@link LegModuleBuilder}. */
    public UsingBuilderWithFor<T> using(
        TypeLiteral<?> valueTypeLiteral,
        Class<? extends Annotation> annotationClazz) {
      return using(Key.get(valueTypeLiteral, annotationClazz));
    }

    /** See the leg configuration examples at {@link LegModuleBuilder}. */
    public UsingBuilderWithFor<T> using(
        TypeLiteral<?> valueTypeLiteral,
        Annotation annotation) {
      return using(Key.get(valueTypeLiteral, annotation));
    }

    /** See the leg configuration examples at {@link LegModuleBuilder}. */
    public UsingBuilderWithFor<T> using(Key<?> valueKey) {
      return using(KeyOrInstanceUnionWithLabel.ofKey(valueKey));
    }

    UsingBuilderWithFor<T> using(
        KeyOrInstanceUnionWithLabel<?> unionWithLabel) {
      // if in the midst of a call, add the old one, hold open the new one
      checkState(
          !valueSet.contains(unionWithLabel),
          "%s is a duplicate configuration parameter within %s",
          unionWithLabel, valueSet);

      valueSet.add(unionWithLabel);
      return new UsingBuilderWithFor<T>(this, unionWithLabel);
    }

    public Module build() {
      return build(Key.get(returnType));
    }

    public Module build(Class<? extends Annotation> annotation) {
      return build(Key.get(returnType, annotation));
    }

    public Module build(Annotation annotation) {
      return build(Key.get(returnType, annotation));
    }

    private Module build(Key<T> returnType) {
      return new LegBinder<T>(implementationType, valueSet)
          .bindTo(returnType);
    }
  }

  static class UsingBuilderWithFor<T> extends UsingBuilder<T> {

    private final KeyOrInstanceUnionWithLabel keyOrInstanceUnionWithLabel;

    private UsingBuilderWithFor(
        UsingBuilder<T> usingBuilder,
        KeyOrInstanceUnionWithLabel keyOrInstanceUnionWithLabel) {
      super(usingBuilder.returnType, usingBuilder.implementationType);
      this.keyOrInstanceUnionWithLabel = keyOrInstanceUnionWithLabel;
      this.valueSet.addAll(usingBuilder.valueSet);
    }

    @SuppressWarnings("unchecked")
    public UsingBuilder<T> forLeg(String label) {
      valueSet.remove(keyOrInstanceUnionWithLabel);

      final KeyOrInstanceUnionWithLabel<?> unionThatActuallyHasLabel;
      if (keyOrInstanceUnionWithLabel.key != null) {
        unionThatActuallyHasLabel = KeyOrInstanceUnionWithLabel.ofKey(
            keyOrInstanceUnionWithLabel.key, label);
      } else {
        unionThatActuallyHasLabel = KeyOrInstanceUnionWithLabel.ofInstance(
            keyOrInstanceUnionWithLabel.instance, label);
      }

      using(unionThatActuallyHasLabel);

      return this;
    }
  }
}
