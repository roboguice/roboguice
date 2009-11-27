/*
 * Copyright 2009 Michael Burton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.application;

import java.util.ArrayList;
import java.util.List;

import roboguice.config.AbstractAndroidModule;
import roboguice.inject.ActivityProvider;
import roboguice.inject.ContextScope;
import roboguice.inject.ContextScoped;
import roboguice.inject.ExtrasListener;
import roboguice.inject.InjectorProvider;
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

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.google.inject.matcher.Matchers;

/**
 * This class is in charge of starting the Guice configuration. When the
 * {@link #getInjector()} method is called for the first time, a new Injector is
 * created, and the magic begins !<br />
 * <br />
 * To add your own custom bindings, you should override this class and override
 * the {@link #addApplicationModules(List)} method. <br />
 * <br />
 * You must define this class (or any subclass) as the application in your
 * <strong>AndroidManifest.xml</strong> file. This can be done by adding {@code
 * android:name="fully qualified name of your application class"} to the {@code
 * <application/>} tag. <br />
 * <br />
 * For instance : <br /> {@code <application android:icon="@drawable/icon"
 * android:label="@string/app_name"
 * android:name="roboguice.application.GuiceApplication"> [...] </application> }
 * 
 * @see GuiceInjectableApplication How to get your Application injected as well.
 */
public class GuiceApplication extends Application implements Module, InjectorProvider {

    protected static int instanceCount = 0;

    public GuiceApplication() {
        if (instanceCount > 0) {
            throw new IllegalStateException(
            "A GuiceApplication should not be instanciated more than once. This exception may happen if you try to inject a subclass of Application without specific binding configuration");
        }
        instanceCount++;
    }

    /**
     * The {@link Injector} of your application.
     */
    protected Injector                 guice;

    protected ContextScope             contextScope;
    protected Provider<Context>        throwingContextProvider;
    protected Provider<Context>        contextProvider;
    protected ResourceListener         resourceListener;
    protected ExtrasListener           extrasListener;
    protected List<StaticTypeListener> staticTypeListeners;

    /**
     * Returns the {@link Injector} of your application. If none exists yet,
     * creates one by calling {@link #createInjector()}. <br />
     * <br />
     * This method is thread-safe.<br />
     * <br />
     * If you decide to override {@link #getInjector()}, you will have to handle
     * synchronization.
     */
    public Injector getInjector() {
        if (guice == null) {
            synchronized (this) {
                if (guice == null) {
                    initInstanceMembers();
                    guice = createInjector();
                }
            }
        }
        return guice;
    }

    /**
     * Since we don't create the injector when the {@link GuiceApplication} is
     * instantiated, but rather when getInjector is first called (lazy
     * initialization), the same lazy initialization is applied to this
     * application instance members, which are not used until the injector is
     * first created. The main advantage is that robo-guice footprint is close
     * to zero if no GuiceActivity is used when running the application.
     */
    protected void initInstanceMembers() {
        contextScope = new ContextScope();

        throwingContextProvider = ContextScope.<Context> seededKeyProvider();

        contextProvider = contextScope.scope(Key.get(Context.class), throwingContextProvider);

        resourceListener = new ResourceListener(contextProvider, this);

        extrasListener = new ExtrasListener(contextProvider);

        staticTypeListeners = new ArrayList<StaticTypeListener>();
        staticTypeListeners.add(resourceListener);
    }

    /**
     * Creates an {@link Injector} configured for this application. This
     * {@link Injector} will be configured with this (being a {@link Module}) ,
     * plus any {@link Module} you might add by overriding
     * {@link #addApplicationModules(List)}. <br />
     * <br />
     * In most cases, you should <strong>NOT</strong> override the
     * {@link #createInjector()} method.
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
     * You should override this method to add your own custom bindings. <br />
     * To do so, you must create implementations of the {@link Module}
     * interface, and add them to the list of {@link Module} given as a
     * parameter. The easiest way to create an {@link Module} implementation is
     * to subclass {@link AbstractAndroidModule}, which provides proxy methods
     * to the binder methods (enabling more readable configuration)<br />
     * <br />
     * This method is called by {@link #createInjector()}.<br />
     * <br />
     * The default implementation is a no-op and does nothing.
     * 
     * @param modules
     *            The list of modules to which you may add your own custom
     *            modules. Please notice that it already contains one module,
     *            which is this.
     */
    protected void addApplicationModules(List<Module> modules) {
    }

    /**
     * Configure this module to define Android related bindings.<br />
     * <br />
     * If you want to provide your own bindings, you should <strong>NOT</strong>
     * override this method, but rather create a {@link Module} implementation
     * and add it to the configuring modules by overriding
     * {@link #addApplicationModules(List)}.<br />
     */
    public void configure(Binder b) {

        // Sundry Android Classes
        b.bind(SharedPreferences.class).toProvider(SharedPreferencesProvider.class);
        b.bind(Resources.class).toProvider(ResourcesProvider.class);

        for (Class<?> c = getClass(); c != null && Application.class.isAssignableFrom(c); c = c
        .getSuperclass()) {
            b.bind((Class<Object>) c).toInstance(this);
        }

        // System Services
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

        // Context Scope bindings
        b.bindScope(ContextScoped.class, contextScope);
        b.bind(ContextScope.class).toInstance(contextScope);
        b.bind(Context.class).toProvider(throwingContextProvider).in(ContextScoped.class);
        b.bind(Activity.class).toProvider(ActivityProvider.class);

        // Android Resources and extras require special handling
        b.bindListener(Matchers.any(), resourceListener);
        b.bindListener(Matchers.any(), extrasListener);
    }

    public List<StaticTypeListener> getStaticTypeListeners() {
        return staticTypeListeners;
    }
}
