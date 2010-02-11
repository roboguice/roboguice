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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;
import com.google.inject.spi.TypeEncounter;

import android.app.Application;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;


/**
 * Resource listener.
 * @author Mike Burton
 */
public class ResourceListener implements StaticTypeListener {
    protected Application application;

    public ResourceListener(Application application) {
        this.application = application;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Class<?> c = typeLiteral.getRawType();
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(InjectResource.class)) {
                    typeEncounter.register(new ResourceMembersInjector<I>(field, application, field.getAnnotation(InjectResource.class)));
                }
            }
            c = c.getSuperclass();
        }
    }

    @SuppressWarnings("unchecked")
    public void requestStaticInjection(Class<?>... types) {
        for (Class<?> c : types) {
            while (c != null) {
                for (Field field : c.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(InjectResource.class)) {
                        new ResourceMembersInjector(field, application, field.getAnnotation(InjectResource.class)).injectMembers(null);
                    }
                }
                c = c.getSuperclass();
            }
        }

    }
}

class ResourceMembersInjector<T> implements MembersInjector<T> {

    protected Field field;
    protected Application application;
    protected InjectResource annotation;

    public ResourceMembersInjector(Field field, Application application, InjectResource annotation) {
        this.field = field;
        this.application = application;
        this.annotation = annotation;
    }

    public void injectMembers(T instance) {

        Object value = null;

        try {

            final int id = annotation.value();
            final Class<?> t = field.getType();

            if (String.class.isAssignableFrom(t)) {
                value = application.getResources().getString(id);
            } else if (Drawable.class.isAssignableFrom(t)) {
                value = application.getResources().getDrawable(id);
            } else if (String[].class.isAssignableFrom(t)) {
                    // <array> resource
                value = application.getResources().getStringArray(id);
            } else if (int.class.isAssignableFrom(t) || Integer.class.isAssignableFrom(t)) {
                    // <color> resource
                value = application.getResources().getColor(id);
            } else if (int[].class.isAssignableFrom(t) || Integer[].class.isAssignableFrom(t)) {
                    // <integer-array>
                value = readResIntArray(id);
            }

            if (value == null && field.getAnnotation(Nullable.class) == null) {
                throw new NullPointerException(String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field
                        .getName()));
            }

            field.setAccessible(true);
            field.set(instance, value);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (IllegalArgumentException f) {
            throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s", value != null ? value.getClass() : "(null)", value,
                    field.getType(), field.getName()));
        }
    }


    /**
     * Read Android's <integer-array> resource.
     * Under normal circumstances we could simply use getResources().getIntArray(id) and
     * it works fine if your resource look like:
     *      <integer-array name="myInts">
     *          <item>1</item>
     *          <item>2</item>
     *          <item>3</item>
     *          <item>99</item>
     *      </integer-array>
     * Unfortunately it doesn't work if you want to address id's of other resources, e.g.
     *      <integer-array name="myInts">
     *          <item>@drawable/my_icon</item>
     *          <item>@drawable/main_menu</item>
     *      </integer-array>
     * In this case getIntArray() returns zero for @items.
     * This method provides work-around to fix this issue.
     * @return  Array of ints.
     */
    private int[] readResIntArray(int id) {

        final TypedArray typedArray = this.application.getResources().obtainTypedArray(id);
        final int len = typedArray.length();
        final int[] values = new int[len];
        for (int i = 0; i < len; i++) {
            final int value = typedArray.getResourceId(i, 0);
            values[i] = (value == 0) ? typedArray.getInt(i, 0) : value;
        }
        typedArray.recycle();
        return values;
    }
}
