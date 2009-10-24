package roboguice.config;

import roboguice.application.GuiceApplication;
import roboguice.inject.ActivityProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScoped;
import roboguice.inject.ExtrasListener;
import roboguice.inject.GuiceApplicationProvider;
import roboguice.inject.ResourceListener;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SharedPreferencesProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class AndroidModule extends AbstractModule {
    protected Application app;

    public AndroidModule( Application app ) {
        this.app = app;
    }

    @Override
    protected void configure() {
        final ContextScope contextScope = new ContextScope();
        final Provider<Context> throwingContextProvider = ContextScope.<Context>seededKeyProvider();
        final Provider<Context> contextProvider = contextScope.scope(Key.get(Context.class), throwingContextProvider);

        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(GuiceApplication.class).toProvider(Key.get(new TypeLiteral<GuiceApplicationProvider<GuiceApplication>>(){}));

        // Context Scope bindings
        bindScope(ContextScoped.class, contextScope );
        bind(ContextScope.class).toInstance(contextScope);
        bind(Context.class).toProvider(throwingContextProvider).in(ContextScoped.class);
        bind(Activity.class).toProvider(ActivityProvider.class);

        // Android Resources require special handling
        bindListener( Matchers.any(), new ResourceListener(contextProvider,app) );
        bindListener( Matchers.any(), new ExtrasListener(contextProvider) );
    }

}
