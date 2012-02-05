package roboguice.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
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
 * If using your own executor, you must call future() to get a runnable you can execute.
 * 
 * @param <ResultT>
 */
public abstract class SafeAsyncTask<ResultT> implements Callable<ResultT> {
    public static final int DEFAULT_POOL_SIZE = 25;
    protected static final Executor DEFAULT_EXECUTOR = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

    protected Handler handler;
    protected Executor executor;
    protected StackTraceElement[] launchLocation;
    protected FutureTask<Void> future;


    /**
     * Sets executor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE) and
     * Handler to new Handler()
     */
    public SafeAsyncTask() {
        this.executor = DEFAULT_EXECUTOR;
    }

    /**
     * Sets executor to Executors.newFixedThreadPool(DEFAULT_POOL_SIZE)
     */
    public SafeAsyncTask( Handler handler ) {
        this.handler = handler;
        this.executor = DEFAULT_EXECUTOR;
    }

    /**
     * Sets Handler to new Handler()
     */
    public SafeAsyncTask( Executor executor ) {
        this.executor = executor;
    }

    public SafeAsyncTask( Handler handler, Executor executor ) {
        this.handler = handler;
        this.executor = executor;
    }


    public FutureTask<Void> future() {
        future = new FutureTask<Void>( newTask() );
        return future;
    }

    public SafeAsyncTask<ResultT> executor( Executor executor ) {
        this.executor = executor;
        return this;
    }

    public Executor executor() {
        return executor;
    }

    public SafeAsyncTask<ResultT> handler( Handler handler ) {
        this.handler = handler;
        return this;
    }

    public Handler handler() {
        return handler;
    }

    public void execute() {
        execute(Thread.currentThread().getStackTrace());
    }

    protected void execute( StackTraceElement[] launchLocation ) {
        this.launchLocation = launchLocation;
        executor.execute( future() );
    }

    public boolean cancel( boolean mayInterruptIfRunning ) {
        if( future==null )
            throw new UnsupportedOperationException("You cannot cancel this task before calling future()");

        return future.cancel(mayInterruptIfRunning);
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
     * @param e an InterruptedException or InterruptedIOException
     */
    protected void onInterrupted( Exception e ) {
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
        onThrowable(e);
    }

    protected void onThrowable( Throwable t ) throws RuntimeException {
        Log.e("roboguice", "Throwable caught during background processing", t);
    }
    
    /**
     * @throws RuntimeException, ignored
     */
    protected void onFinally() throws RuntimeException {}


    protected Task<ResultT> newTask() {
        return new Task<ResultT>(this);
    }


    public static class Task<ResultT> implements Callable<Void> {
        protected SafeAsyncTask<ResultT> parent;
        protected Handler handler;

        public Task(SafeAsyncTask<ResultT> parent) {
            this.parent = parent;
            this.handler = parent.handler!=null ? parent.handler : new Handler(Looper.getMainLooper());
        }

        public Void call() throws Exception {
            try {
                doPreExecute();
                doSuccess(doCall());

            } catch( final Exception e ) {
                try {
                    doException(e);
                } catch( Exception f ) {
                    // logged but ignored
                    Ln.e(f);
                }

            } catch( final Throwable t ) {
                try {
                    doThrowable(t);
                } catch( Exception f ) {
                    // logged but ignored
                    Ln.e(f);
                }
            } finally {
                doFinally();
            }

            return null;
        }

        protected void doPreExecute() throws Exception {
            postToUiThreadAndWait( new Callable<Object>() {
                public Object call() throws Exception {
                    parent.onPreExecute();
                    return null;
                }
            });
        }

        protected ResultT doCall() throws Exception {
            return parent.call();
        }

        protected void doSuccess( final ResultT r ) throws Exception {
            postToUiThreadAndWait( new Callable<Object>() {
                public Object call() throws Exception {
                    parent.onSuccess(r);
                    return null;
                }
            });
        }

        protected void doException( final Exception e ) throws Exception {
            if( parent.launchLocation!=null ) {
                final ArrayList<StackTraceElement> stack = new ArrayList<StackTraceElement>(Arrays.asList(e.getStackTrace()));
                stack.addAll(Arrays.asList(parent.launchLocation));
                e.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
            }
            postToUiThreadAndWait( new Callable<Object>() {
                public Object call() throws Exception {
                    if( e instanceof InterruptedException || e instanceof InterruptedIOException )
                        parent.onInterrupted(e);
                    else
                        parent.onException(e);
                    return null;
                }
            });
        }

        protected void doThrowable( final Throwable e ) throws Exception {
            if( parent.launchLocation!=null ) {
                final ArrayList<StackTraceElement> stack = new ArrayList<StackTraceElement>(Arrays.asList(e.getStackTrace()));
                stack.addAll(Arrays.asList(parent.launchLocation));
                e.setStackTrace(stack.toArray(new StackTraceElement[stack.size()]));
            }
            postToUiThreadAndWait( new Callable<Object>() {
                public Object call() throws Exception {
                    parent.onThrowable(e);
                    return null;
                }
            });
        }
        
        protected void doFinally() throws Exception {
            postToUiThreadAndWait( new Callable<Object>() {
                public Object call() throws Exception {
                    parent.onFinally();
                    return null;
                }
            });
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

    }

}
