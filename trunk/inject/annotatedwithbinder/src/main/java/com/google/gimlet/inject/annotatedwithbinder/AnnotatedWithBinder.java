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

package com.google.gimlet.inject.annotatedwithbinder;

import static com.google.gimlet.inject.annotatedwithbinder.AnnotatedWithBinder.TypeDef.newTypeDef;
import static com.google.gimlet.inject.annotatedwithbinder.AnnotatedWithBinder.TypeDefFlagBinder.newTypeDefFlagBinder;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/**
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class AnnotatedWithBinder {
  private AnnotatedWithBinder() { }

  private static final Logger logger =
      Logger.getLogger(AnnotatedWithBinder.class.getCanonicalName());

  // input => enum { DESC_1, DESC_2, ..., DESC_N }
  // plus an annotation class @UsedAsSomething

  // AnnotatedWithBinder
  //    .newAnnotatedWithBinder(binder())
  //    .bind(Thing.class)
  //    .annotatedWith(UsedAsSomething.class, DESC_1)
  //    ...;
  //

  // what we want to do is to be sure we can take an annotation and then
  // dynamically create the instance thereof.

  // so given an annotation


  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  @BindingAnnotation
  @interface UsedToBindStuff {
    Point value();

    enum Point { P1, P2 }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    class UsedToBindStuffImpl implements UsedToBindStuff {

      @Override public Point value() {
        return null;
      }

      @Override public Class<? extends Annotation> annotationType() {
        return null;
      }
    }

    UsedToBindStuff usedToBindStuff = new UsedToBindStuffImpl();
  }

  public void doNothing(@UsedToBindStuff(UsedToBindStuff.Point.P1) Object point1) {
    UsedToBindStuff usedToBindStuff2 = UsedToBindStuff.usedToBindStuff;
  }

  interface TypeDef2<T> {
  }


    static class TypeDef<T> /* extends ValueType */ {
      // TODO(ffaber): if we have a SettableTypeDef, that means we should be
      // able to assign this field through the binder.
      // static class BigtableName extends SettableTypeDef<T> { }

      @Nullable private final T value;

      protected TypeDef() { this(null); }

      protected TypeDef(T value) {
        this.value = value;
      }

      static <T, D extends TypeDef<T>> D newTypeDef(
          Class<D> typeDefClass, T value)
          throws IllegalAccessException, InstantiationException,
          NoSuchFieldException {
        System.err.println("callspot: " + Throwables.getStackTraceAsString(new Exception()));
        D typeDef;
        try {
          Constructor<? extends TypeDef<?>> constructor =
              typeDefClass.getDeclaredConstructor(value.getClass());
          constructor.setAccessible(true);
          typeDef = (D) constructor.newInstance(value);
        } catch (Exception e) {
          typeDef = typeDefClass.newInstance();
          Field valueField =
              typeDefClass.getSuperclass().getDeclaredField("value");
          valueField.setAccessible(true);
          valueField.set(typeDef, value);
        }
        return typeDef;
      }

      public T value() {
        return value;
      }
      
      @Override public final String toString() {
        return Objects.toStringHelper(getClass())
            .add("value", value)
            .toString();
      }

      @Override public final int hashCode() {
        return Objects.hashCode(value);
      }

      @Override public final boolean equals(Object other) {
        if (this == other) {
          return true;
        }

        if (other == null || !(other.getClass() == this.getClass())) {
          return false;
        }

        return Objects.equal(
            this.value, ((TypeDef) other).value);
      }
    }

  static final class BigtableName extends TypeDef<String> {
    BigtableName(String underlyingValue) {
      super(underlyingValue);
    }
  }

  static final class NumberOfOssThreads extends TypeDef<Integer> {
    NumberOfOssThreads(Integer underlyingValue) {
      super(underlyingValue);
    }
  }

  static final class NumberOfOssThreads2 extends TypeDef<Integer> { }
  
  void instantiateTypeDefManually() {
  }

  // Flag binder replacement.
  // @SomeRealAnnotation
  // private final static Flag<Type> someFlag = ...;

  // @SomeRealAnnotation(GIVES_THREAD_COUNT)
  // private final static Flag<Type> someFlag = ...;

  // @BindAsTypeDef(TypeDefClass.class)
  // private final static Flag<Type> someFlag = ...;

  // TypeDefFlagBinder.bindTypeDefFlags(getClass());

  static class Flag<T> {

    static <T> Flag<T> value(T defaultValue) {
      return new Flag<T>(defaultValue);
    }

    private final T defaultValue;

    Flag(T defaultValue) {
      this.defaultValue = defaultValue ;
    }

    public T getValue() {
      return defaultValue; // for now
    }
  }

  private static final Flag<Integer> numberOfOssThreads = Flag.value(100);
  private static final Flag<String> bigtableName =
      Flag.value("/bigtable/mix-gd/some-table");

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface BindAsTypeDef {
    Class<? extends TypeDef<?>> value();
  }
  
  static class TypeDefFlagBinder {
    static TypeDefFlagBinder newTypeDefFlagBinder(Binder binder) {
      return new TypeDefFlagBinder(binder);
    }

    private final Binder binder;

    private TypeDefFlagBinder(Binder binder) {
      this.binder = binder.skipSources(getClass());
    }

    void bindTypeDefFlags(Class<?> clazz) {
      try {
        innerBindTypeDefFlags(clazz);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    void innerBindTypeDefFlags(Class<?> clazz)
        throws IllegalAccessException, NoSuchMethodException,
        InvocationTargetException, InstantiationException,
        NoSuchFieldException {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
        BindAsTypeDef bindAsTypeDef = field.getAnnotation(BindAsTypeDef.class);
        if (bindAsTypeDef == null) {
          continue;
        }
        
        int modifierMask = field.getModifiers();
        
        boolean isFinal = (Modifier.FINAL & modifierMask) > 0;
        if (!isFinal) {
          System.err.println("Your flags should be final");
        }
        
        boolean isPrivate = (Modifier.PRIVATE & modifierMask) > 0;
        if (!isPrivate) {
          System.err.println("Your flags should be private");
        }

        field.setAccessible(true);
        Object flagField = field.get(null);
        if (!(flagField instanceof Flag)) {
          System.err.println("Wrongly annotated field: " + flagField);
          continue;
        }

        Flag<?> flag = (Flag) flagField;
        Object flagValue = flag.getValue();
        Class<? extends TypeDef<?>> typeDefClass = bindAsTypeDef.value();
        // TODO(ffaber): if the typedef is of type 'SettableTypeDef' and doesn't
        // have a ctr to use, then just set the field directly.

        TypeDef<?> typeDef = (TypeDef<?>) newTypeDef((Class) typeDefClass, flagValue);

        System.err.println("typedef is: " + typeDef);
        System.err.println("typedefClass is: " + typeDefClass);
        System.err.println("typedefClass x 2 is: " + typeDef.getClass());
        // Then we need to get an instance of the subclass from this shit.
        bindTypeDef(typeDefClass, typeDef);
      }
    }

    void useCglib(final TypeDef<?> typeDef, Class<? extends TypeDef<?>> typeDefClass) {
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(typeDefClass);
      enhancer.setCallback(new MethodInterceptor() {
        @Override public Object intercept(
            Object obj, Method method, Object[] args, MethodProxy proxy)
            throws Throwable {
          return method.invoke(typeDef, args);
        }
      });
      TypeDef<?> enhancedTypeDef = (TypeDef<?>) enhancer.create();
      System.err.println("typedefClass x 3 is: " + enhancedTypeDef.getClass());
    }

    @SuppressWarnings("unchecked")
    void bindTypeDef(
        Class<? extends TypeDef<?>> typeDefClass, TypeDef<?> typeDef) {
      binder.bind((Class) typeDefClass).toInstance(typeDef);
    }
  }

  private static TypeDef<?> newTypeDef1(
      Object flagValue,
      Class<? extends TypeDef<?>> typeDefClass)
      throws InstantiationException, IllegalAccessException,
      NoSuchFieldException {
    TypeDef<?> typeDef;
    try {
      Constructor<? extends TypeDef<?>> constructor =
          typeDefClass.getDeclaredConstructor(flagValue.getClass());
      constructor.setAccessible(true);
      typeDef = constructor.newInstance(flagValue);
    } catch (Exception e) {
      System.err.println("exception below:");
      e.printStackTrace();
//          Constructor<?> constructor =
//              typeDefClass.getSuperclass().getDeclaredConstructor();
//          constructor.setAccessible(true);
//          typeDef = (TypeDef<?>) constructor.newInstance();

      typeDef = typeDefClass.newInstance();
      Field valueField =
          typeDefClass.getSuperclass().getDeclaredField("value");
      valueField.setAccessible(true);
      valueField.set(typeDef, flagValue);
    }
    return typeDef;
  }

  static class TypeDefUsingModule extends AbstractModule {
    @BindAsTypeDef(NumberOfOssThreads2.class)
    private static final Flag<Integer> numberOfOssThreads2 = Flag.value(100);

    @BindAsTypeDef(BigtableName.class)
    private static final Flag<String> bigtableName2 =
        Flag.value("/bigtable/mix-gd/some-table");

    @Override protected void configure() {
      newTypeDefFlagBinder(binder()).bindTypeDefFlags(getClass());
    }
  }

  public static void main(String[] args) throws Exception {
    NumberOfOssThreads2 numberOfOssThreads2 = new NumberOfOssThreads2();
    System.err.println("numberOfOssThreads2: " + numberOfOssThreads2);

    Injector injector = Guice.createInjector(new TypeDefUsingModule());
    GetsInjectedWithStuff getsInjectedWithStuff =
        injector.getInstance(GetsInjectedWithStuff.class);
    getsInjectedWithStuff.doFakeStuff();
    
    NumberOfOssThreads2 manuallyCreatedNumberOfOssThreads2 =
        NumberOfOssThreads2.newTypeDef(NumberOfOssThreads2.class, 3);
    BigtableName manuallyCreatedBigtableName =
        newTypeDef(BigtableName.class, "bigbigtable");
    GetsInjectedWithStuff manuallyCreatedGetsInjectedWithStuff =
        new GetsInjectedWithStuff(
            manuallyCreatedNumberOfOssThreads2,
            manuallyCreatedBigtableName);
    manuallyCreatedGetsInjectedWithStuff.doFakeStuff();
  }


  static class GetsInjectedWithStuff {
    private final NumberOfOssThreads2 numberOfOssThreads;
    private final BigtableName bigtableName;

    @Inject GetsInjectedWithStuff(
        NumberOfOssThreads2 numberOfOssThreads,
        BigtableName bigtableName) {
      this.numberOfOssThreads = numberOfOssThreads;
      this.bigtableName = bigtableName;
    }

    public void doFakeStuff() {
      System.err.println("Bigtable name: " + bigtableName);
      System.err.println("Bigtable name: " + bigtableName.value());
      System.err.println("Number of threads: " + numberOfOssThreads);
      System.err.println("Number of threads: " + numberOfOssThreads.value());
    }

    public void doStuff() {
      fakeOpenBigtable(bigtableName.value());
      Executors.newFixedThreadPool(numberOfOssThreads.value());
    }

    public void fakeOpenBigtable(String bigtableName) {
    }
  }

}
