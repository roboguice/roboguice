package roboguice.test;

import roboguice.RoboGuice;
import roboguice.util.RoboAsyncTask;
import roboguice.util.RoboLooperThread;

import android.app.Application;
import android.content.Context;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import java.util.concurrent.CountDownLatch;


public class SafeAsyncTest extends RoboUnitTestCase {
    public enum State {
        UNKNOWN, TEST_FAIL, TEST_SUCCESS
    }

    @MediumTest
    public void testExceptionInOnPreExecute() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final State state[] = new State[]{State.UNKNOWN};

        new RoboLooperThread() {
            public void run() {

                new RoboAsyncTask<Void>(){
                    @Override
                    protected void onPreExecute() throws Exception {
                        throw new NullPointerException();
                    }

                    public Void call() throws Exception {
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

                }.execute();


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

                new RoboAsyncTask<Void>(){
                    public Void call() throws Exception {
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

                }.execute();

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

                new RoboAsyncTask<Void>(){
                    public Void call() throws Exception {
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

                }.execute();

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

                new RoboAsyncTask<Void>(){
                    public Void call() throws Exception {
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

                }.execute();

            }
        }.start();


        latch.await();
        assertEquals(State.TEST_SUCCESS,state[0]);
    }


    @MediumTest
    public void testCancel() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final String state[] = new String[]{"Task was never canceled"};

        new RoboLooperThread() {
            public void run() {
                final RoboAsyncTask<Void> task = new RoboAsyncTask<Void>(){
                    public Void call() throws Exception {
                        Thread.sleep(20000);
                        state[0] = "Shouldn't finish executing doInBackground";
                        return null;
                    }

                    @Override
                    protected void onSuccess(Void ignored) throws Exception {
                        state[0] = "onSuccess shouldn't be called";
                    }

                    @Override
                    protected void onInterrupted(Exception e) {
                        state[0] = null; // expected
                    }

                    @Override
                    protected void onException(Exception e){
                        state[0] = "onException shouldn't be called";
                    }


                    @Override
                    protected void onFinally() {
                        latch.countDown();
                    }
                };
                task.execute();
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException e ) {
                    Thread.interrupted();
                }
                task.cancel(true);
            }
        }.start();


        latch.await();
        assertNull(state[0], state[0]);
    }



    @MediumTest
    public void testRoboAsyncTask() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final State state[] = new State[]{State.UNKNOWN};
        final Injector injector = RoboGuice.getInjector((Application)getInstrumentation().getTargetContext().getApplicationContext());

        new RoboLooperThread() {
            public void run() {

                final FakeHttpGet g = new FakeHttpGet(new View(injector.getInstance(Context.class)), "http://google.com" ){
                    @Override
                    protected void onSuccess(String s) throws Exception {
                        state[0] = s.contains("booga") ? SafeAsyncTest.State.TEST_SUCCESS : SafeAsyncTest.State.TEST_FAIL;
                    }

                    @Override
                    protected void onFinally() {
                        latch.countDown();
                    }

                };
                injector.injectMembers(g);
                g.execute();

            }
        }.start();


        latch.await();
        assertEquals(State.TEST_SUCCESS,state[0]);
    }



}







class FakeHttpGet extends RoboAsyncTask<String> {
    @Inject protected Provider<Context> context;

    protected View spinner;
    protected String uri;

    public FakeHttpGet( View spinner, String uri ) {
        this.spinner = spinner;
        this.uri = uri;
    }

    public String call() throws Exception {
        context.get();
        return "<html>booga</html>";
    }


    @Override
    protected void onPreExecute() throws Exception {
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onFinally() {
        spinner.setVisibility(View.GONE);
    }

}


