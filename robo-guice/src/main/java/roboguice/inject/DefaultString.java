package roboguice.inject;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Indicates that an extra injected with @InjectExtra should have a default value. The default value is specified in the
 * value parameter of this annotation, which must be provided.<br />
 * <br />
 * This annotation MUST NOT be used without @InjectExtra.<br />
 * The value type MUST match the parameter type.<br />
 * This annotation SHOULD NOT be combined with @Nullable.<br />
 * This annotation SHOULD NOT be combined with any other @DefaultXXX annotation.<br />
 * <br />
 * Usage example :<br />
 * <br /> {@code @InjectExtra("someValue") @DefaultString(true) protected String someValue;}
 */
@Retention(RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@BindingAnnotation
public @interface DefaultString {
    String value();
}
