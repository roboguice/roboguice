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
    @Override
    public void run() {
        final StackTraceElement[] launchLocation = Ln.isDebugEnabled() ? Thread.currentThread().getStackTrace() : null;
        new AndroidCallableWrapper<ResultT>(null,this, launchLocation).run();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPreCall() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinally() {}
}
