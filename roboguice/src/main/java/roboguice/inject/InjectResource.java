/*
 * Copyright 2009 Michael Burton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.inject;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a variable member of a class (whether static or not) should be
 * injected with an Android resource.
 *
 * The value corresponds to the id of the resource.<br />
 *
 * You may specify the name of the resource instead of the id using {@link #name()},
 * which will use {@link android.content.res.Resources#getIdentifier(String, String, String)} to
 * resolve the resource by name.
 *
 * Usage example:<br />
 * {@code @InjectResource(R.string.hello) protected String hello;} <br/>
 * {@code @InjectResource(name="com.myapp:string/hello") protected String hello;}
 *
 * @author Mike Burton
 */
@Retention(RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@BindingAnnotation
public @interface InjectResource {
    int value() default -1;
    String name() default "";
}
