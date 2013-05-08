package roboguice.event;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * //TODO
 */
@Retention(RUNTIME)
@Target({ ElementType.TYPE })
public @interface StickyEvent {
}
