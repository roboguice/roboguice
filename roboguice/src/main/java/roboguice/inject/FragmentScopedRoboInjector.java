package roboguice.inject;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roboguice.inject.ViewListener.ViewMembersInjector;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverterBinding;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

public class FragmentScopedRoboInjector implements RoboInjector {
    protected Injector delegate;
    protected Context context;
    protected FragmentScope scope;

    public FragmentScopedRoboInjector(Object fragment, Injector activityInjector) {
        this.delegate = activityInjector;
        this.context = getContext(fragment);
        this.scope = delegate.getInstance(FragmentScope.class);
    }
    
    private Context getContext(Object fragment) {
        Context context;
        if( fragment instanceof Fragment ) {
            context = ((Fragment) fragment).getActivity();
        } else if( fragment instanceof android.support.v4.app.Fragment ) {
            context = ((android.support.v4.app.Fragment) fragment).getActivity();
        } else {
            throw new IllegalArgumentException(String.format("%s does not appear to belong to a RoboGuice context (instanceof RoboContext)",fragment));
        }
        return context;
    }

    @Override
    public Injector createChildInjector(Iterable<? extends Module> modules) {
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        synchronized (FragmentScope.class) {
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
        throw new UnsupportedOperationException("It is not possible to use a fragment injector to inject views into an activity. Use the activity injector.");
    }

    @Override
    public void injectViewMembers(android.support.v4.app.Fragment fragment) {
        injectViews(fragment);
    }

    @Override
    public void injectViewMembers(Fragment fragment) {
        injectViews(fragment);
    }

    private void injectViews(Object fragment) {
        synchronized (FragmentScope.class) {
            scope.enter(context);
            try {
                ViewMembersInjector.injectViews(fragment);
            } finally {
                scope.exit(context);
            }
        }
    }
}
