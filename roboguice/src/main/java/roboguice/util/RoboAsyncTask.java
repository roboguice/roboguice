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
        return new Task<ResultT>(this, contextProvider.get(), scopeProvider.get());
    }

    protected static class Task<ResultT> extends SafeAsyncTask.Task<ResultT> {
        protected Context context;
        protected ContextScope scope;

        public Task(SafeAsyncTask<ResultT> parent, Context context, ContextScope scope) {
            super(parent);
            this.context = context;
            this.scope = scope;
        }

        @Override
        protected ResultT doCall() throws Exception {
            scope.open(context); // BUG is this even necessary anymore?
            return super.doCall();
        }
    }
}
