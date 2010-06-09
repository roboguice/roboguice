package roboguice.util;

import roboguice.inject.ContextScope;

import android.content.Context;
import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.concurrent.Executor;

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

    protected RoboAsyncTask(Handler handler, Executor executor) {
        super(handler, executor);
    }

    protected RoboAsyncTask(Executor executor) {
        super(executor);
    }

    @Override
    protected Task<ResultT> newTask() {
        return new RoboTask<ResultT>(this);
    }

    protected class RoboTask<ResultT> extends SafeAsyncTask.Task<ResultT> {
        public RoboTask(SafeAsyncTask parent) {
            super(parent);
        }

        @Override
        protected ResultT doCall() throws Exception {
            try {
                scope.enter(context);
                return super.doCall();
            } finally {
                scope.exit(context);
            }
        }
    }
}
