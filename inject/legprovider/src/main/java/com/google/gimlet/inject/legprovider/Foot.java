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

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.xml.bind.annotation.XmlElement.DEFAULT;

/**
 * A constructor parameter annotated with {@code Leg} indicates that the param
 * is a foot (of potentially several feet) of a "robot leg."
 *
 * @author ffaber@gmail.com (Fred Faber)
 * @see <a href="http://code.google.com/p/google-gimlet/wiki/GimletLegProvider">
 *   http://code.google.com/p/google-gimlet/wiki/GimletLegProvider</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@BindingAnnotation
public @interface Foot {
  String DEFAULT_FOOT_LABEL = "";

  String value() default DEFAULT_FOOT_LABEL;
}
