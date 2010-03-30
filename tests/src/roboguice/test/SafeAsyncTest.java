package roboguice.test;

import org.apache.http.client.HttpClient;
import roboguice.test.config.RoboGuiceTestApplication;
import roboguice.util.RoboAsyncTask;
import roboguice.util.RoboLooperThread;

import android.content.Context;
import android.os.Handler;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;


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

                new RoboAsyncTask<Void>(){
                    @Override
                    protected void onPreExecute() throws Exception {
                        throw new NullPointerException();
                    }

                    @Override
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
                    @Override
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
                    @Override
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
                    @Override
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
                    @Override
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
                    protected void onInterrupted(InterruptedException e) {
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
    public void testFakeHttp() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final State state[] = new State[]{State.UNKNOWN};

        new RoboLooperThread() {
            public void run() {

                new FakeHttpGet(new View(getInjector().getInstance(Context.class)), URI.create("http://google.com")){
                    @Override
                    protected void onSuccess(String s) throws Exception {
                        state[0] = s.contains("booga") ? SafeAsyncTest.State.TEST_SUCCESS : SafeAsyncTest.State.TEST_FAIL;
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



}







/**
 * Copied from another project, using here for test purposes
 */
class FakeHttpGet extends RoboAsyncTask<String> {
    // We use static injection here so that it's easy for users of HttpGet
    // to instantiate instances without having to do injection themselves.
    @Inject static protected Provider<HttpClient> client;

    protected View spinner;
    protected URI uri;

    public FakeHttpGet( URI uri ) {
        this.uri = uri;
    }

    public FakeHttpGet( View spinner, URI uri ) {
        this.spinner = spinner;
        this.uri = uri;
    }

    public FakeHttpGet(Handler handler, View spinner, URI uri) {
        super(handler);
        this.spinner = spinner;
        this.uri = uri;
    }

    public FakeHttpGet(ThreadFactory threadFactory, Handler handler, View spinner, URI uri) {
        super(handler, threadFactory);
        this.spinner = spinner;
        this.uri = uri;
    }


    public String call() throws Exception {
        return "<html>booga</html>";
    }


    @Override
    protected void onPreExecute() throws Exception {
        if( spinner != null )
            spinner.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onFinally() {
        if( spinner != null )
            spinner.setVisibility(View.GONE);
    }

}


