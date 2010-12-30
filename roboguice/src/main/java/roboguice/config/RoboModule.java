package roboguice.config;

import android.app.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import roboguice.inject.*;
import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;
import roboguice.util.RoboThread;

import java.util.List;

/**
 * A Module that provides bindings and configuration to use Guice on Android.
 * Used by {@link roboguice.application.RoboApplication}.
 *
 * @author Mike Burton
 * @author Pierre-Yves Ricau (py.ricau+roboguice@gmail.com)
 */
public class RoboModule extends AbstractModule {

    protected final ContextScope contextScope;
    protected final Provider<Context> throwingContextProvider;
    protected final Provider<Context> contextProvider;
    protected final ResourceListener resourceListener;
    protected final ViewListener viewListener;
    protected final ExtrasListener extrasListener;
    protected final PreferenceListener preferenceListener;
    protected final Application application;
    protected final ContextObservationManager observationManager;
    protected final ContextObserverClassEventManager classEventObservationManager;

    public RoboModule(ContextScope contextScope, Provider<Context> throwingContextProvider, Provider<Context> contextProvider,
            ResourceListener resourceListener, ViewListener viewListener, ExtrasListener extrasListener,
            PreferenceListener preferenceListener, ContextObservationManager observationManager,
            ContextObserverClassEventManager classEventObservationManager, Application application) {
        this.contextScope = contextScope;
        this.throwingContextProvider = throwingContextProvider;
        this.contextProvider = contextProvider;
        this.resourceListener = resourceListener;
        this.viewListener = viewListener;
        this.extrasListener = extrasListener;
        this.preferenceListener = preferenceListener;
        this.observationManager = observationManager;
        this.classEventObservationManager = classEventObservationManager;
        this.application = application;
    }

    /**
     * Configure this module to define Android related bindings.<br />
     * <br />
     * If you want to provide your own bindings, you should <strong>NOT</strong>
     * override this method, but rather create a {@link Module} implementation
     * and add it to the configuring modules by overriding
     * {@link roboguice.application.RoboApplication#addApplicationModules(List)}.<br />
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void configure() {
        // Context Scope bindings
        bindScope(ContextScoped.class, contextScope);
        bind(ContextScope.class).toInstance(contextScope);
        bind(Context.class).toProvider(throwingContextProvider).in(ContextScoped.class);
        bind(Activity.class).toProvider(ActivityProvider.class);
        bind(AssetManager.class).toProvider( AssetManagerProvider.class );

        // Sundry Android Classes
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);

        // Context observers
        bind(ContextObservationManager.class).toInstance(observationManager);
        bind(ContextObserverClassEventManager.class).toInstance(classEventObservationManager);

        for (Class<? extends Object> c = application.getClass(); c != null && Application.class.isAssignableFrom(c); c = c.getSuperclass()) {
            bind((Class<Object>) c).toInstance(application);
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


        // Android Resources, Views and extras require special handling
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), extrasListener);
        bindListener(Matchers.any(), viewListener);

        if (preferenceListener != null) {
          bindListener(Matchers.any(), preferenceListener);
        }

        if (observationManager.isEnabled()) {
            bindListener(Matchers.any(), new ContextObserverTypeListener(contextProvider, observationManager));
        }

        if(classEventObservationManager.isEnabled()){
            bindListener(Matchers.any(), new ContextObserverClassEventTypeListener(contextProvider, classEventObservationManager));
        }

        requestStaticInjection( Ln.class );
        requestStaticInjection( RoboThread.class );
        requestStaticInjection( RoboAsyncTask.class );
    }

}
