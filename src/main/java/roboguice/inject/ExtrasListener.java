package roboguice.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class ExtrasListener implements TypeListener {
    protected Provider<Context> contextProvider;

    public ExtrasListener( Provider<Context> contextProvider ) {
        this.contextProvider = contextProvider;
    }

    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Class<?> c = typeLiteral.getRawType();
        while( c!=null ) {
            for (Field field : c.getDeclaredFields())
                if( field.isAnnotationPresent(InjectExtra.class) )
                    typeEncounter.register(new ExtrasMembersInjector<I>(field, contextProvider, field.getAnnotation(InjectExtra.class)));
            c = c.getSuperclass();
        }
    }
}


class ExtrasMembersInjector<T> implements MembersInjector<T> {
    protected Field field;
    protected Provider<Context> contextProvider;
    protected InjectExtra annotation;

    public ExtrasMembersInjector( Field field, Provider<Context> contextProvider, InjectExtra annotation ) {
        this.field = field;
        this.contextProvider = contextProvider;
        this.annotation = annotation;
    }

    public void injectMembers(T instance) {
        final Context context = contextProvider.get();

        if( !(context instanceof Activity))
            return;

        final Activity activity = (Activity)context;
        Object value = null;

        try {

            final String id = annotation.value();
            final Bundle extras = activity.getIntent().getExtras();

            value = extras==null ? null : extras.get(id) ;

            if( value==null ) {
                final Annotation[] annotations = field.getAnnotations();
                for( Annotation a : annotations ) {
                    final Class<?> c = a.annotationType();
                    if( c==DefaultString.class ) {
                        value = ((DefaultString)a).value();
                        break;
                    } else if( c==DefaultBoolean.class ) {
                        value = ((DefaultBoolean)a).value();
                        break;
                    } else if( c==DefaultInteger.class ) {
                        value = ((DefaultInteger)a).value();
                        break;
                    }
                }
            }

            if( value==null && field.getAnnotation(Nullable.class)==null )
                throw new NullPointerException( String.format("Can't inject null value into %s.%s when field is not @Nullable", field.getDeclaringClass(), field.getName() ));

            value = convert(field,value);

            field.setAccessible(true);
            field.set(instance, value );

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (IllegalArgumentException f ) {
            throw new IllegalArgumentException( String.format("Can't assign %s value %s to %s field %s", value!=null ? value.getClass() : "(null)" , value, field.getType(), field.getName() ));
        }
    }

    /**
     * Special handling for certain types.  BUG I'd rather come up with a more generic solution
     */
    protected Object convert(Field field, Object value ) {
        if( value==null ) return null;

        final Class<?> c = field.getType();
        if( c.isAssignableFrom(Date.class) && value.getClass().isAssignableFrom(Long.class))
            return new Date((Long)value);

        return value;
    }
}