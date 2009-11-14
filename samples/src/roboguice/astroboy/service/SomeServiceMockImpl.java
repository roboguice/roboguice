package roboguice.astroboy.service;

import roboguice.astroboy.AstroboyApplication;

import com.google.inject.Inject;

/**
 * Demonstrate how a dependency to the Application instance is resolved.
 */
public class SomeServiceMockImpl implements SomeService {

    @Inject protected AstroboyApplication application;

    public String gimmeAString() {
        return "Application says: " + application.sayWhoYouAre();
    }
}
