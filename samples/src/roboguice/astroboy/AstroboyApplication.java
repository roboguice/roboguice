package roboguice.astroboy;

import roboguice.application.GuiceApplication;
import roboguice.inject.ActivityProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScoped;
import roboguice.inject.ExtrasListener;
import roboguice.inject.GuiceApplicationProvider;
import roboguice.inject.ResourceListener;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SharedPreferencesProvider;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;


public class AstroboyApplication extends GuiceApplication {

    @Override
    public void configure( Binder b ) {
        final ContextScope contextScope = new ContextScope();
        final Provider<Context> throwingContextProvider = ContextScope.<Context>seededKeyProvider();
        final Provider<Context> contextProvider = contextScope.scope(Key.get(Context.class), throwingContextProvider);

        b.bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        b.bindConstant().annotatedWith(Names.named("sharedPreferencesContext")).to("com.tripit");
        b.bind(AstroboyApplication.class).toProvider(Key.get(new TypeLiteral<GuiceApplicationProvider<AstroboyApplication>>(){}));
        b.bind(Resources.class).toProvider(ResourcesProvider.class);

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
