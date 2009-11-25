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

import roboguice.application.GuiceApplication;
import roboguice.inject.ContextScope;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.MapActivity;
import com.google.inject.Injector;

/**
 * A {@link GuiceMapActivity} extends from {@link MapActivity} to provide dynamic injection of collaborators, using
 * Google Guice.<br />
 * 
 * @see GuiceActivity
 */
public abstract class GuiceMapActivity extends MapActivity {
    protected ContextScope scope;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getInjector().injectMembers(this);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        getInjector().injectMembers(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getInjector().injectMembers(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scope = getInjector().getInstance(ContextScope.class);
        scope.enter(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        scope.enter(this);
        super.onRestart();
    }

    @Override
    protected void onStart() {
        scope.enter(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        scope.enter(this);
        super.onResume();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        scope.exit(this);
    }

    /**
     * @see GuiceApplication#getInjector()
     */
    public Injector getInjector() {
        return ((GuiceApplication) getApplication()).getInjector();
    }

}
