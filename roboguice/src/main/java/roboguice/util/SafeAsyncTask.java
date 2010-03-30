package roboguice.util;

import android.os.Handler;
import android.util.Log;

import java.util.concurrent.*;

/**
 * A class similar but unrelated to android's {@link android.os.AsyncTask}.
 *
 * Unlike AsyncTask, this class properly propagates exceptions.
 *
 * If you're familiar with AsyncTask and are looking for {@link android.os.AsyncTask#doInBackground(Object[])},
 * we've named it {@link #call()} here to conform with java 1.5's {@link java.util.concurrent.Callable} interface.
 *
 * Current limitations: does not yet handle progress, although it shouldn't be
 * hard to add.
 * 
 * @param <ResultT>
 */
public abstract class SafeAsyncTask<ResultT> implements Callable<ResultT> {

    protected Handler handler;
    protected ThreadFactory threadFactory;
    protected FutureTask<Void> future = new FutureTask<Void>( new Callable<Void>() {
            public Void call() throws Exception {
                try {
                    postToUiThreadAndWait( new Callable<Object>() {
                        public Object call() throws Exception {
                            onPreExecute();
                            return null;
                        }
                    });

                    callSetup();
                    final ResultT rtrn = SafeAsyncTask.this.call();
                    callTearDown();

                    postToUiThreadAndWait( new Callable<Object>() {
                        public Object call() throws Exception {
                            onSuccess(rtrn);
                            return null;
                        }
                    });

                    return null;

                } catch( final Exception e ) {
                    try {
                        postToUiThreadAndWait( new Callable<Object>() {
                            public Object call() throws Exception {
                                if( e instanceof InterruptedException )
                                    onInterrupted((InterruptedException)e);
                                else
                                    onException(e);
                                return null;
                            }
                        });
                    } catch( Exception f ) {
                        // ignore this exception, throw the original instead
                    }

                    throw e;

                } finally {
                    postToUiThreadAndWait( new Callable<Object>() {
                        public Object call() throws Exception {
                            onFinally();
                            return null;
                        }
                    });
                }


            }
        });



    public SafeAsyncTask() {
        this.handler = new Handler();
        this.threadFactory = Executors.defaultThreadFactory();
    }

    public SafeAsyncTask( Handler handler ) {
        this.handler = handler;
        this.threadFactory = Executors.defaultThreadFactory();
    }

    public SafeAsyncTask( ThreadFactory threadFactory ) {
        this.handler = new Handler();
        this.threadFactory = threadFactory;
    }

    public SafeAsyncTask( Handler handler, ThreadFactory threadFactory ) {
        this.handler = handler;
        this.threadFactory = threadFactory;
    }


    public void execute() {
        threadFactory.newThread( future ).start();
    }

    public boolean cancel( boolean mayInterruptIfRunning ) {
        return future != null && future.cancel(mayInterruptIfRunning);
    }

    /**
     * Posts the specified runnable to the UI thread using a handler,
     * and waits for operation to finish.  If there's an exception,
     * it captures it and rethrows it.
     * @param c the callable to post
     * @throws Exception on error
     */
    protected void postToUiThreadAndWait( final Callable c ) throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Exception[] exceptions = new Exception[1];

        // Execute onSuccess in the UI thread, but wait
        // for it to complete.
        // If it throws an exception, capture that exception
        // and rethrow it later.
        handler.post( new Runnable() {
           public void run() {
               try {
                   c.call();
               } catch( Exception e ) {
                   exceptions[0] = e;
               } finally {
                   latch.countDown();
               }
           }
        });

        // Wait for onSuccess to finish
        latch.await();

        if( exceptions[0] != null )
            throw exceptions[0];

    }


    /**
     * @throws Exception, captured on passed to onException() if present.
     */
    protected void onPreExecute() throws Exception {}

    /**
     * @param t the result of {@link #call()}
     * @throws Exception, captured on passed to onException() if present.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onSuccess( ResultT t ) throws Exception {}

    /**
     * Called when the thread has been interrupted, likely because
     * the task was canceled.
     *
     * By default, calls {@link #onException(Exception)}, but this method
     * may be overridden to handle interruptions differently than other
     * exceptions.
     *
     * @param e the exception thrown from {@link #onPreExecute()}, {@link #call()}, or {@link #onSuccess(Object)}
     */
    protected void onInterrupted( InterruptedException e ) {
        onException(e);
    }

    /**
     * Logs the exception as an Error by default, but this method may
     * be overridden by subclasses.
     *
     * @param e the exception thrown from {@link #onPreExecute()}, {@link #call()}, or {@link #onSuccess(Object)}
     * @throws RuntimeException, ignored
     */
    protected void onException( Exception e ) throws RuntimeException {
        Log.e("roboguice", "Exception caught during background processing", e);
    }

    /**
     * @throws RuntimeException, ignored
     */
    protected void onFinally() throws RuntimeException {}

    /**
     * For if subclasses wish to do additional setup on background thread before {@link #call()} is called
     */
    protected void callSetup() {}

    /**
     * For if subclasses wish to do additional teardown on background thread after {@link #call()} is called
     */
    protected void callTearDown() {}

}
