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
// derived from RoboActivity.java

package roboguice.activity;

import com.google.inject.Injector;

import roboguice.application.RoboApplication;
import roboguice.inject.ContextScope;
import android.accounts.AccountAuthenticatorActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * A subclass of {@link AccountAuthenticatorActivity} that provides dependency injection
 * with RoboGuice.
 *
 * @author Marcus Better
 */
public class RoboAccountAuthenticatorActivity extends AccountAuthenticatorActivity
{
    protected ContextScope scope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Injector injector = getInjector();
        scope = injector.getInstance(ContextScope.class);
        scope.enter(this);
        injector.injectMembers(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        scope.injectViews();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        scope.injectViews();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        scope.injectViews();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this;
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
    protected void onPause() {
        super.onPause();
        scope.exit(this);
    }

    /**
     * @see roboguice.application.GuiceApplication#getInjector()
     */
    public Injector getInjector() {
        return ((RoboApplication) getApplication()).getInjector();
    }
}
