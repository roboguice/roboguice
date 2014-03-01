package roboguice.config;

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
import android.os.Build.VERSION_CODES;
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
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

import roboguice.activity.RoboActivity;
import roboguice.event.EventManager;
import roboguice.event.ObservesTypeListener;
import roboguice.event.eventListener.factory.EventListenerThreadingDecorator;
import roboguice.fragment.FragmentUtil;
import roboguice.inject.*;
import roboguice.service.RoboService;
import roboguice.util.Ln;
import roboguice.util.LnImpl;
import roboguice.util.LnInterface;
import roboguice.util.Strings;

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
@SuppressWarnings("PMD")
public class DefaultRoboModule extends AbstractModule {
    public static final String GLOBAL_EVENT_MANAGER_NAME = "GlobalEventManager";

    @SuppressWarnings("rawtypes")
    protected static final Class ACCOUNT_MANAGER_CLASS;

    static {
        Class<?> c = null;
        try {
            c = Class.forName("android.accounts.AccountManager");
        } catch( Throwable ignored ) {}
        ACCOUNT_MANAGER_CLASS = c;
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
        final PreferenceListener preferenceListener = new PreferenceListener(contextProvider,application);
        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator();

        // Package Info
        try {
            final PackageInfo info = application.getPackageManager().getPackageInfo(application.getPackageName(),0);
            bind(PackageInfo.class).toInstance(info);
        } catch( PackageManager.NameNotFoundException e ) {
            throw new RuntimeException(e);
        }

        String androidId = null;
        final ContentResolver contentResolver = application.getContentResolver();
        try {
            androidId = Secure.getString(contentResolver, Secure.ANDROID_ID);
        } catch( RuntimeException e) {
            // ignore Stub! errors for Secure.getString() when mocking in test cases since there's no way to mock static methods
        }

        if(Strings.notEmpty(androidId))
            bindConstant().annotatedWith(Names.named(Settings.Secure.ANDROID_ID)).to(androidId);



        // Singletons
        bind(ViewListener.class).toInstance(viewListener);
        bind(PreferenceListener.class).toInstance(preferenceListener);
        bind(EventManager.class).annotatedWith(Names.named(GLOBAL_EVENT_MANAGER_NAME)).to(EventManager.class).asEagerSingleton();



        // ContextSingleton bindings
        bindScope(ContextSingleton.class, contextScope);
        bind(ContextScope.class).toInstance(contextScope);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);
        bind(Context.class).toProvider(NullProvider.<Context>instance()).in(ContextSingleton.class);
        bind(Activity.class).toProvider(NullProvider.<Activity>instance()).in(ContextSingleton.class);
        bind(RoboActivity.class).toProvider(NullProvider.<RoboActivity>instance()).in(ContextSingleton.class);
        bind(Service.class).toProvider(NullProvider.<Service>instance()).in(ContextSingleton.class);
        bind(RoboService.class).toProvider(NullProvider.<RoboService>instance()).in(ContextSingleton.class);

        
        // Sundry Android Classes
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
        bind(Application.class).toInstance(application);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);
        bind(Handler.class).toProvider(HandlerProvider.class);



        // System Services
        bind(LocationManager.class).toProvider(new SystemServiceProvider<LocationManager>(application, Context.LOCATION_SERVICE));
        bind(WindowManager.class).toProvider(new SystemServiceProvider<WindowManager>(application, Context.WINDOW_SERVICE));
        bind(ActivityManager.class).toProvider(new SystemServiceProvider<ActivityManager>(application, Context.ACTIVITY_SERVICE));
        bind(PowerManager.class).toProvider(new SystemServiceProvider<PowerManager>(application, Context.POWER_SERVICE));
        bind(AlarmManager.class).toProvider(new SystemServiceProvider<AlarmManager>(application, Context.ALARM_SERVICE));
        bind(NotificationManager.class).toProvider(new SystemServiceProvider<NotificationManager>(application, Context.NOTIFICATION_SERVICE));
        bind(KeyguardManager.class).toProvider(new SystemServiceProvider<KeyguardManager>(application, Context.KEYGUARD_SERVICE));
        bind(Vibrator.class).toProvider(new SystemServiceProvider<Vibrator>(application, Context.VIBRATOR_SERVICE));
        bind(ConnectivityManager.class).toProvider(new SystemServiceProvider<ConnectivityManager>(application, Context.CONNECTIVITY_SERVICE));
        bind(WifiManager.class).toProvider(new SystemServiceProvider<WifiManager>(application, Context.WIFI_SERVICE));
        bind(InputMethodManager.class).toProvider(new SystemServiceProvider<InputMethodManager>(application, Context.INPUT_METHOD_SERVICE));
        bind(SensorManager.class).toProvider( new SystemServiceProvider<SensorManager>(application, Context.SENSOR_SERVICE));
        bind(TelephonyManager.class).toProvider( new SystemServiceProvider<TelephonyManager>(application, Context.TELEPHONY_SERVICE));
        bind(AudioManager.class).toProvider( new SystemServiceProvider<AudioManager>(application, Context.AUDIO_SERVICE));

        // System Services that must be scoped to current context
        bind(LayoutInflater.class).toProvider(new ContextScopedSystemServiceProvider<LayoutInflater>(contextProvider,Context.LAYOUT_INFLATER_SERVICE));
        bind(SearchManager.class).toProvider(new ContextScopedSystemServiceProvider<SearchManager>(contextProvider,Context.SEARCH_SERVICE));


        // Android Resources, Views and extras require special handling
        bindListener(Matchers.any(), resourceListener);
        bindListener(Matchers.any(), extrasListener);
        bindListener(Matchers.any(), viewListener);
        bindListener(Matchers.any(), preferenceListener);
        bindListener(Matchers.any(), new ObservesTypeListener(getProvider(EventManager.class), observerThreadingDecorator));


        bind(LnInterface.class).to(LnImpl.class);

        requestInjection(observerThreadingDecorator);


        requestStaticInjection(Ln.class);

        bindDynamicBindings();
    }

    @SuppressWarnings("unchecked")
    private void bindDynamicBindings() {
        // Compatibility library bindings
        if(FragmentUtil.hasSupport) {
            bind(FragmentUtil.supportFrag.fragmentManagerType()).toProvider(FragmentUtil.supportFrag.fragmentManagerProviderType());
        }
        if(FragmentUtil.hasNative) {
            bind(FragmentUtil.nativeFrag.fragmentManagerType()).toProvider(FragmentUtil.nativeFrag.fragmentManagerProviderType());
        }

        // 2.0 Eclair
        if( VERSION.SDK_INT>=VERSION_CODES.ECLAIR ) {
            //noinspection unchecked
            bind(ACCOUNT_MANAGER_CLASS).toProvider(AccountManagerProvider.class);
        }
    }
}
