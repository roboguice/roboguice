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
import roboguice.inject.SystemServiceProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

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

        // Services
        bind(LocationManager.class).toProvider( new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        bind(WindowManager.class).toProvider( new SystemServiceProvider<WindowManager>(Context.WINDOW_SERVICE));
        bind(LayoutInflater.class).toProvider( new SystemServiceProvider<LayoutInflater>(Context.LAYOUT_INFLATER_SERVICE));
        bind(ActivityManager.class).toProvider( new SystemServiceProvider<ActivityManager>(Context.ACTIVITY_SERVICE));
        bind(PowerManager.class).toProvider( new SystemServiceProvider<PowerManager>(Context.POWER_SERVICE));
        bind(AlarmManager.class).toProvider( new SystemServiceProvider<AlarmManager>(Context.ALARM_SERVICE));
        bind(NotificationManager.class).toProvider( new SystemServiceProvider<NotificationManager>(Context.NOTIFICATION_SERVICE));
        bind(KeyguardManager.class).toProvider( new SystemServiceProvider<KeyguardManager>(Context.KEYGUARD_SERVICE));
        bind(SearchManager.class).toProvider( new SystemServiceProvider<SearchManager>(Context.SEARCH_SERVICE));
        bind(Vibrator.class).toProvider( new SystemServiceProvider<Vibrator>(Context.VIBRATOR_SERVICE));
        bind(ConnectivityManager.class).toProvider( new SystemServiceProvider<ConnectivityManager>(Context.CONNECTIVITY_SERVICE));
        bind(WifiManager.class).toProvider( new SystemServiceProvider<WifiManager>(Context.WIFI_SERVICE));
        bind(InputMethodManager.class).toProvider( new SystemServiceProvider<InputMethodManager>(Context.INPUT_METHOD_SERVICE));

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
