package roboguice.event.eventListener.factory;

import com.google.inject.Inject;
import roboguice.event.EventListener;
import roboguice.event.eventListener.RunnableAsyncTaskAdaptor;

/**
 * Factory for the RunnableAsyncTaskAdaptor.
 *
 * @author John Ericksen
 */
public class RunnableAsyncTaskAdaptorFactory {

    @Inject
    protected EventFireRunnableFactory runnableFactory;

    public <T> RunnableAsyncTaskAdaptor build(T event, EventListener<T> eventListener) {
        return new RunnableAsyncTaskAdaptor(runnableFactory.build(event, eventListener));
    }
}
