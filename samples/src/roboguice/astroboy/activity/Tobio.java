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
package roboguice.astroboy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

/**
 * This activity was created to execute {@link DoctorTenma} activity with extra
 * values.
 *
 * Tobio doesn't use injection itself, so there's no particular need to extend
 * GuiceActivity, but we could if we wanted to.
 *
 */
public class Tobio extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, DoctorTenma.class);
        intent.putExtra("nullExtra", (Serializable) null);
        intent.putExtra("nameExtra", "Atom");
        intent.putExtra("ageExtra", 3000L);
        intent.putExtra("timestampExtra", 1000L);
        intent.putExtra("timestampTwiceExtra", 1000);

        startActivity(intent);
        finish();
    }
}
