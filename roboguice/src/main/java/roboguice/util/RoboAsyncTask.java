package roboguice.util;

import roboguice.inject.ContextScope;

import android.content.Context;
import android.os.Handler;

import com.google.inject.Inject;

import java.util.concurrent.Executor;

/**
 * Allows injection to happen for tasks that execute in a background thread.
 * 
 * @param <ResultT>
 */
public abstract class RoboAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {
    @Inject protected Context context;
    @Inject protected ContextScope scope;

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
        return new Task<ResultT>(this, context, scope);
    }

    protected static class Task<ResultT> extends SafeAsyncTask.Task<ResultT> {
        protected Context context;
        protected ContextScope scope;

        public Task(SafeAsyncTask parent, Context context, ContextScope scope) {
            super(parent);
            this.context = context;
            this.scope = scope;
        }

        @Override
        protected ResultT doCall() throws Exception {
            scope.enter(context);
            return super.doCall();
        }
    }
}
