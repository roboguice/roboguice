package roboguice.event.eventListener;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the RunnableAsyncTaskAdaptor class
 *
 * @author John Ericksen
 */
public class RunnableAsyncTaskAdaptorTest {

    @SuppressWarnings("rawtypes")
    protected EventListenerRunnable runnable;

    protected RunnableAsyncTaskAdaptor runnableAdaptor;

    @Before
    public void setup(){
        runnable = createMock(EventListenerRunnable.class);

        runnableAdaptor = new RunnableAsyncTaskAdaptor(runnable);
    }

    @Test
    public void test() throws Exception {
        reset(runnable);

        runnable.run();

        replay(runnable);

        runnableAdaptor.call();

        verify(runnable);
    }

}
