package roboguice.util;

import roboguice.RoboGuice;

import android.content.Context;
import android.os.Handler;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.concurrent.Executor;

public abstract class RoboAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {
    @Inject static protected Provider<Context> contextProvider;

    protected RoboAsyncTask() {
        RoboGuice.getInjector(contextProvider.get()).injectMembers(this);
    }

    protected RoboAsyncTask(Handler handler) {
        super(handler);
        RoboGuice.getInjector(contextProvider.get()).injectMembers(this);
    }

    protected RoboAsyncTask(Handler handler, Executor executor) {
        super(handler, executor);
        RoboGuice.getInjector(contextProvider.get()).injectMembers(this);
    }

    protected RoboAsyncTask(Executor executor) {
        super(executor);
        RoboGuice.getInjector(contextProvider.get()).injectMembers(this);
    }
}
