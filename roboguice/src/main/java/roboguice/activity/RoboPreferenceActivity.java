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

import roboguice.application.RoboApplication;
import roboguice.event.EventManager;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectPreference;
import roboguice.inject.InjectorProvider;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * A {@link RoboPreferenceActivity} extends from {@link PreferenceActivity} to provide
 * dynamic injection of collaborators, using Google Guice.<br />
 * 
 * @see RoboActivity
 * 
 * @author Toly Pochkin
 * @author Rodrigo Damazio
 */
public abstract class RoboPreferenceActivity extends PreferenceActivity implements InjectorProvider {
    
    @Inject protected EventManager eventManager;

    protected ContextScope scope;

    /** {@inheritDoc } */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Injector injector = getInjector();
        scope = injector.getInstance(ContextScope.class);
        scope.enter(this);

        // Injecting the preferences requires that they've been loaded, so load them
        onCreatePreferences();

        // Only then inject everything
        injector.injectMembers(this);
    }

    /**
     * Override this method to specify how your preferences will be loaded.
     * This is called before injecting the preference member fields, and will
     * usually contain a call to {@link #addPreferencesFromResource}. This
     * method must load or create all preferences which will be injected by
     * {@link InjectPreference} annotations.
     */
    protected void onCreatePreferences() {
        // Do nothing by default
    }

    /** {@inheritDoc } */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Built-in preference views don't need injection
        if (scope != null) scope.injectViews();
    }

    /** {@inheritDoc } */
    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);

        // Built-in preference views don't need injection
        if (scope != null) scope.injectViews();
    }

    /** {@inheritDoc } */
    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        // Built-in preference views don't need injection
        if (scope != null) scope.injectViews();
    }

    /** {@inheritDoc } */
    @Override
    protected void onRestart() {
        scope.enter(this);
        super.onRestart();
    }

    /** {@inheritDoc } */
    @Override
    protected void onStart() {
        scope.enter(this);
        super.onStart();
    }

    /** {@inheritDoc } */
    @Override
    protected void onResume() {
        scope.enter(this);
        super.onResume();
    }

    /** {@inheritDoc } */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        scope.exit(this);
    }

    @Override
    protected void onNewIntent( Intent intent ) {
        super.onNewIntent(intent);
        scope.enter(this);
    }

    /**
     * @see roboguice.application.RoboApplication#getInjector()
     */
    public Injector getInjector() {
        return ((RoboApplication) getApplication()).getInjector();
    }

}
