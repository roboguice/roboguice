package roboguice.astroboy.service;

import roboguice.astroboy.AstroboyApplication;

import com.google.inject.Inject;

/**
 * Demonstrate how a dependency to the Application instance is resolved.
 */
public class TalkingThingMockImpl implements TalkingThing {

    @Inject protected AstroboyApplication application;

    public String talk() {
        return "My master is " + application.getClass().getName();
    }
}
