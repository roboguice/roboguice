package roboguice.inject;

import roboguice.inject.ViewListener.ViewMembersInjector;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.inject.*;
import com.google.inject.spi.TypeConverterBinding;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextScopedRoboInjector implements RoboInjector {
    protected Injector delegate;
    protected Context context;
    protected ContextScope scope;
    protected ViewListener viewListener;

    public ContextScopedRoboInjector(Context context, Injector applicationInjector, ViewListener viewListener) {
        this.delegate = applicationInjector;
        this.context = context;
        this.viewListener = viewListener;
        this.scope = delegate.getInstance(ContextScope.class);
    }

    @Override
    public Injector createChildInjector(Iterable<? extends Module> modules) {
        synchronized (ContextScope.class) {
            scope.enter(context);
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
            scope.enter(context);
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
            scope.enter(context);
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
            scope.enter(context);
            try {
                return delegate.getAllBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getBinding(Key<T> key) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getBinding(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getBinding(Class<T> type) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getBinding(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Key<?>, Binding<?>> getBindings() {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Binding<T> getExistingBinding(Key<T> key) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getExistingBinding(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> T getInstance(Key<T> key) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getInstance(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getInstance(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getMembersInjector(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        synchronized (ContextScope.class) {
            scope.enter(context);
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
            scope.enter(context);
            try {
                return delegate.getParent();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getProvider(key);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                return delegate.getProvider(type);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        synchronized (ContextScope.class) {
            scope.enter(context);
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
            scope.enter(context);
            try {
                return delegate.getTypeConverterBindings();
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public void injectMembers(Object instance) {
        injectMembersWithoutViews(instance);
    }

    public void injectMembersWithoutViews( Object instance ) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                delegate.injectMembers(instance);
            }finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public void injectViewMembers(Activity activity) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                if( context!=activity )
                    throw new UnsupportedOperationException("internal error, how did you get here?");

                ViewMembersInjector.injectViews(activity);
            } finally {
                scope.exit(context);
            }
        }
    }

    @Override
    public void injectViewMembers(Fragment fragment) {
        synchronized (ContextScope.class) {
            scope.enter(context);
            try {
                ViewMembersInjector.injectViews(fragment);
            } finally {
                scope.exit(context);
            }
        }
    }
}
