package roboguice.inject;

import roboguice.RoboGuice;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.content.Context;


public class ContextScopedProvider<T> {
    @Inject protected Provider<T> provider;

    public T get(Context context) {
        //see https://github.com/roboguice/roboguice/issues/112
        final ContextScope scope = RoboGuice.getInjector(context).getInstance(ContextScope.class);
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return provider.get();
            } finally {
                scope.exit(context);
            }
        }
    }
}
