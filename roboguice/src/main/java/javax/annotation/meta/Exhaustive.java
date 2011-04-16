package javax.annotation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation can be applied to the value() element of an annotation that
 * is annotated as a TypeQualifier. This is only appropriate if the value field
 * returns a value that is an Enumeration.
 * 
 * Applications of the type qualifier with different values are exclusive, and
 * the enumeration is an exhaustive list of the possible values.
 * 
 * For example, the following defines a type qualifier such that if you know a
 * value is neither {@literal @Foo(Color.Red)} or {@literal @Foo(Color.Blue)},
 * then the value must be {@literal @Foo(Color.Green)}. And if you know it is
 * {@literal @Foo(Color.Green)}, you know it cannot be
 * {@literal @Foo(Color.Red)} or {@literal @Foo(Color.Blue)}
 * 
 * <code>
 * @TypeQualifier  @interface Foo {
 *     enum Color {RED, BLUE, GREEN};
 *     @Exhaustive Color value();
 *     }
 *  </code>
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Exhaustive {

}
