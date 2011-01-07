package roboguice.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Parameter annotation to indicate the expected return type of methods
 * that handle the specified event.
 */
@Retention(RUNTIME)
@Target( { ElementType.TYPE })
public @interface Returns {
    Class<?> value();
}
