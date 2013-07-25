package roboguice.util;


import android.os.Looper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class AndroidCallableTest {

    @Test
    @Ignore("I think this is failing because of a robolectric problem.  Handler(Looper.getMainLooper()) is running message on the bg thread instead of fg thread, not sure how to fix it.  Maybe robolectric2?")
    public void shouldCallMethodsUsingProperThreads() throws Exception {

        final Thread fgThread = Thread.currentThread();
        final Thread[] bgThread = {null};

        final ThreadFactory bgThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                bgThread[0] = new Thread(runnable);
                bgThread[0].setName("bgThread");
                return bgThread[0];
            }
        };


        final Thread[] answers = new Thread[4];

        final AndroidCallable c = new AndroidCallable<String>() {
            @Override
            public void onPreCall() {
                answers[0] = Thread.currentThread();
            }

            @Override
            public String doInBackground() throws Exception {
                answers[1] = Thread.currentThread();
                return "12345";
            }

            @Override
            public void onException(Exception e) {
                // not called
            }

            @Override
            public void onSuccess(String result) {
                answers[2] = Thread.currentThread();
            }

            @Override
            public void onFinally() {
                answers[3] = Thread.currentThread();
                synchronized (AndroidCallableTest.this){
                    AndroidCallableTest.this.notifyAll();
                }
            }


        };

        Executors.newSingleThreadExecutor(bgThreadFactory).submit(c);

        // wait for the above to finish
        synchronized (AndroidCallableTest.this) {
            AndroidCallableTest.this.wait();
        }

        // Run all the pending tasks on the ui thread
        Robolectric.shadowOf(Looper.getMainLooper()).runToEndOfTasks();


        final Thread[] correctAnswer = new Thread[]{fgThread, bgThread[0], fgThread, fgThread };
        assertThat( answers, equalTo(correctAnswer));

    }

    @Test
    @Ignore("Can't implement until I get the other test working")
    public void shouldNotCallOnPreCall() {
        throw new UnsupportedOperationException();
    }

    @Test
    @Ignore("Can't implement until I get the other test working")
    public void shouldHaveCorrectStackTrace() {
        throw new UnsupportedOperationException();
    }

}
