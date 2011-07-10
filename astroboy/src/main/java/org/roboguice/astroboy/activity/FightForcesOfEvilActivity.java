package org.roboguice.astroboy.activity;

import org.roboguice.astroboy.activity.AstroboyRemoteControl;
import roboguice.activity.RoboActivity;

import android.os.Bundle;

import javax.inject.Inject;

public class FightForcesOfEvilActivity extends RoboActivity {

    // AstroboyRemoteControl is annotated as @ContextScoped, so the instance
    // we get in FightForcesOfEvilActivity will be a different instance than
    // the one we got in AstroboyMasterConsole
    @Inject AstroboyRemoteControl remoteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        remoteControl.fightForcesOfEvil();
    }
}
