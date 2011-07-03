package roboguice.config;

import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.inject.*;
import roboguice.util.Ln;
import roboguice.util.Strings;

import android.app.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * A Module that provides bindings and configuration to use Guice on Android.
 * Used by {@link roboguice.RoboGuice}.
 *
 * @author Mike Burton
 */
public class RoboModule extends AbstractModule {

    protected Application application;
    protected Provider<Context> contextProvider;
    protected ContextScope contextScope;
    protected ResourceListener resourceListener;
    protected ViewListener viewListener;
    protected EventManager eventManager;


    public RoboModule( final Application application) {

        final Provider<Context> throwingContextProvider = new Provider<Context>() {
            public Context get() {
                return application;
            }
        };

        this.application = application;
        contextScope = new ContextScope(application);
        contextProvider = contextScope.scope(Key.get(Context.class), throwingContextProvider);
        viewListener = new ViewListener(contextProvider, application, contextScope);
        resourceListener = new ResourceListener(application);
        eventManager = new EventManager();
    }

    /**
     * Configure this module to define Android related bindings.
     */
    @Override
    protected void configure() {

        final ExtrasListener extrasListener = new ExtrasListener(contextProvider);
        final PreferenceListener preferenceListener = new PreferenceListener(contextProvider,application,contextScope);
        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator();
        final String androidId = Secure.getString(application.getContentResolver(), Secure.ANDROID_ID);

        if(Strings.notEmpty(androidId))
            bindConstant().annotatedWith(Names.named(Settings.Secure.ANDROID_ID)).to(androidId);

        // Context Scope bindings
        bindScope(ContextScoped.class, contextScope);
        bind(ContextScope.class).toInstance(contextScope);
        bind(Context.class).toProvider(contextProvider).in(ContextScoped.class);
        bind(Activity.class).toProvider(ActivityProvider.class);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);

        
        // Sundry Android Classes
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
        bind(Application.class).toInstance(application);
        bind(EventManager.class).toInstance(eventManager);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);


        // Package Info
        try {
            final PackageInfo info = application.getPackageManager().getPackageInfo(application.getPackageName(),0);
            bind(PackageInfo.class).toInstance(info);
        } catch( PackageManager.NameNotFoundException e ) {
            throw new RuntimeException(e);
        }

        // System Services
        bind(LocationManager.class).toProvider(new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        bind(WindowManager.class).toProvider(new SystemServiceProvider<WindowManager>(Context.WINDOW_SERVICE));
        bind(LayoutInflater.class).toProvider(new SystemServiceProvider<LayoutInflater>(Context.LAYOUT_INFLATER_SERVICE));
        bind(ActivityManager.class).toProvider(new SystemServiceProvider<ActivityManager>(Context.ACTIVITY_SERVICE));
        bind(PowerManager.class).toProvider(new SystemServiceProvider<PowerManager>(Context.POWER_SERVICE));
        bind(AlarmManager.class).toProvider(new SystemServiceProvider<AlarmManager>(Context.ALARM_SERVICE));
        bind(NotificationManager.class).toProvider(new SystemServiceProvider<NotificationManager>(Context.NOTIFICATION_SERVICE));
        bind(KeyguardManager.class).toProvider(new SystemServiceProvider<KeyguardManager>(Context.KEYGUARD_SERVICE));
        bind(SearchManager.class).toProvider(new SystemServiceProvider<SearchManager>(Context.SEARCH_SERVICE));
        bind(Vibrator.class).toProvider(new SystemServiceProvider<Vibrator>(Context.VIBRATOR_SERVICE));
        bind(ConnectivityManager.class).toProvider(new SystemServiceProvider<ConnectivityManager>(Context.CONNECTIVITY_SERVICE));
        bind(WifiManager.class).toProvider(new SystemServiceProvider<WifiManager>(Context.WIFI_SERVICE));
        bind(InputMethodManager.class).toProvider(new SystemServiceProvider<InputMethodManager>(Context.INPUT_METHOD_SERVICE));
        bind(SensorManager.class).toProvider( new SystemServiceProvider<SensorManager>(Context.SENSOR_SERVICE));
        bind(TelephonyManager.class).toProvider( new SystemServiceProvider<TelephonyManager>(Context.TELEPHONY_SERVICE));
        bind(AudioManager.class).toProvider( new SystemServiceProvider<AudioManager>(Context.AUDIO_SERVICE));


        // Android Resources, Views and extras require special handling
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), extrasListener);
        bindListener(Matchers.any(), viewListener);
        bindListener(Matchers.any(), preferenceListener);
        bindListener(Matchers.any(), new ObservesTypeListener(eventManager, observerThreadingDecorator));


        requestInjection(observerThreadingDecorator);
        requestInjection(eventManager);
        

        requestStaticInjection(Ln.class);
    }

}
