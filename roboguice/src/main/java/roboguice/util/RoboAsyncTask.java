package roboguice.util;

import roboguice.inject.ContextScope;

import android.content.Context;
import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.concurrent.ThreadFactory;

/**
 * Allows injection to happen for tasks that execute in a background thread.
 * 
 * @param <ResultT>
 */
public abstract class RoboAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {
    @Inject static protected Provider<Context> contextProvider;
    @Inject static protected Provider<ContextScope> scopeProvider;
    
    protected ContextScope scope = scopeProvider.get();
    protected Context context = contextProvider.get();

    protected RoboAsyncTask() {
    }

    protected RoboAsyncTask(Handler handler) {
        super(handler);
    }

    protected RoboAsyncTask(Handler handler, ThreadFactory threadFactory) {
        super(handler, threadFactory);
    }

    protected RoboAsyncTask(ThreadFactory threadFactory) {
        super(threadFactory);
    }

    @Override
    protected void callSetup() {
        scope.enter(context);
    }

    @Override
    protected void callTearDown() {
        scope.exit(context);
    }
}
