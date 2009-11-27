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
package roboguice.inject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class SharedPreferencesProvider implements Provider<SharedPreferences> {

    protected static final String DEFAULT = "default";

    @Inject(optional = true)
    @Named("sharedPreferencesContext")
    protected String              contextName;

    @Inject
    protected Application         application;

    protected SharedPreferences   preferences;

    public SharedPreferences get() {
        if (preferences == null) {
            synchronized (this) {
                if (preferences == null) {
                    preferences = application.getSharedPreferences(contextName != null ? contextName : DEFAULT,
                            Context.MODE_PRIVATE);
                    application=null;
                }
            }
        }
        return preferences;
    }
}
