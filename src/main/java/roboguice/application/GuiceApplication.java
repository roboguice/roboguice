package roboguice.application;

import roboguice.inject.ActivityProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScoped;
import roboguice.inject.ExtrasListener;
import roboguice.inject.GuiceApplicationProvider;
import roboguice.inject.ResourceListener;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SharedPreferencesProvider;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class GuiceApplication extends Application implements Module {
    protected Injector guice;

    @Override
    public void onCreate() {
        super.onCreate();
        final Injector i = getInjector();
        final ContextScope scope = i.getInstance(ContextScope.class);
        scope.enter(this);
        i.injectMembers(this);
    }

    public Injector getInjector() {
        return guice!=null ? guice : ( guice = Guice.createInjector( Stage.PRODUCTION, this ) );
    }

    /**
     * Override to configure your own appliation
     */
    public void configure(Binder b) {
        final ContextScope contextScope = new ContextScope();
        final Provider<Context> throwingContextProvider = ContextScope.<Context>seededKeyProvider();
        final Provider<Context> contextProvider = contextScope.scope(Key.get(Context.class), throwingContextProvider);

        b.bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        b.bind(Resources.class).toProvider(ResourcesProvider.class);
        b.bind(GuiceApplication.class).toProvider(Key.get(new TypeLiteral<GuiceApplicationProvider<GuiceApplication>>(){}));

        // Context Scope bindings
        b.bindScope(ContextScoped.class, contextScope );
        b.bind(ContextScope.class).toInstance(contextScope);
        b.bind(Context.class).toProvider(throwingContextProvider).in(ContextScoped.class);
        b.bind(Activity.class).toProvider(ActivityProvider.class);

        // Android Resources require special handling
        b.bindListener( Matchers.any(), new ResourceListener(contextProvider) );
        b.bindListener( Matchers.any(), new ExtrasListener(contextProvider) );

    }
}
