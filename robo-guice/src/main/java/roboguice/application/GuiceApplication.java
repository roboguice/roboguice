package roboguice.application;

import java.util.ArrayList;
import java.util.List;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ActivityProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScoped;
import roboguice.inject.ExtrasListener;
import roboguice.inject.GuiceApplicationProvider;
import roboguice.inject.ResourceListener;
import roboguice.inject.ResourcesProvider;
import roboguice.inject.SharedPreferencesProvider;
import roboguice.inject.StaticTypeListener;
import roboguice.inject.SystemServiceProvider;
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

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

/**
 * This class is in charge of starting the Guice configuration. When the {@link #getInjector()} method is called for the
 * first time, a new Injector is created, and the magic begins !<br />
 * <br />
 * To add your own custom bindings, you should override this class and override the {@link #addApplicationModules(List)}
 * method. <br />
 * <br />
 * You must define this class (or any subclass) as the application in your <strong>AndroidManifest.xml</strong> file.
 * This can be done by adding {@code android:name="fully qualified name of your application class"} to the {@code
 * <application/>} tag. <br />
 * <br />
 * For instance : <br /> {@code <application android:icon="@drawable/icon" android:label="@string/app_name"
 * android:name="roboguice.application.GuiceApplication"> [...] </application> }
 * 
 * @see GuiceInjectableApplication How to get your Application injected as well.
 */
public class GuiceApplication extends Application implements Module {

    /**
     * The {@link Injector} of your application.
     */
    protected Injector                 guice;

    protected ContextScope             contextScope            = new ContextScope();
    protected Provider<Context>        throwingContextProvider = ContextScope.<Context> seededKeyProvider();
    protected Provider<Context>        contextProvider         = contextScope.scope(Key.get(Context.class),
                                                                       throwingContextProvider);
    protected ResourceListener         resourceListener        = new ResourceListener(contextProvider, this);
    protected ExtrasListener           extrasListener          = new ExtrasListener(contextProvider);
    protected List<StaticTypeListener> staticTypeListeners     = new ArrayList<StaticTypeListener>();

    {
        staticTypeListeners.add(resourceListener);
    }

    /**
     * Returns the {@link Injector} of your application. If none exists yet, creates one by calling
     * {@link #createInjector()}. <br />
     * <br />
     * This method is thread-safe.<br />
     * <br />
     * If you decide to override {@link #getInjector()}, you will have to handle synchronization.
     */
    public Injector getInjector() {
        if (guice == null) {
            synchronized (this) {
                if (guice == null) {
                    guice = createInjector();
                }
            }
        }
        return guice;
    }

    /**
     * Creates an {@link Injector} configured for this application. This {@link Injector} will be configured with this
     * (being a {@link Module}) , plus any {@link Module} you might add by overriding
     * {@link #addApplicationModules(List)}. <br />
     * <br />
     * In most cases, you should <strong>NOT</strong> override the {@link #createInjector()} method, unless you don't
     * want an this to be used as a configuration module.
     */
    protected Injector createInjector() {
        ArrayList<Module> modules = new ArrayList<Module>();
        modules.add(this);
        addApplicationModules(modules);
        for (Module m : modules) {
            if (m instanceof AbstractAndroidModule) {
                ((AbstractAndroidModule) m).setStaticTypeListeners(staticTypeListeners);
            }
        }
        return Guice.createInjector(Stage.PRODUCTION, modules);
    }

    /**
     * You should override this method to do your own custom bindings. <br />
     * To do so, you must create implementations of the {@link Module} interface, and add them to the list of
     * {@link Module} given as a parameter. The easiest way to create an {@link Module} implementation is to subclass
     * {@link AbstractModule}, which provides proxy methods to the binder methods (enabling more readable configuration)<br />
     * <br />
     * If you want to enable static injection of resources and extras, please see {@link AbstractAndroidModule}.<br />
     * <br />
     * This method is called by {@link #createInjector()}.<br />
     * <br />
     * The default implementation is a no-op and does nothing.
     * 
     * @param modules
     *            The list of modules to which you may add your own custom modules. Please notice that it already
     *            contains one module, which is this.
     */
    protected void addApplicationModules(List<Module> modules) {
    }

    /**
     * Configure this module to define Android related bindings.<br />
     * <br />
     * If you want to provide your own bindings, you should <strong>NOT</strong> override this method, but rather create
     * a {@link Module} implementation and add it to the configuring modules by overriding
     * {@link #addApplicationModules(List)}.<br />
     * <br />
     * However, if you still decide to override {@link #configure(Binder)}, please do not forget to call {@code
     * super.configure(b)} from the overriding {@link #configure(Binder)} method. <br />
     * <br />
     * The only exception might be if you do not want the default bindings provided by robo-guice. Please notice that
     * even if you do not currently use these bindings, they might prove useful to you in a close future.
     */
    public void configure(Binder b) {

        b.bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        b.bind(Resources.class).toProvider(ResourcesProvider.class);
        b.bind(GuiceApplication.class).toProvider(
                Key.get(new TypeLiteral<GuiceApplicationProvider<GuiceApplication>>() {
                }));

        // Services
        b.bind(LocationManager.class).toProvider(new SystemServiceProvider<LocationManager>(Context.LOCATION_SERVICE));
        b.bind(WindowManager.class).toProvider(new SystemServiceProvider<WindowManager>(Context.WINDOW_SERVICE));
        b.bind(LayoutInflater.class).toProvider(
                new SystemServiceProvider<LayoutInflater>(Context.LAYOUT_INFLATER_SERVICE));
        b.bind(ActivityManager.class).toProvider(new SystemServiceProvider<ActivityManager>(Context.ACTIVITY_SERVICE));
        b.bind(PowerManager.class).toProvider(new SystemServiceProvider<PowerManager>(Context.POWER_SERVICE));
        b.bind(AlarmManager.class).toProvider(new SystemServiceProvider<AlarmManager>(Context.ALARM_SERVICE));
        b.bind(NotificationManager.class).toProvider(
                new SystemServiceProvider<NotificationManager>(Context.NOTIFICATION_SERVICE));
        b.bind(KeyguardManager.class).toProvider(new SystemServiceProvider<KeyguardManager>(Context.KEYGUARD_SERVICE));
        b.bind(SearchManager.class).toProvider(new SystemServiceProvider<SearchManager>(Context.SEARCH_SERVICE));
        b.bind(Vibrator.class).toProvider(new SystemServiceProvider<Vibrator>(Context.VIBRATOR_SERVICE));
        b.bind(ConnectivityManager.class).toProvider(
                new SystemServiceProvider<ConnectivityManager>(Context.CONNECTIVITY_SERVICE));
        b.bind(WifiManager.class).toProvider(new SystemServiceProvider<WifiManager>(Context.WIFI_SERVICE));
        b.bind(InputMethodManager.class).toProvider(
                new SystemServiceProvider<InputMethodManager>(Context.INPUT_METHOD_SERVICE));

        // Context Scope b.bindings
        b.bindScope(ContextScoped.class, contextScope);
        b.bind(ContextScope.class).toInstance(contextScope);
        b.bind(Context.class).toProvider(throwingContextProvider).in(ContextScoped.class);
        b.bind(Activity.class).toProvider(ActivityProvider.class);

        // Android Resources require special handling
        b.bindListener(Matchers.any(), resourceListener);
        b.bindListener(Matchers.any(), extrasListener);
    }
}
