package roboguice.util;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import android.os.Handler;
import android.util.Log;

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
@Deprecated
public abstract class SafeAsyncTask<ResultT> implements Callable<ResultT> {
    public static final int DEFAULT_POOL_SIZE = 25;
    protected static final Executor DEFAULT_EXECUTOR = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);

    protected Handler handler;
    protected Executor executor;
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
        future = new FutureTask<Void>( newTask(), null );
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
        final StackTraceElement[] launchLocation = Ln.isDebugEnabled() ? Thread.currentThread().getStackTrace() : null;
        execute(launchLocation);
    }

    protected void execute( StackTraceElement[] launchLocation ) {
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


    protected Runnable newTask() {
        return new SafeAsyncTaskAndroidCallable();
    }


    public class SafeAsyncTaskAndroidCallable extends AndroidCallable<ResultT> {
        @Override
        public ResultT doInBackground() throws Exception {
            return call();
        }

        @Override
        public void onException(Exception e) {
            SafeAsyncTask.this.onException(e);
        }

        @Override
        public void onFinally() {
            SafeAsyncTask.this.onFinally();
        }

        @Override
        public void onPreCall() {
            try {
                SafeAsyncTask.this.onPreExecute();
            } catch (Exception e) {
                throw new RuntimeException(e); // This will halt the UI thread
            }
        }

        @Override
        public void onSuccess(ResultT result) {
            try {
                SafeAsyncTask.this.onSuccess(result);
            } catch (Exception e) {
                throw new RuntimeException(e); //This will halt the UI thread
            }
        }
    }
}
