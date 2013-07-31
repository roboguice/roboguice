package roboguice.util;


/**
 * Represent an action that may be run in the background.  Following execution of the task, results are processed in
 * the foreground thread using {@link #onSuccess(Object)} and {@link #onException(Exception)}.
 * <p>
 * Similar to java's {@link java.util.concurrent.Callable}, except the result of the
 * background operation is passed to the UI thread for processing via {@link #onSuccess(Object)} and {@link #onException(Exception)}.
 * <p>
 * Similar to Android's {@link android.os.AsyncTask}, except it adds handling for
 * exceptions and finally and call also be used with java's @{link java.util.concurrent.Executor} and {@link java.util.concurrent.ExecutorService}
 * <p>
 * To use, subclass from {@link AndroidCallable} and execute using an executor such as one from {@link java.util.concurrent.Executors}.
 * @see AndroidCallable
 * @param <ResultT>
 */
public interface AndroidCallableI<ResultT> {

    /**
     * Executed in the handler's thread (usually the UI thread) before the task's doInBackground() method is called.
     * Exceptions will be handled by the onException callback.
     */
    void onPreCall() throws Exception;

    /**
     * Executed in the executor's background thread.
     * Exceptions will be handled by the onException callback.
     */
    ResultT doInBackground() throws Exception;


    /**
     * Executed in the handler's thread (usually the UI thread) if doInBackground threw an exception.
     * @param e the exception thrown by the {@link #doInBackground} method
     */
    void onException(Exception e);


    /**
     * Executed in the handler's thread (usually the UI thread) if call returned a result.
     * Exceptions will be thrown from the UI thread, which will crash the app if not caught.
     * @param result the result returned by the {@link #doInBackground} method
     */
    void onSuccess(ResultT result);

    /**
     * Executed in the handler's thread (usually the UI thread) after {@link #onSuccess(Object)} or {@link #onException(Exception)}
     * are executed.
     * Always executed iff {@link #doInBackground()} is executed.  Not executed if the task is canceled before it is executed.
     */
    void onFinally();
}


