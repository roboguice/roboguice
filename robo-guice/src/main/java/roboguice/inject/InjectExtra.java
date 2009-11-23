package roboguice.inject;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Indicates that a variable member of a class (whether static or not) should be injected with an Android extra. The
 * value is compulsory, and correspond to the key of the extra.<br />
 * <br />
 * You can use this annotation combined with @Nullable, or @DefaultXXX to set the default value when trying to get the
 * extra (XXX being a wrapper type, @DefaultBoolean for isntance)<br />
 * <br />
 * Usage example:<br /> {@code @InjectExtra("someValue") protected Integer someValue;}<br />
 * <br />{@code @InjectExtra("someValue") @DefaultInteger(2) protected Integer someValue;}
 */
@Retention(RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@BindingAnnotation
public @interface InjectExtra {
    String value();
}
