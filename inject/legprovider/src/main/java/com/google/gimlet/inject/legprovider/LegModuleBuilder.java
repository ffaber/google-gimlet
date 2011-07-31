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
import static com.google.common.collect.Iterables.getLast;

import com.google.common.collect.Sets;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;

/**
 * This class builds modules for a leg of a configurable class. There is a clear
 * conceptual overlap between this class and {@code FactoryModuleBuilder}. In
 * the same way that {@code FactoryModuleBuilder} offers factories that use
 * runtime args to configure an injectable instance, {@link LegModuleBuilder}
 * offers providers that use args available within a {@code configure()} method.
 *
 * <p>Note that in this context, "runtime" means behavior that happens after
 * the application's injector is created.
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
 * <p>An example is worth a thousand words. Please have a look at the
 * <a href="https://code.google.com/p/google-gimlet/wiki/GimletLegProvider">
 * leg provider wiki</a> page for an illustration.
 *
 */
public class LegModuleBuilder {

  // The mixins below help to enforce the following rules:
  // if we call .bind(), we can call:  annotatedWith(), to(), using()
  // if we call .annotatedWith(), we can call:  to(), using()
  // if we call .to(), we can call: using()
  // if we call using(), we can call: using(), forFoot(), build()

  interface UsingMixin<T> {
    ReturnedFromCallToUsing<T> using(Class<?> clazz);
    ReturnedFromCallToUsing<T> using(
        Class<?> clazz, Class<? extends Annotation> annotationClazz);
    ReturnedFromCallToUsing<T> using(Class<?> clazz, Annotation annotation);
    ReturnedFromCallToUsing<T> using(TypeLiteral<?> valueTypeLiteral);
    ReturnedFromCallToUsing<T> using(
        TypeLiteral<?> valueTypeLiteral,
        Class<? extends Annotation> annotationClazz);
    ReturnedFromCallToUsing<T> using(
        TypeLiteral<?> valueTypeLiteral, Annotation annotation);
    ReturnedFromCallToUsing<T> using(Key<?> valueKey);
    ReturnedFromCallToUsing<T> usingInstance(Object instance);
  }

  interface ForFootMixin<T> extends UsingMixin<T> {
    ReturnedFromCallToForFoot<T> forFoot(String footName);
  }

  interface AnnotatedWithMixin<T> {
    ReturnedFromCallToAnnotatedWith<T> annotatedWith(
        Class<? extends Annotation> annotationClass);
    ReturnedFromCallToAnnotatedWith<T> annotatedWith(
        Annotation annotation);
  }

  interface ToMixin<T> {
    ReturnedFromCallToTo<T> to(Class<? extends T> implementationClass);
    ReturnedFromCallToTo<T> to(
        TypeLiteral<? extends T> implementationTypeLiteral);
  }

  interface BuildMixin {
    Module build();
  }

  public interface ReturnedFromCallToBind<T> extends
      AnnotatedWithMixin<T>,
      ToMixin<T>,
      UsingMixin<T> {
  }

  public interface ReturnedFromCallToAnnotatedWith<T> extends
      ToMixin<T>,
      UsingMixin<T> {
  }

  public interface ReturnedFromCallToTo<T> extends
      UsingMixin<T> {
  }

  public interface ReturnedFromCallToUsing<T> extends
      BuildMixin,
      UsingMixin<T>,
      ForFootMixin<T> {
  }

  public interface ReturnedFromCallToForFoot<T> extends
      BuildMixin,
      UsingMixin<T> {
  }


  public <T> ReturnedFromCallToBind<T> bind(Class<T> interfaceType) {
    return bind(TypeLiteral.get(interfaceType));
  }

  public <T> ReturnedFromCallToBind<T> bind(TypeLiteral<T> interfaceType) {
    return new BindingBuilder<T>(interfaceType);
  }

