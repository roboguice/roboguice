package org.roboguice.astroboy.activity;

import org.roboguice.astroboy.R;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;

/**
 * Things you'll learn in this class: - How to inject Resources - How to use RoboAsyncTask to do
 * background tasks with injection - What it means to be a @Singleton
 */
public class FightForcesOfEvilActivity extends RoboFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_evil);
    }

}
