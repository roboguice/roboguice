package roboguice.astroboy.service;

import roboguice.application.GuiceApplication;
import roboguice.astroboy.AstroboyApplication;

import com.google.inject.Inject;

/**
 * Demonstrate how a dependency to the Application instance is resolved.
 */
public class SomeServiceMockImpl implements ISomeService {

    private final AstroboyApplication application;

    @Inject
    public SomeServiceMockImpl(GuiceApplication application) {
        /*
         * There is currently no way to directly get an AstroboyApplication injected, so we need to cast. See
         * AndroidModule for details.
         */
        this.application = (AstroboyApplication) application;
    }

    public String gimeAString() {
        return "Application says: " + application.sayWhoYouAre();
    }
}
