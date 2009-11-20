package roboguice.application;

import java.util.ArrayList;
import java.util.List;

import roboguice.config.AndroidModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

import android.app.Application;

/**
 * This class is in charge of starting the Guice configuration. When the {@link #getInjector()} method is called for the
 * first time, a new Injector is created, and the magic begins !<br />
 * <br />
 * To add your own custom bindings, you should override this class and override the {@link #addApplicationModules(List)}
 * method. <br />
 * <br />
 * You must define this class (or any subclass) as the application in your <strong>AndroidManifest.xml</strong> file.
 * This can be done by adding <strong>android:name="fully qualified name of your application class"</strong> to the
 * &lt;application/&gt; tag. <br />
 * <br />
 * For instance : &lt;application android:icon="@drawable/icon" android:label="@string/app_name"
 * android:name="roboguice.application.GuiceApplication"&gt;&lt;/application&gt;
 *
 * @see GuiceInjectableApplication How to get your Application injected as well.
 */
public class GuiceApplication extends Application {

    /**
     * The {@link Injector} of your application.
     */
    protected Injector guice;

    /**
     * Returns the {@link Injector} of your application. If none exists yet, creates one by calling
     * {@link #createInjector()}.
     */
    public synchronized Injector getInjector() {
        return guice != null ? guice : (guice = createInjector());
    }

    /**
     * Creates an {@link Injector} configured for this application. This {@link Injector} will be configured with an
     * {@link AndroidModule}, plus any {@link Module} you might add by overriding {@link #addApplicationModules(List)}. <br />
     * <br />
     * In most cases, you should <strong>NOT</strong> override the {@link #createInjector()} method, unless you don't
     * want an {@link AndroidModule} to be created.
     */
    protected synchronized Injector createInjector() {
        ArrayList<Module> modules = new ArrayList<Module>();
        modules.add(new AndroidModule(this));
        addApplicationModules(modules);
        return Guice.createInjector(Stage.PRODUCTION, modules);
    }

    /**
     * You should override this method to do your own custom bindings. <br />
     * To do so, you must create implementations of the {@link Module} interface, and add them to the list of
     * {@link Module} given as a parameter. <br />
     * <br />
     * This method is called by {@link #createInjector()}.<br />
     * <br />
     * The default implementation is a no-op and does nothing.
     *
     * @param modules
     *            The list of modules to which you may add your own custom modules. Please notice that it already
     *            contains one module, which is an instance of {@link AndroidModule}.
     */
    protected void addApplicationModules(List<Module> modules) {
    }

}
