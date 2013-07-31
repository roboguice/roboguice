package roboguice.util;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;


// TODO
// - has no execute() method
// - has no progress support
// - has no onInterrupted support
// - add retry support


public class AndroidCallableWrapper<ResultT> implements Runnable {
    protected Handler handler;
    protected AndroidCallableI<ResultT> delegate;
    protected StackTraceElement[] launchLocation;

    public AndroidCallableWrapper(Handler handler, AndroidCallableI<ResultT> delegate, StackTraceElement[] launchLocation ) {
        this.delegate = delegate;
        this.launchLocation = launchLocation;
        this.handler = handler != null ? handler : new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        ResultT result = null;
        Exception exception = null;
        try {
            if (isPreCallOverriden(delegate.getClass()))
                beforeCall();

            result = doDoInBackgroundThread();
        } catch (Exception e) {
            exception = e;
        } finally {
            afterCall(result, exception);
        }
    }


    void beforeCall() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Exception[] exceptions = new Exception[1];

        // Execute onSuccess in the UI thread, but wait
        // for it to complete.
        // If it throws an exception, capture that exception
        // and rethrow it later.
        handler.post(new Runnable() {
            public void run() {
                try {
                    new Callable() {
                        @Override
                        public Object call() throws Exception {
                            doOnPreCall();
                            return null;
                        }
                    }.call();
                } catch (Exception e) {
                    exceptions[0] = e;
                } finally {
                    latch.countDown();
                }
            }
        });

        // Wait for onSuccess to finish
        latch.await();

        if (exceptions[0] != null)
            throw exceptions[0];

    }

    void afterCall(final ResultT result, final Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (e != null) {
                        if( launchLocation!=null ) {
                            final StackTraceElement[] stackTrace = e.getStackTrace();
                            final StackTraceElement[] result = new StackTraceElement[stackTrace.length + launchLocation.length];
                            System.arraycopy(stackTrace, 0, result, 0, stackTrace.length);
                            System.arraycopy(launchLocation, 0, result, stackTrace.length, launchLocation.length);
                            e.setStackTrace(result);
                        }
                        doOnException(e);
                    } else {
                        doOnSuccess(result);
                    }
                } finally {
                    doOnFinally();
                }
            }
        });
    }

    protected void doOnPreCall() throws Exception {
        delegate.onPreCall();
    }

    protected ResultT doDoInBackgroundThread() throws Exception {
        return delegate.doInBackground();
    }

    protected void doOnSuccess(ResultT result) {
        delegate.onSuccess(result);
    }

    protected void doOnException(Exception e) {
        delegate.onException(e);
    }

    protected void doOnFinally() {
        delegate.onFinally();
    }


    static HashMap<Class<? extends AndroidCallableI>, Boolean> isPreCallOverriddenMap = new HashMap<Class<? extends AndroidCallableI>, Boolean>();

    static boolean isPreCallOverriden(Class<? extends AndroidCallableI> subClass) {
        try {
            Boolean tmp = isPreCallOverriddenMap.get(subClass);
            if (tmp != null)
                return tmp;

            tmp = subClass.getMethod("onPreCall").getDeclaringClass() != AndroidCallableWrapper.class;
            isPreCallOverriddenMap.put(subClass, tmp);
            return tmp;

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}