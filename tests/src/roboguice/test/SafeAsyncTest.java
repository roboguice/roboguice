package roboguice.test;

import roboguice.test.config.RoboGuiceTestApplication;
import roboguice.util.RoboAsyncTask;
import roboguice.util.RoboLooperThread;

import android.test.suitebuilder.annotation.MediumTest;

import java.util.concurrent.CountDownLatch;

public class SafeAsyncTest extends RoboUnitTestCase<RoboGuiceTestApplication> {
    public enum State {
        UNKNOWN, TEST_FAIL, TEST_SUCCESS
    }

    @MediumTest
    public void testExceptionInOnPreExecute() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final State state[] = new State[]{State.UNKNOWN};

        new RoboLooperThread() {
            public void run() {

                new RoboAsyncTask<Void,Void>(){
                    @Override
                    protected void onPreExecute() throws Exception {
                        throw new NullPointerException();
                    }

                    @Override
                    protected Void doInBackground(Void ignored) throws Exception {
                        state[0] = SafeAsyncTest.State.TEST_FAIL;
                        return null;
                    }

                    @Override
                    protected void onSuccess(Void ignored) throws Exception {
                        state[0] = SafeAsyncTest.State.TEST_FAIL;
                    }

                    @Override
                    protected void onException(Exception e) {
                        state[0] = SafeAsyncTest.State.TEST_SUCCESS;
                    }

                    @Override
                    protected void onFinally() {
                        latch.countDown();
                    }

                }.execute(null);


            }
        }.start();


        latch.await();
        assertEquals(State.TEST_SUCCESS,state[0]);
    }


    @MediumTest
    public void testExceptionInDoInBackground() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final State state[] = new State[]{State.UNKNOWN};

        new RoboLooperThread() {
            public void run() {

                new RoboAsyncTask<Void,Void>(){
                    @Override
                    protected Void doInBackground(Void ignored) throws Exception {
                        throw new NullPointerException();
                    }

                    @Override
                    protected void onSuccess(Void ignored) throws Exception {
                        state[0] = SafeAsyncTest.State.TEST_FAIL;
                    }

                    @Override
                    protected void onException(Exception e) {
                        state[0] = SafeAsyncTest.State.TEST_SUCCESS;
                    }

                    @Override
                    protected void onFinally() {
                        latch.countDown();
                    }

                }.execute(null);

            }
        }.start();


        latch.await();
        assertEquals(State.TEST_SUCCESS,state[0]);
    }

    @MediumTest
    public void testExceptionInOnSuccess() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final State state[] = new State[]{State.UNKNOWN};

        new RoboLooperThread() {
            public void run() {

                new RoboAsyncTask<Void,Void>(){
                    @Override
                    protected Void doInBackground(Void ignored) throws Exception {
                        return null;
                    }

                    @Override
                    protected void onSuccess(Void ignored) throws Exception {
                        throw new NullPointerException();
                    }

                    @Override
                    protected void onException(Exception e) {
                        state[0] = SafeAsyncTest.State.TEST_SUCCESS;
                    }

                    @Override
                    protected void onFinally() {
                        latch.countDown();
                    }

                }.execute(null);

            }
        }.start();


        latch.await();
        assertEquals(State.TEST_SUCCESS,state[0]);
    }


    @MediumTest
    public void testExceptionInOnException() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final State state[] = new State[]{State.UNKNOWN};
        final Exception thrown[] = new Exception[]{null};

        new RoboLooperThread() {
            public void run() {

                new RoboAsyncTask<Void,Void>(){
                    @Override
                    protected Void doInBackground(Void ignored) throws Exception {
                        return null;
                    }

                    @Override
                    protected void onSuccess(Void ignored) throws Exception {
                        throw new NullPointerException();
                    }

                    @Override
                    protected void onException(Exception e) {
                        thrown[0] = e;
                        throw new NullPointerException();
                    }

                    @Override
                    protected void onFinally() {
                        state[0] = thrown[0]!=null ? SafeAsyncTest.State.TEST_SUCCESS : SafeAsyncTest.State.TEST_FAIL;
                        latch.countDown();
                    }

                }.execute(null);

            }
        }.start();


        latch.await();
        assertEquals(State.TEST_SUCCESS,state[0]);
    }


    /*
    @MediumTest
    public void testCancel() throws InterruptedException {
        throw new UnsupportedOperationException("not implemented yet");
    }
    */

}
