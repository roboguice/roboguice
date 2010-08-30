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

import android.app.ActivityGroup;
import roboguice.application.RoboApplication;
import roboguice.inject.ContextScope;
import roboguice.inject.InjectorProvider;

import com.google.inject.Injector;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.content.Intent;

/**
 * A {@link RoboActivityGroup} extends from {@link ActivityGroup} to provide
 * dynamic injection of collaborators, using Google Guice.<br />
 * 
 * @see RoboActivity
 * 
 * @author Toly Pochkin
 */
public class RoboActivityGroup extends ActivityGroup implements InjectorProvider {
    protected ContextScope scope;

    /** {@inheritDoc } */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Injector injector = getInjector();
        scope = injector.getInstance(ContextScope.class);
        scope.enter(this);
        injector.injectMembers(this);
        super.onCreate(savedInstanceState);
    }

    /** {@inheritDoc } */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        scope.injectViews();
    }

    /** {@inheritDoc } */
    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        scope.injectViews();
    }

    /** {@inheritDoc } */
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        scope.injectViews();
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

    /** {@inheritDoc } */
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
