package roboguice.inject;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;
import com.google.inject.internal.Nullable;

/**
 * Indicates that a variable member of a class (whether static or not) should be
 * injected with an Android extra. The value is compulsory, and correspond to
 * the key of the extra.<br />
 * <br />
 * The extra must exists when the activity is injected, unless you specify
 * {@code optional=true} in the {@link InjectExtra} annotation. If optional is
 * set to true and no extra is found, no value will be injected in the field.<br />
 * <br />
 * The default behavior of the {@link InjectExtra} annotation is to forbid null
 * values. However, if you wish to allow injection of null values, you should
 * use the {@link Nullable} annotation.<br />
 * <br />
 * You can define a default value in Java when the extra is optional :
 * <br /> {@code @InjectExtra(value="someValue", optional=true) Integer someValue =
 * 2;} ) <br />
 * However, it is a non-sense to inject a default value in Java if the extra is
 * not optional : {@code @InjectExtra("someValue") Integer someValue = 2; // DO
 * NOT DO THIS}
 * 
 * <br />
 * Usage example:<br /> {@code @InjectExtra("someValue") protected Integer someValue;}<br />
 * <br />{@code @InjectExtra("someValue") @Nullable protected Integer someValue;}
 */
@Retention(RUNTIME)
@Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@BindingAnnotation
public @interface InjectExtra {
    String value();

    boolean optional() default false;
}
