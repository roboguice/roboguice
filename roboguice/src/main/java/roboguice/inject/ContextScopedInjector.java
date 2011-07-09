package roboguice.inject;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.inject.*;
import com.google.inject.spi.TypeConverterBinding;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContextScopedInjector implements RoboInjector {
    protected Injector delegate;
    protected Context context;
    protected ContextScope scope;
    protected ViewListener viewListener;

    public ContextScopedInjector(Context context, Injector applicationInjector, ViewListener viewListener) {
        this.delegate = applicationInjector;
        this.context = context;
        this.viewListener = viewListener;
        this.scope = delegate.getInstance(ContextScope.class);
    }

    @Override
    public Injector createChildInjector(Iterable<? extends Module> modules) {
        scope.enter(context);
        return delegate.createChildInjector(modules);
    }

    @Override
    public Injector createChildInjector(Module... modules) {
        scope.enter(context);
        return delegate.createChildInjector(modules);
    }

    @Override
    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        scope.enter(context);
        return delegate.findBindingsByType(type);
    }

    @Override
    public Map<Key<?>, Binding<?>> getAllBindings() {
        scope.enter(context);
        return delegate.getAllBindings();
    }

    @Override
    public <T> Binding<T> getBinding(Key<T> key) {
        scope.enter(context);
        return delegate.getBinding(key);
    }

    @Override
    public <T> Binding<T> getBinding(Class<T> type) {
        scope.enter(context);
        return delegate.getBinding(type);
    }

    @Override
    public Map<Key<?>, Binding<?>> getBindings() {
        scope.enter(context);
        return delegate.getBindings();
    }

    @Override
    public <T> Binding<T> getExistingBinding(Key<T> key) {
        scope.enter(context);
        return delegate.getExistingBinding(key);
    }

    @Override
    public <T> T getInstance(Key<T> key) {
        scope.enter(context);
        return delegate.getInstance(key);
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        scope.enter(context);
        return delegate.getInstance(type);
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
        scope.enter(context);
        return delegate.getMembersInjector(type);
    }

    @Override
    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
        scope.enter(context);
        return delegate.getMembersInjector(typeLiteral);
    }

    @Override
    public Injector getParent() {
        scope.enter(context);
        return delegate.getParent();
    }

    @Override
    public <T> Provider<T> getProvider(Key<T> key) {
        scope.enter(context);
        return delegate.getProvider(key);
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        scope.enter(context);
        return delegate.getProvider(type);
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        scope.enter(context);
        return delegate.getScopeBindings();
    }

    @Override
    public Set<TypeConverterBinding> getTypeConverterBindings() {
        scope.enter(context);
        return delegate.getTypeConverterBindings();
    }

    @Override
    public void injectMembers(Object instance) {
        scope.enter(context);
        delegate.injectMembers(instance);
    }

    @Override
    public void injectViewMembers(Activity activity) {
        viewListener.injectViews(activity);
    }

    @Override
    public void injectViewMembers(View root) {
        throw new UnsupportedOperationException();
    }
}
