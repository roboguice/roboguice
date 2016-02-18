package roboguice.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class Nullable {
    private Nullable() {
    }
    
    public static boolean notNullable( Field field ) {
        return !isNullable( field );
    }

    public static boolean isNullable(Field field) {
        for( Annotation a : field.getAnnotations() )
            if( "Nullable".equals(a.annotationType().getSimpleName()))
                return true;
        
        return false;
    }
}
