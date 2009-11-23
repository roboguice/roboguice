package roboguice.inject;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Indicates that a variable member of a class (whether static or not) should be injected with an Android resource. The
 * value is compulsory, and correspond to the id of the resource.<br />
 * Usage example:<br /> {@code @InjectResource(R.string.hello) protected String hello;}
 */
@Retention(RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@BindingAnnotation
public @interface InjectResource {
    int value();
}
