package roboguice.inject;

import roboguice.util.Strings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Nullable {
    private Nullable() {
    }
    
    public static boolean notNullable( Field field ) {
        return !isNullable( field );
    }

    public static boolean isNullable(Field field) {
        for( Annotation a : field.getAnnotations() )
            if( Strings.equals("Nullable",a.annotationType().getSimpleName()))
                return true;
        
        return false;
    }
}
