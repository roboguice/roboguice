package roboguice.util;

/**
 * An abstract instance of {@link AndroidCallableI} with empty default implementations for {@link #onPreCall()}
 * and {@link #onFinally()}.
 * <p></p>
 * Also implements {@link Runnable} so it can be used with any thread or {@link java.util.concurrent.Executor}.
 * @see java.util.concurrent.Executors for some useful default executors.
 * @param <ResultT>
 */
public abstract class AndroidCallable<ResultT> implements AndroidCallableI<ResultT>, Runnable {
    protected StackTraceElement[] creationLocation = Ln.isDebugEnabled() ? Thread.currentThread().getStackTrace() : null;

    /**
     * Do not call this directly, pass this AndroidCallable to an Executor and this
     * your doInBackground method will be executed in the background thread.
     */
    @Override
    public void run() {
        new AndroidCallableWrapper<ResultT>(null,this, creationLocation).run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreCall() throws Exception {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinally() {}
}
