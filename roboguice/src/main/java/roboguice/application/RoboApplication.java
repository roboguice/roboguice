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

import roboguice.RoboGuice;
import roboguice.inject.InjectorProvider;

import android.app.Application;

import com.google.inject.Injector;

import java.util.List;

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
 * android:name="roboguice.application.RoboApplication"> [...] </application> }
 *
 * @see RoboInjectableApplication How to get your Application injected as well.
 *
 * @author Mike Burton
 */
public class RoboApplication extends Application implements InjectorProvider {


    /**
     * The {@link Injector} of your application.
     */
    protected Injector guiceInjector;


    /**
     * Returns the {@link Injector} of your application. If none exists yet,
     * creates one by calling {@link #createInjector()}. <br />
     * <br />
     * This method is thread-safe.<br />
     * <br />
     * If you decide to override getInjector(), you will have to handle
     * synchronization.
     */
    public Injector getInjector() {
        if (guiceInjector == null) {
            synchronized (this) {
                if (guiceInjector == null) {
                    guiceInjector = createInjector();
                }
            }
        }
        return guiceInjector;
    }


    protected Injector createInjector() {
        return RoboGuice.createInjector(this);
    }

}