  static class BindingBuilder<T> implements
      ReturnedFromCallToBind<T>,
      ReturnedFromCallToAnnotatedWith<T>,
      ReturnedFromCallToTo<T>,
      ReturnedFromCallToUsing<T>,
      ReturnedFromCallToForFoot<T> {

    private final LinkedHashSet<KeyOrInstanceUnionWithLabel<?>> valueSet =
        Sets.newLinkedHashSet();
    private Key<T> returnType;
    private TypeLiteral<? extends T> implementationType;

    private BindingBuilder(TypeLiteral<T> interfaceType) {
      this.returnType = Key.get(interfaceType);
      this.implementationType = interfaceType;
    }

    @Override public ReturnedFromCallToAnnotatedWith<T> annotatedWith(
        Class<? extends Annotation> annotationClass) {
      returnType = Key.get(returnType.getTypeLiteral(), annotationClass);
      return this;
    }

    @Override public ReturnedFromCallToAnnotatedWith<T> annotatedWith(
        Annotation annotation) {
      returnType = Key.get(returnType.getTypeLiteral(), annotation);
      return this;
    }

    @Override public ReturnedFromCallToTo<T> to(
        Class<? extends T> implementationClass) {
      return to(TypeLiteral.get(implementationClass));
    }

    @Override public ReturnedFromCallToTo<T> to(
        TypeLiteral<? extends T> implementationType) {
      this.implementationType = implementationType;
      return this;
    }

    @Override public ReturnedFromCallToUsing<T> using(Class<?> clazz) {
      return using(TypeLiteral.get(clazz));
    }

    @Override public ReturnedFromCallToUsing<T> using(
        TypeLiteral<?> valueTypeLiteral) {
      return using(Key.get(valueTypeLiteral));
    }

    @Override public ReturnedFromCallToUsing<T> using(
        Class<?> clazz,
        Class<? extends Annotation> annotationClazz) {
      return using(TypeLiteral.get(clazz), annotationClazz);
    }

    @Override public ReturnedFromCallToUsing<T> using(
        TypeLiteral<?> valueTypeLiteral,
        Class<? extends Annotation> annotationClazz) {
      return using(Key.get(valueTypeLiteral, annotationClazz));
    }

    @Override public ReturnedFromCallToUsing<T> using(
        Class<?> clazz,
        Annotation annotation) {
      return using(TypeLiteral.get(clazz), annotation);
    }

    @Override public ReturnedFromCallToUsing<T> using(
        TypeLiteral<?> valueTypeLiteral,
        Annotation annotation) {
      return using(Key.get(valueTypeLiteral, annotation));
    }

    @Override public ReturnedFromCallToUsing<T> using(Key<?> valueKey) {
      return using(KeyOrInstanceUnionWithLabel.ofKey(valueKey));
    }

    @Override public ReturnedFromCallToUsing<T> usingInstance(Object instance) {
      return using(KeyOrInstanceUnionWithLabel.ofInstance(instance));
    }

    private ReturnedFromCallToUsing<T> using(
        KeyOrInstanceUnionWithLabel<?> unionWithLabel) {
      // if in the midst of a call, add the old one, hold open the new one
      checkState(
          !valueSet.contains(unionWithLabel),
          "%s is a duplicate configuration parameter within %s",
          unionWithLabel, valueSet);

      valueSet.add(unionWithLabel);
      return this;
    }

    @Override public ReturnedFromCallToForFoot<T> forFoot(
        String footName) {
      KeyOrInstanceUnionWithLabel<?> lastEntry = getLast(valueSet);
      valueSet.remove(lastEntry);

      final KeyOrInstanceUnionWithLabel<?> thisEntry;
      if (lastEntry.key != null) {
        thisEntry = KeyOrInstanceUnionWithLabel.ofKey(lastEntry.key, footName);
      } else {
        thisEntry = KeyOrInstanceUnionWithLabel.ofInstance(
            lastEntry.instance, footName);
      }

      using(thisEntry);
      return this;
    }

    @Override public Module build() {
      return new LegBinder<T>(implementationType, valueSet).bindTo(returnType);
    }
  }
}
