package roboguice.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Parameter annotation to bind a given method and parameter to an event raised through the
 * EventManager.fire() method.
 *
 * Please note that a runtime exception will be thrown if more than one parameter is annotated or more than one parameter
 * exists in the method definition.
 *
 * Example of proper use:
 *
 * ContextEvent<EventParameter> event = new ContextEvent<EventParameter>(EventParameter.class, new EventParameter("data"));
 *
 * eventManager.fire(context, event);
 *
 * triggers:
 *
 * public void handleEvent(@Observes EventParameter event){
 *     String data = event.getData() // "data"
 * }
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
@Retention(RUNTIME)
@Target( { ElementType.PARAMETER })
public @interface Observes {
    EventThread value() default EventThread.CURRENT;
}
