package roboguice.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to annotate fields that
 * should be mocked and injected during tests.
 *
 * @author SNI
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Mock {

    MockType value() default MockType.NORMAL;

    public enum MockType {
        NORMAL,
        STRICT,
        NICE;
    }
}
