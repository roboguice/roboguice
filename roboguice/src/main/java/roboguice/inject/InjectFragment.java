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
 * Indicates that a variable member of a class should be
 * injected with an Android fragment.
 *
 * The value corresponds to the id of the fragment.<br />
 *
 * As an alternative to the id, you can supply a tag to identify the desired fragment.
 * ID will always take precendence over tag, if specified.
 *
 * You must specify either an ID or a tag.
 *
 * Usage example:<br />
 * {@code @InjectFragment(R.id.hello) protected MyFragment hello;}<br/>
 * {@code @InjectFragment(tag="hello") protected Fragment hello;}
 *
 * @author Mike Burton
 */
@Retention(RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@BindingAnnotation
public @interface InjectFragment {
    int value() default -1;
    String tag() default "";
}
