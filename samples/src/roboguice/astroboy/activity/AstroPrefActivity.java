/*
 * Copyright 2010 Rodrigo Damazio
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
package roboguice.astroboy.activity;

import roboguice.activity.RoboPreferenceActivity;
import roboguice.astroboy.R;
import roboguice.astroboy.service.PreferenceChangeEventToastListener;
import roboguice.inject.InjectPreference;

import android.os.Bundle;
import android.preference.CheckBoxPreference;

import com.google.inject.Inject;

/**
 * @author Rodrigo Damazio
 */
public class AstroPrefActivity extends RoboPreferenceActivity {

    @InjectPreference("bool_pref") protected CheckBoxPreference boolPref;
    @Inject protected PreferenceChangeEventToastListener preferenceChangeEventListener;

    @Override
    protected void onCreatePreferences() {
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //example of binding a event listener through a preference change listener
        boolPref.setOnPreferenceChangeListener(preferenceChangeEventListener);
    }
}
