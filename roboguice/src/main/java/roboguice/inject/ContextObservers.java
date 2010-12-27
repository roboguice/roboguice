package roboguice.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to allow a method to accept multiple @ContextObserver annotations
 *
 * @author John Ericksen
 */
@Retention(RUNTIME)
@Target( { ElementType.METHOD })
public @interface ContextObservers {
    ContextObserver[] value();
}
