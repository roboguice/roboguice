package roboguice.util;

import roboguice.inject.ContextScope;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * An extension to {@link Thread} which propogates the current
 * ContextScoped to the background thread.
 *
 * Current limitations:  any parameters set in the RoboThread are
 * ignored other than Runnable.  This means that priorities, groups,
 * names, etc. won't be honored. Yet.
 */
public class RoboThread extends Thread {
    @Inject static protected Provider<Context> contextProvider;
    @Inject static protected Provider<ContextScope> scopeProvider;

    protected Context context = contextProvider.get();
    protected ContextScope scope = scopeProvider.get();

    public RoboThread() {
    }

    public RoboThread(Runnable runnable) {
        super(runnable);
    }

    @Override
    public void start() {

        // BUG any parameters set in the RoboThread are ignored other than Runnable.
        // This means that priorities, groups, names, etc. won't be honored. Yet.
        new Thread() {
            public void run() {
                scope.enter(context);

                RoboThread.this.run();
            }
        }.start();

    }
}
