package roboguice.util;

import roboguice.RoboGuice;

import android.content.Context;
import android.os.Handler;

import java.util.concurrent.Executor;

public abstract class RoboAsyncTask<ResultT> extends SafeAsyncTask<ResultT> {
    protected Context context;

    protected RoboAsyncTask(Context context) {
        RoboGuice.getInjector(context).injectMembers(this);
    }

    protected RoboAsyncTask(Context context, Handler handler) {
        super(handler);
        RoboGuice.getInjector(context).injectMembers(this);
    }

    protected RoboAsyncTask(Context context, Handler handler, Executor executor) {
        super(handler, executor);
        RoboGuice.getInjector(context).injectMembers(this);
    }

    protected RoboAsyncTask(Context context, Executor executor) {
        super(executor);
        RoboGuice.getInjector(context).injectMembers(this);
    }

    public Context getContext() {
        return context;
    }
}
