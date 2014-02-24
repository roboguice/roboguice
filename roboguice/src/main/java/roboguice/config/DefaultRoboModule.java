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
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
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
 * If you wish to add your own bindings, DO NOT subclass this class. Instead,
 * create a new module that extends AbstractModule with your own bindings, then
 * do something like the following:
 * 
 * RoboGuice.setAppliationInjector( app, RoboGuice.DEFAULT_STAGE,
 * Modules.override(RoboGuice.newDefaultRoboModule(app)).with(new MyModule() );
 * @see com.google.inject.util.Modules#override(com.google.inject.Module...)
 * @see roboguice.RoboGuice#setBaseApplicationInjector(android.app.Application,
 *      com.google.inject.Stage, com.google.inject.Module...)
 * @see roboguice.RoboGuice#newDefaultRoboModule(android.app.Application)
 * @see roboguice.RoboGuice#DEFAULT_STAGE
 * @author Mike Burton
 */
@SuppressWarnings("PMD")
public class DefaultRoboModule extends AbstractModule {
    public static final String GLOBAL_EVENT_MANAGER_NAME = "GlobalEventManager";

    // service feature
    protected boolean hasFeatureLocationService = true;
    protected boolean hasFeatureWindowManagerService = true;
    protected boolean hasFeatureActivityManagerService = true;
    protected boolean hasFeaturePowerManagerService = true;
    protected boolean hasFeatureAlarmManagerService = true;
    protected boolean hasFeatureNotificationManagerService = true;
    protected boolean hasFeatureKeyguardManagerService = true;
    protected boolean hasFeatureVibratorService = true;
    protected boolean hasFeatureConnectivityManagerService = true;
    protected boolean hasFeatureWifiManagerService = true;
    protected boolean hasFeatureInputMethodManagerService = true;
    protected boolean hasFeatureSensorManagerService = true;
    protected boolean hasFeatureTelephonyManagerService = true;
    protected boolean hasFeatureAudioManagerService = true;
    protected boolean hasFeatureSearchManagerService = true;
    protected boolean hasFeatureLayoutInflaterService = true;
    protected boolean hasFeatureAccountManagerService = true;

    //inject extra feature
    protected boolean hasFeatureInjectExtra = true;
    protected boolean hasFeatureInjectView = true;
    protected boolean hasFeatureInjectFragment = true;
    protected boolean hasFeatureInjectPreference = true;
    protected boolean hasFeatureInjectResource = true;

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
        final EventListenerThreadingDecorator observerThreadingDecorator = new EventListenerThreadingDecorator();

