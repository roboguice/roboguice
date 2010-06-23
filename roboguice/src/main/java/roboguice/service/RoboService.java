/*
 * Copyright 2009 Michael Burton Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package roboguice.service;

import roboguice.application.RoboApplication;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectorProvider;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;

import com.google.inject.Injector;

/**
 * A {@link RoboService} extends from {@link Service} to provide dynamic
 * injection of collaborators, using Google Guice.<br /> <br />
 * 
 * Your own services that usually extend from {@link Service} should now extend from
 * {@link GuiceActivity}.<br /> <br />
 *
 * If we didn't provide what you need, you have two options : either post an issue on <a
 * href="http://code.google.com/p/roboguice/issues/list">the bug tracker</a>, or
 * implement it yourself. Have a look at the source code of this class (
 * {@link RoboService}), you won't have to write that much changes. And of
 * course, you are welcome to contribute and send your implementations to the
 * RoboGuice project.<br /> <br />
 *
 * You can have access to the Guice
 * {@link Injector} at any time, by calling {@link #getInjector()}.<br />
 *
 * However, you will not have access to Context scoped beans until
 * {@link #onCreate(Bundle)} is called. <br /> <br />
 *
 * @author Mike Burton
 * @author Christine Karman
 */
public abstract class RoboService extends Service implements InjectorProvider {

    protected ContextScope scope;

    @Override
    public void onCreate() {
        final Injector injector = getInjector();
        scope = injector.getInstance(ContextScope.class);
        scope.enter(this);
        injector.injectMembers(this);
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        scope.enter(this);
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        scope.exit(this);
        super.onDestroy();
    }

    /**
     * @see roboguice.application.RoboApplication#getInjector() 
     */
    public Injector getInjector() {
        return ((RoboApplication) getApplication()).getInjector();
    }
}
