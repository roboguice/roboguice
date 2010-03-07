package roboguice.util;

import roboguice.inject.ContextScope;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Allows injection to happen for tasks that execute in a background thread.
 * 
 * @param <ArgumentT>
 * @param <ResultT>
 */
public abstract class RoboAsyncTask<ArgumentT, ResultT> extends SafeAsyncTask<ArgumentT, ResultT> {
    @Inject static protected Provider<Context> contextProvider;
    @Inject static protected Provider<ContextScope> scopeProvider;
    
    protected ContextScope scope;
    protected Context context;

    @Override
    public SafeAsyncTask execute(ArgumentT arg) {
        context = contextProvider.get();
        scope = scopeProvider.get();

        return super.execute(arg);
    }

    @Override
    protected void doInBackgroundSetup() {
        scope.enter(context);
    }

    @Override
    protected void doInBackgroundTearDown() {
        scope.exit(context);
    }
}
