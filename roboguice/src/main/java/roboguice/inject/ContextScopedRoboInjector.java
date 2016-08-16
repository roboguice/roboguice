package roboguice.inject;

import android.content.Context;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//github.com/roboguice/roboguice/issues/174

public class ContextScopedRoboInjector implements Injector {
    protected Injector delegate;
    protected Context context;
    protected ContextScope scope;
    private final HashMap<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();

    public ContextScopedRoboInjector(Context context, Injector applicationInjector, ContextScope scope, Iterable<? extends Module> modules) {
        if (modules == null) {
            this.delegate = applicationInjector;
        } else {
            this.delegate = applicationInjector.createChildInjector(modules);
        }
        this.context = context;
        this.scope = scope;
        if (scope == null) {
            throw new RuntimeException("Scope can't be null");
        }
        // Add the context to the scope for key Context, Activity, etc.
        Class<?> c = context.getClass();
        do {
            scopedObjects.put(Key.get(c), context);
            c = c.getSuperclass();
        } while (c != Object.class);

    }

    @Override
    public Injector createChildInjector(Iterable<? extends Module> modules) {
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.createChildInjector(modules);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Injector createChildInjector(Module... modules) {
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.createChildInjector(modules);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.findBindingsByType(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Key<?>, Binding<?>> getAllBindings() {
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getAllBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getBinding(Key<T> key) {
        //System.out.println("in getBinding");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getBinding(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getBinding(Class<T> type) {
        //System.out.println("in getBinding");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getBinding(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Key<?>, Binding<?>> getBindings() {
        //System.out.println("in getBindings");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getExistingBinding(Key<T> key) {
        //System.out.println("in getExistingBindings");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getExistingBinding(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> T getInstance(Key<T> key) {
        //System.out.println("in getInstance");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getInstance(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        //System.out.println("in getInstance");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getInstance(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        //System.out.println("in getMembersInjector");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getMembersInjector(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        //System.out.println("in getMembersInjector");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getMembersInjector(typeLiteral);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Injector getParent() {
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getParent();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        //System.out.println("in getProvider");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getProvider(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        //System.out.println("in getProvider");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getProvider(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        //System.out.println("in getScopeBindings");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getScopeBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Set<TypeConverterBinding> getTypeConverterBindings() {
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                return delegate.getTypeConverterBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public void injectMembers(Object instance) {
        //System.out.println("in injectMembers");
        synchronized (ContextScope.class) {
            scope.enter(context, scopedObjects);
            try {
                delegate.injectMembers(instance);
            } finally {
                scope.exit(context);
            }
        }
    }

    public ContextScope getContextScope() {
        return scope;
    }

    public Map<Key<?>, Object> getScopedObjects() {
        return scopedObjects;
    }
}
