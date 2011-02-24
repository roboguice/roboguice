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
package roboguice.activity;

import roboguice.activity.event.*;
import roboguice.application.RoboApplication;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectorProvider;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.google.inject.Injector;

/**
 * A {@link RoboListActivity} extends from {@link ListActivity} to provide
 * dynamic injection of collaborators, using Google Guice.<br />
 * 
 * @see RoboActivity
 * 
 * @author Mike Burton
 */
public class RoboListActivity extends ListActivity implements InjectorProvider {
    protected EventManager eventManager;
    protected ContextScope scope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Injector injector = getInjector();
        eventManager = injector.getInstance(EventManager.class);
        scope = injector.getInstance(ContextScope.class);
        scope.enter(this);
        injector.injectMembers(this);
        super.onCreate(savedInstanceState);
        eventManager.fire(new OnCreateEvent(savedInstanceState));
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        scope.injectViews();
        eventManager.fire(new OnContentViewAvailableEvent());
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        scope.injectViews();
        eventManager.fire(new OnContentViewAvailableEvent());
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        scope.injectViews();
        eventManager.fire(new OnContentViewAvailableEvent());
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this;
    }

    @Override
    protected void onRestart() {
        scope.enter(this);
        super.onRestart();
        eventManager.fire(new OnRestartEvent());
    }

    @Override
    protected void onStart() {
        scope.enter(this);
        super.onStart();
        eventManager.fire(new OnStartEvent());
    }

    @Override
    protected void onResume() {
        scope.enter(this);
        super.onResume();
        eventManager.fire(new OnResumeEvent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventManager.fire(new OnPauseEvent());
    }

    @Override
    protected void onNewIntent( Intent intent ) {
        super.onNewIntent(intent);
        scope.enter(this);
        eventManager.fire(new OnNewIntentEvent());
    }

    @Override
    protected void onStop() {
        scope.enter(this);
        try {
            eventManager.fire(new OnStopEvent());
        } finally {
            scope.exit(this);
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        scope.enter(this);
        try {
            eventManager.fire(new OnDestroyEvent());
        } finally {
            eventManager.clear(this);
            scope.exit(this);
            scope.dispose(this);
            super.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent(currentConfig, newConfig));
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        eventManager.fire(new OnContentChangedEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        scope.enter(this);
        try {
            eventManager.fire(new OnActivityResultEvent(requestCode, resultCode, data));
        } finally {
            scope.exit(this);
        }
    }

    /**
     * @see roboguice.application.RoboApplication#getInjector()
     */
    @Override
    public Injector getInjector() {
        return ((RoboApplication) getApplication()).getInjector();
    }

}
