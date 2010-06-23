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
import roboguice.inject.InjectPreference;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;

/**
 * @author Rodrigo Damazio
 */
public class AstroPrefActivity extends RoboPreferenceActivity {

  @InjectPreference("bool_pref")
  CheckBoxPreference boolPref;
  @InjectPreference("text_pref")
  EditTextPreference textPref;

  @Override
  protected void onCreatePreferences() {
    addPreferencesFromResource(R.xml.preference);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    boolPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference arg0, Object arg1) {
            Toast.makeText(AstroPrefActivity.this, "Meep.", Toast.LENGTH_LONG).show();
            return true;
        }
    });
  }
}
