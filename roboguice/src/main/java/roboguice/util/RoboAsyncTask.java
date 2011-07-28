package roboguice.util;

import roboguice.inject.ContextScope;

import android.content.Context;
import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.concurrent.Executor;

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
        return new Task<ResultT>(this);
    }

    protected static class Task<ResultT> extends SafeAsyncTask.Task<ResultT> {
        protected Context context;
        protected ContextScope scope;

        public Task(SafeAsyncTask parent) {
            super(parent);
            this.context = contextProvider.get();
            this.scope = scopeProvider.get();
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
