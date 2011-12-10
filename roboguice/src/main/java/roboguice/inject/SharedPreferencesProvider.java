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
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.File;

/**
 * To override the name of the file, bindConstant().annotatedWith(SharedPreferencesName.class).to...
 *
 * @see {@link SharedPreferencesName}
 * @author Mike Burton
 * @author Pierre-Yves Ricau (py.ricau+roboguice@gmail.com)
 */
public class SharedPreferencesProvider implements Provider<SharedPreferences> {
    protected static final String ROBOGUICE_1_DEFAULT_FILENAME = "default.xml";
    
    protected String preferencesName;

    @Inject protected Application application;

    public SharedPreferencesProvider() {}

    @Inject
    public SharedPreferencesProvider(PreferencesNameHolder preferencesNameHolder) {
        preferencesName = preferencesNameHolder.value;
    }

    public SharedPreferencesProvider(String preferencesName) {
        this.preferencesName = preferencesName;
    }

    public SharedPreferences get() {
        
        // If the user specified an alternate name, OR if the old roboguice 1 file is around, use that
        if( preferencesName!=null )
            return application.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);

        if(  new File("shared_prefs/"+ROBOGUICE_1_DEFAULT_FILENAME).canRead() )
            return application.getSharedPreferences(ROBOGUICE_1_DEFAULT_FILENAME, Context.MODE_PRIVATE);

        return PreferenceManager.getDefaultSharedPreferences(application) ;
    }

    // http://code.google.com/p/google-guice/wiki/FrequentlyAskedQuestions => How can I inject optional parameters into a constructor?
    public static class PreferencesNameHolder {
        @Inject(optional = true)
        @SharedPreferencesName
        protected String value;
    }

}
