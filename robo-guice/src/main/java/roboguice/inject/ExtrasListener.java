package roboguice.inject;

import java.lang.reflect.Field;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class ExtrasListener implements TypeListener {
    protected Provider<Context> contextProvider;

    public ExtrasListener(Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Class<?> c = typeLiteral.getRawType();
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectExtra.class)) {
                    typeEncounter.register(new ExtrasMembersInjector<I>(field, contextProvider, field
                            .getAnnotation(InjectExtra.class)));
                }
            }
            c = c.getSuperclass();
        }
    }

}

class ExtrasMembersInjector<T> implements MembersInjector<T> {
    protected Field             field;
    protected Provider<Context> contextProvider;
    protected InjectExtra       annotation;

    public ExtrasMembersInjector(Field field, Provider<Context> contextProvider, InjectExtra annotation) {
        this.field = field;
        this.contextProvider = contextProvider;
        this.annotation = annotation;
    }

    public void injectMembers(T instance) {
        final Context context = contextProvider.get();

        if (!(context instanceof Activity)) {
            return;
        }

        final Activity activity = (Activity) context;
        Object value = null;

        final String id = annotation.value();
        final Bundle extras = activity.getIntent().getExtras();

        if (extras == null || !extras.containsKey(id)) {
            // If no extra found and the extra injection is optional, no injection happens.
            if (annotation.optional()) {
                return;
            } else {
                throw new IllegalStateException(String.format(
                        "Can't find the mandatory extra identified by key [%s] on field %s.%s", id, field
                        .getDeclaringClass(), field.getName()));
            }
        }

        value = extras.get(id);

        if (value == null && field.getAnnotation(Nullable.class) == null) {
            throw new NullPointerException(String.format(
                    "Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field
                    .getName()));
        }

        value = convert(field, value);

        field.setAccessible(true);
        try {

            field.set(instance, value);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (IllegalArgumentException f) {
            throw new IllegalArgumentException(String.format("Can't assign %s value %s to %s field %s",
                    value != null ? value.getClass() : "(null)", value, field.getType(), field.getName()));
        }
    }

    /**
     * Special handling for certain types. BUG I'd rather come up with a more generic solution
     */
    protected Object convert(Field field, Object value) {
        if (value == null) {
            return null;
        }

        final Class<?> c = field.getType();
        if (c.isAssignableFrom(Date.class) && value.getClass().isAssignableFrom(Long.class)) {
            return new Date((Long) value);
        }

        return value;
    }
}