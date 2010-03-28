package roboguice.util;

import android.os.Handler;
import android.util.Log;

import java.util.concurrent.*;

/**
 * A class similar but unrelated to android's AsyncTask.
 *
 * Unlike AsyncTask, this class properly propagates exceptions.
 *
 * Current limitations: does not yet handle progress, although it shouldn't be
 * hard to add.  Also, it doesn't support the ellipsis operator in execute(), but this can be
 * simulated with a collection
 * 
 * @param <ArgumentT>
 * @param <ExceptionT>
 * @param <ResultT>
 */
public abstract class SafeAsyncTask<ArgumentT,ResultT> {

    protected Handler handler;
    protected ThreadFactory threadFactory;
    protected FutureTask<ResultT> future;


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


    public SafeAsyncTask<ArgumentT,ResultT> execute( final ArgumentT arg ) {
        future = new FutureTask<ResultT>( new Callable<ResultT>() {
            public ResultT call() throws Exception {
                try {
                    postToUiThreadAndWait( new Callable<Object>() {
                        public Object call() throws Exception {
                            onPreExecute();
                            return null;
                        }
                    });

                    doInBackgroundSetup();
                    final ResultT rtrn = doInBackground(arg);
                    doInBackgroundTearDown();

                    postToUiThreadAndWait( new Callable<Object>() {
                        public Object call() throws Exception {
                            onSuccess(rtrn);
                            return null;
                        }
                    });

                    return rtrn;

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

        threadFactory.newThread( future ).start();

        return this;
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

    /*
    // This is illegal, because it creates a deadlock situation in most cases.
    // It blocks the main thread (usually the UI thread) until all the onXXX methods
    // are invoked, but the onXXX methods need to execute in the UI thread (which is
    // blocked)
    public ResultT get() throws Exception {
        return internalTask.get();
    }
    */


    protected abstract ResultT doInBackground( ArgumentT arg ) throws Exception;

    /**
     * @throws Exception, captured on passed to onException() if present.
     */
    protected void onPreExecute() throws Exception {}

    /**
     * @param t the result of {@link #doInBackground(Object)}
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
     * @param e the exception thrown from {@link #onPreExecute()}, {@link #doInBackground(Object)}, or {@link #onSuccess(Object)}
     */
    protected void onInterrupted( InterruptedException e ) {
        onException(e);
    }

    /**
     * Logs the exception as an Error by default, but this method may
     * be overridden by subclasses.
     *
     * @param e the exception thrown from {@link #onPreExecute()}, {@link #doInBackground(Object)}, or {@link #onSuccess(Object)}
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
     * For if subclasses wish to do additional setup on background thread before {@link #doInBackground(Object)} is called
     */
    protected void doInBackgroundSetup() {}

    /**
     * For if subclasses wish to do additional teardown on background thread after {@link #doInBackground(Object)} is called
     */
    protected void doInBackgroundTearDown() {}

}
