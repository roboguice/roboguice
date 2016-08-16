package roboguice.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.app.Service;
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
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import roboguice.inject.AccountManagerProvider;
import roboguice.inject.AssetManagerProvider;
import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScopedSystemServiceProvider;
import roboguice.inject.ContextSingleton;
import roboguice.inject.HandlerProvider;
import roboguice.inject.NullProvider;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SharedPreferencesProvider;
import roboguice.inject.SystemServiceProvider;
import roboguice.util.Ln;
import roboguice.util.LnImpl;
import roboguice.util.LnInterface;

/**
 * A Module that provides bindings and configuration to use Guice on Android.
 * Used by {@link roboguice.RoboGuice}.
 *
 * If you wish to add your own bindings, DO NOT subclass this class.  Instead, create a new
 * module that extends AbstractModule with your own bindings, then do something like the following:
 *
 * RoboGuice.setAppliationInjector( app, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(app)).with(new MyModule() );
 *
 * @author Mike Burton
 * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
 * @see roboguice.RoboGuice#getOrCreateBaseApplicationInjector(android.app.Application, com.google.inject.Stage, com.google.inject.Module...)
 * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
 * @see roboguice.RoboGuice#DEFAULT_STAGE
 */
@SuppressWarnings("PMD")
public class DefaultRoboModule extends AbstractModule {

    @SuppressWarnings("rawtypes")
    private static Map<Class, String> mapSystemSericeClassToName = new HashMap<Class, String>();

    protected Application application;
    protected ContextScope contextScope;

    static {
        mapSystemSericeClassToName.put(LocationManager.class, Context.LOCATION_SERVICE);
        mapSystemSericeClassToName.put(WindowManager.class, Context.WINDOW_SERVICE);
        mapSystemSericeClassToName.put(ActivityManager.class, Context.ACTIVITY_SERVICE);
        mapSystemSericeClassToName.put(PowerManager.class, Context.POWER_SERVICE);
        mapSystemSericeClassToName.put(AlarmManager.class, Context.ALARM_SERVICE);
        mapSystemSericeClassToName.put(NotificationManager.class, Context.NOTIFICATION_SERVICE);
        mapSystemSericeClassToName.put(KeyguardManager.class, Context.KEYGUARD_SERVICE);
        mapSystemSericeClassToName.put(Vibrator.class, Context.VIBRATOR_SERVICE);
        mapSystemSericeClassToName.put(ConnectivityManager.class, Context.CONNECTIVITY_SERVICE);
        mapSystemSericeClassToName.put(WifiManager.class, Context.WIFI_SERVICE);
        mapSystemSericeClassToName.put(InputMethodManager.class, Context.INPUT_METHOD_SERVICE);
        mapSystemSericeClassToName.put(SensorManager.class, Context.SENSOR_SERVICE);
        mapSystemSericeClassToName.put(TelephonyManager.class, Context.TELEPHONY_SERVICE);
        mapSystemSericeClassToName.put(AudioManager.class, Context.AUDIO_SERVICE);
        if (VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mapSystemSericeClassToName.put(DownloadManager.class, Context.DOWNLOAD_SERVICE);
        }
    }

    public DefaultRoboModule(final Application application, ContextScope contextScope) {
        this.application = application;
        this.contextScope = contextScope;
    }

    /**
     * Configure this module to define Android related bindings.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void configure() {

        final Provider<Context> contextProvider = getProvider(Context.class);

        // ContextSingleton bindings
        bindScope(ContextSingleton.class, contextScope);
        //we need to super bind as we inject the scope by code only, not by annotations
        superbind(ContextScope.class).toInstance(contextScope);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);
        bind(Context.class).toProvider(NullProvider.<Context>instance()).in(ContextSingleton.class);
        bind(Activity.class).toProvider(NullProvider.<Activity>instance()).in(ContextSingleton.class);
        bind(Service.class).toProvider(NullProvider.<Service>instance()).in(ContextSingleton.class);

        // Sundry Android Classes
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
        bind(Application.class).toInstance(application);
        bind(Handler.class).toProvider(HandlerProvider.class);

        // System Services
        for (Entry<Class, String> entry : mapSystemSericeClassToName.entrySet()) {
            bindSystemService(entry.getKey(), entry.getValue());
        }

        // System Services that must be scoped to current context
        bind(LayoutInflater.class).toProvider(new ContextScopedSystemServiceProvider<LayoutInflater>(contextProvider, Context.LAYOUT_INFLATER_SERVICE));
        bind(SearchManager.class).toProvider(new ContextScopedSystemServiceProvider<SearchManager>(contextProvider, Context.SEARCH_SERVICE));

        if (isInjectable(Ln.class)) {
            bind(LnInterface.class).to(LnImpl.class);
            //should this be placed in if statement ?
            requestStaticInjection(Ln.class);
        }

        bindDynamicBindings();
    }

    private <T> void bindSystemService(Class<T> c, String androidServiceName) {
        bind(c).toProvider(new SystemServiceProvider<T>(application, androidServiceName));
    }

    @SuppressWarnings("unchecked")
    private void bindDynamicBindings() {
        // Compatibility library bindings
        if (VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            try {
                @SuppressWarnings("rawtypes")
                Class c = Class.forName("android.accounts.AccountManager");
                bind(c).toProvider(AccountManagerProvider.class);
            } catch (Throwable ex) {
                Log.e(DefaultRoboModule.class.getName(), "Impossible to bind AccountManager", ex);
            }
        }
    }

    // ----------------------------------
    //  PROVIDER METHODS
    //  used for lazy bindings, when
    //  instance creation is costly.
    // ----------------------------------

    @Provides
    @Singleton
    public PackageInfo providesPackageInfo() {
        try {
            return application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Named(Settings.Secure.ANDROID_ID)
    public String providesAndroidId() {
        String androidId = null;
        final ContentResolver contentResolver = application.getContentResolver();
        try {
            androidId = Secure.getString(contentResolver, Secure.ANDROID_ID);
        } catch (RuntimeException e) {
            // ignore Stub! errors for Secure.getString() when mocking in test cases since there's no way to mock static methods
            Log.e(DefaultRoboModule.class.getName(), "Impossible to get the android device Id. This may fail 'normally' when testing.", e);
        }

        if (!"".equals(androidId)) {
            return androidId;
        } else {
            throw new RuntimeException("No Android Id.");
        }
    }
}
