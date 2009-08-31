package roboguice.inject;

import java.lang.reflect.Field;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ResourceListener implements TypeListener {
    protected Provider<Activity> activity;

    public ResourceListener( Provider<Activity> activity ) {
        this.activity = activity;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Class<?> c = typeLiteral.getRawType();
        while( c!=null ) {
            for (Field field : c.getDeclaredFields())
                if( field.isAnnotationPresent(InjectResource.class) )
                    typeEncounter.register(new ResourceMembersInjector<I>(field, activity, field.getAnnotation(InjectResource.class)));
            c = c.getSuperclass();
        }
    }
}


class ResourceMembersInjector<T> implements MembersInjector<T> {
    protected Field field;
    protected Provider<Activity> activity;
    protected InjectResource annotation;

    public ResourceMembersInjector( Field field, Provider<Activity> activity, InjectResource annotation ) {
        this.field = field;
        this.activity = activity;
        this.annotation = annotation;
    }

    public void injectMembers(T instance) {
        Object value = null;

        try {

            final int id = annotation.value();
            final Resources resources = activity.get().getResources();
            final Class<?> t = field.getType();
            value = String.class.isAssignableFrom(t) ? resources.getString(id) :
                                 View.class.isAssignableFrom(t) ? activity.get().findViewById(id) :
                                 Drawable.class.isAssignableFrom(t) ? resources.getDrawable(id) :
                                 null ;

            if( value==null && field.getAnnotation(Nullable.class)==null )
                throw new NullPointerException( String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName() ));

            field.setAccessible(true);
            field.set(instance, value );

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (IllegalArgumentException f ) {
            throw new IllegalArgumentException( String.format("Can't assign %s value %s to %s field %s", value!=null ? value.getClass() : "(null)", value, field.getType(), field.getName() ));
        }
    }
}