        // Package Info
        //TODO
        try {
            final PackageInfo info = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
            bind(PackageInfo.class).toInstance(info);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        //TODO
        String androidId = null;
        final ContentResolver contentResolver = application.getContentResolver();
        try {
            androidId = Secure.getString(contentResolver, Secure.ANDROID_ID);
        } catch (RuntimeException e) {
            // ignore Stub! errors for Secure.getString() when mocking in test
            // cases since there's no way to mock static methods
        }

        if (Strings.notEmpty(androidId))
            bindConstant().annotatedWith(Names.named(Settings.Secure.ANDROID_ID)).to(androidId);

        // Singletons
        bind(EventManager.class).annotatedWith(Names.named(GLOBAL_EVENT_MANAGER_NAME)).to(EventManager.class).asEagerSingleton();

        // ContextSingleton bindings
        bindScope(ContextSingleton.class, contextScope);
        bind(ContextScope.class).toInstance(contextScope);
        bind(AssetManager.class).toProvider(AssetManagerProvider.class);
        bind(Context.class).toProvider(Key.get(new TypeLiteral<NullProvider<Context>>() {
        })).in(ContextSingleton.class);
        bind(Activity.class).toProvider(Key.get(new TypeLiteral<NullProvider<Activity>>() {
        })).in(ContextSingleton.class);
        bind(RoboActivity.class).toProvider(Key.get(new TypeLiteral<NullProvider<RoboActivity>>() {
        })).in(ContextSingleton.class);
        bind(Service.class).toProvider(Key.get(new TypeLiteral<NullProvider<Service>>() {
        })).in(ContextSingleton.class);
        bind(RoboService.class).toProvider(Key.get(new TypeLiteral<NullProvider<RoboService>>() {
        })).in(ContextSingleton.class);

        // Sundry Android Classes
        //TODO
        bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        bind(Resources.class).toProvider(ResourcesProvider.class);
        bind(ContentResolver.class).toProvider(ContentResolverProvider.class);
        bind(Application.class).toInstance(application);
        bind(EventListenerThreadingDecorator.class).toInstance(observerThreadingDecorator);
        bind(Handler.class).toProvider(HandlerProvider.class);

        // System Services
        if (hasFeatureLocationService) {
            bind(LocationManager.class).toProvider(new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        }
        if (hasFeatureWindowManagerService) {
            bind(WindowManager.class).toProvider(new SystemServiceProvider<WindowManager>(Context.WINDOW_SERVICE));
        }
        if (hasFeatureActivityManagerService) {
            bind(ActivityManager.class).toProvider(new SystemServiceProvider<ActivityManager>(Context.ACTIVITY_SERVICE));
        }
        if (hasFeaturePowerManagerService) {
            bind(PowerManager.class).toProvider(new SystemServiceProvider<PowerManager>(Context.POWER_SERVICE));
        }
        if (hasFeatureAlarmManagerService) {
            bind(AlarmManager.class).toProvider(new SystemServiceProvider<AlarmManager>(Context.ALARM_SERVICE));
        }
        if (hasFeatureNotificationManagerService) {
            bind(NotificationManager.class).toProvider(new SystemServiceProvider<NotificationManager>(Context.NOTIFICATION_SERVICE));
        }
        if (hasFeatureKeyguardManagerService) {
            bind(KeyguardManager.class).toProvider(new SystemServiceProvider<KeyguardManager>(Context.KEYGUARD_SERVICE));
        }
        if (hasFeatureVibratorService) {
            bind(Vibrator.class).toProvider(new SystemServiceProvider<Vibrator>(Context.VIBRATOR_SERVICE));
        }
        if (hasFeatureConnectivityManagerService) {
            bind(ConnectivityManager.class).toProvider(new SystemServiceProvider<ConnectivityManager>(Context.CONNECTIVITY_SERVICE));
        }
        if (hasFeatureWifiManagerService) {
            bind(WifiManager.class).toProvider(new SystemServiceProvider<WifiManager>(Context.WIFI_SERVICE));
        }
        if (hasFeatureInputMethodManagerService) {
            bind(InputMethodManager.class).toProvider(new SystemServiceProvider<InputMethodManager>(Context.INPUT_METHOD_SERVICE));
        }
        if (hasFeatureSensorManagerService) {
            bind(SensorManager.class).toProvider(new SystemServiceProvider<SensorManager>(Context.SENSOR_SERVICE));
        }
        if (hasFeatureTelephonyManagerService) {
            bind(TelephonyManager.class).toProvider(new SystemServiceProvider<TelephonyManager>(Context.TELEPHONY_SERVICE));
        }
        if (hasFeatureAudioManagerService) {
            bind(AudioManager.class).toProvider(new SystemServiceProvider<AudioManager>(Context.AUDIO_SERVICE));
        }

        // System Services that must be scoped to current context
        if (hasFeatureLayoutInflaterService) {
            bind(LayoutInflater.class).toProvider(new ContextScopedSystemServiceProvider<LayoutInflater>(contextProvider, Context.LAYOUT_INFLATER_SERVICE));
        }
        if (hasFeatureSearchManagerService) {
            bind(SearchManager.class).toProvider(new ContextScopedSystemServiceProvider<SearchManager>(contextProvider, Context.SEARCH_SERVICE));
        }

        // Android Resources, Views and extras require special handling
        if( hasFeatureInjectResource ) {
            bindListener(Matchers.any(), resourceListener);
        }

        if( hasFeatureInjectExtra ) {
            bindListener(Matchers.any(), new ExtrasListener(contextProvider));
        }

        if( hasFeatureInjectView ) {
            bind(ViewListener.class).toInstance(viewListener);
            bindListener(Matchers.any(), viewListener);
        }

        if( hasFeatureInjectPreference ) {
            final PreferenceListener preferenceListener = new PreferenceListener(contextProvider, application);
            bind(PreferenceListener.class).toInstance(preferenceListener);
            bindListener(Matchers.any(), preferenceListener);
        }

        bindListener(Matchers.any(), new ObservesTypeListener(getProvider(EventManager.class), observerThreadingDecorator));

        bind(LnInterface.class).to(LnImpl.class);

        requestInjection(observerThreadingDecorator);

        requestStaticInjection(Ln.class);

        bindDynamicBindings();
    }

    @SuppressWarnings("unchecked")
    private void bindDynamicBindings() {
        // Compatibility library bindings
        if (FragmentUtil.hasSupport) {
            bind(FragmentUtil.supportFrag.fragmentManagerType()).toProvider(FragmentUtil.supportFrag.fragmentManagerProviderType());
        }
        if (FragmentUtil.hasNative) {
            bind(FragmentUtil.nativeFrag.fragmentManagerType()).toProvider(FragmentUtil.nativeFrag.fragmentManagerProviderType());
        }

        // 2.0 Eclair
        if (hasFeatureAccountManagerService && VERSION.SDK_INT >= VERSION_CODES.ECLAIR) {
            @SuppressWarnings("rawtypes")
            Class accountManagerServiceClass = null;
            try {
                accountManagerServiceClass = Class.forName("android.accounts.AccountManager");
                // noinspection unchecked
                bind(accountManagerServiceClass).toProvider(AccountManagerProvider.class);
            } catch (Throwable ignored) {
            }
        }
    }
}
