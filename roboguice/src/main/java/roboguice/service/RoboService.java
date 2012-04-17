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

import roboguice.RoboGuice;
import roboguice.event.EventManager;
import roboguice.service.event.OnConfigurationChangedEvent;
import roboguice.service.event.OnCreateEvent;
import roboguice.service.event.OnDestroyEvent;
import roboguice.service.event.OnStartEvent;
import roboguice.util.RoboContext;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;

import com.google.inject.Injector;
import com.google.inject.Key;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link RoboService} extends from {@link Service} to provide dynamic
 * injection of collaborators, using Google Guice.<br /> <br />
 * 
 * Your own services that usually extend from {@link Service} should now extend from
 * {@link RoboService}.<br /> <br />
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
 * However, you will not have access to ContextSingleton scoped beans until
 * {@link #onCreate()} is called. <br /> <br />
 *
 * @author Mike Burton
 * @author Christine Karman
 */
public abstract class RoboService extends Service implements RoboContext {

    protected EventManager eventManager;
    protected HashMap<Key<?>,Object> scopedObjects = new HashMap<Key<?>, Object>();

    @Override
    public void onCreate() {
        final Injector injector = RoboGuice.getInjector(this);
        eventManager = injector.getInstance(EventManager.class);
        injector.injectMembers(this);
        super.onCreate();
        eventManager.fire(new OnCreateEvent() );
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        eventManager.fire(new OnStartEvent());
    }

    @Override
    public void onDestroy() {
        try {
            if(eventManager!=null) // may be null during test: http://code.google.com/p/roboguice/issues/detail?id=140
                eventManager.fire(new OnDestroyEvent() );
        } finally {
            try {
                RoboGuice.destroyInjector(this);
            } finally {
                super.onDestroy();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig) );
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }

}
