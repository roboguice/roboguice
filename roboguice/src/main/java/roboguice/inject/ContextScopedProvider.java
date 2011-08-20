package roboguice.inject;

import android.content.Context;

import com.google.inject.Inject;
import com.google.inject.Provider;


public class ContextScopedProvider<T> {
    @Inject protected ContextScope scope;
    @Inject protected Provider<T> provider;

    public T get(Context context) {
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
