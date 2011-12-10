package roboguice.config;

import roboguice.activity.RoboActivity;
import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.inject.*;
import roboguice.service.RoboService;
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
import android.os.Build.VERSION;
import android.os.Handler;
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
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * A Module that provides bindings and configuration to use Guice on Android.
 * Used by {@link roboguice.RoboGuice}.
 *
 * If you wish to add your own bindings, DO NOT subclass this class.  Instead, create a new
 * module that extends AbstractModule with your own bindings, then do something like the following:
 *
 * RoboGuice.setAppliationInjector( app, RoboGuice.DEFAULT_STAGE, Modules.override(RoboGuice.newDefaultRoboModule(app)).with(new MyModule() );
 *
 * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
 * @see roboguice.RoboGuice#setBaseApplicationInjector(android.app.Application, com.google.inject.Stage, com.google.inject.Module...)
 * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
 * @see roboguice.RoboGuice#DEFAULT_STAGE
 *
 * @author Mike Burton
 */
public class DefaultRoboModule extends AbstractModule {
    protected static final Class fragmentManagerClass;
    protected static final Class accountManagerClass;

    static {
        Class c = null;
        try {
            c = Class.forName("android.support.v4.app.FragmentManager");
        } catch( Exception ignored ) {}
        fragmentManagerClass = c;
    }

    static {
        Class c = null;
        try {
            c = Class.forName("android.accounts.AccountManager");
        } catch( Exception ignored ) {}
        accountManagerClass = c;
    }


    protected Application application;
    protected ContextScope contextScope;
    protected ResourceListener resourceListener;
    protected ViewListener viewListener;


    public DefaultRoboModule(final Application application, ContextScope contextScope, ViewListener viewListener, ResourceListener resourceListener) {


        this.application = application;
        this.contextScope = contextScope;
        this.viewListener = viewListener;
        this.resourceListener = resourceListener;
    }

    /**
     * Configure this module to define Android related bindings.
     */
    @Override
    protected void configure() {

        final Provider<Context> contextProvider = getProvider(Context.class);
        final ExtrasListener extrasListener = new ExtrasListener(contextProvider);
        final PreferenceListener preferenceListener = new PreferenceListener(contextProvider,application,contextScope);
        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator();
        final String androidId = Secure.getString(application.getContentResolver(), Secure.ANDROID_ID);

        // Package Info
        try {
            final PackageInfo info = application.getPackageManager().getPackageInfo(application.getPackageName(),0);
            bind(PackageInfo.class).toInstance(info);
        } catch( PackageManager.NameNotFoundException e ) {
            throw new RuntimeException(e);
        }


        if(Strings.notEmpty(androidId))
            bindConstant().annotatedWith(Names.named(Settings.Secure.ANDROID_ID)).to(androidId);


        // Singletons
        bind(ViewListener.class).toInstance(viewListener);
        bind(PreferenceListener.class).toInstance(preferenceListener);



        // ContextSingleton bindings
        bindScope(ContextSingleton.class, contextScope);
        bind(ContextScope.class).toInstance(contextScope);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);
        bind(Context.class).toProvider(Key.get(new TypeLiteral<NullProvider<Context>>(){})).in(ContextSingleton.class);
        bind(Activity.class).toProvider(Key.get(new TypeLiteral<NullProvider<Activity>>(){})).in(ContextSingleton.class);
        bind(RoboActivity.class).toProvider(Key.get(new TypeLiteral<NullProvider<RoboActivity>>(){})).in(ContextSingleton.class);
        bind(Service.class).toProvider(Key.get(new TypeLiteral<NullProvider<Service>>(){})).in(ContextSingleton.class);
        bind(RoboService.class).toProvider(Key.get(new TypeLiteral<NullProvider<RoboService>>(){})).in(ContextSingleton.class);

        
        // Sundry Android Classes
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
        bind(Application.class).toInstance(application);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);
        bind(Handler.class).toProvider(HandlerProvider.class);



        // System Services
        bind(LocationManager.class).toProvider(new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        bind(WindowManager.class).toProvider(new SystemServiceProvider<WindowManager>(Context.WINDOW_SERVICE));
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

        // System Services that must be scoped to current context
        bind(LayoutInflater.class).toProvider(new ContextScopedSystemServiceProvider<LayoutInflater>(contextProvider,Context.LAYOUT_INFLATER_SERVICE));


        // Android Resources, Views and extras require special handling
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), extrasListener);
        bindListener(Matchers.any(), viewListener);
        bindListener(Matchers.any(), preferenceListener);
        bindListener(Matchers.any(), new ObservesTypeListener(getProvider(EventManager.class), observerThreadingDecorator));


        requestInjection(observerThreadingDecorator);


        requestStaticInjection(Ln.class);

        // Compatibility library bindings
        if(fragmentManagerClass!=null) {
            bind(fragmentManagerClass).toProvider(FragmentManagerProvider.class);
        }


        // 2.0 Eclair
        if( VERSION.SDK_INT>=5 ) {
            bind(accountManagerClass).toProvider(AccountManagerProvider.class);
        }


    }

}
