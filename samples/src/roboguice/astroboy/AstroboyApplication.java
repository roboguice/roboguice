package roboguice.astroboy;

import java.util.List;

import roboguice.application.GuiceApplication;
import roboguice.astroboy.service.SomeServiceMockImpl;

import com.google.inject.Module;

public class AstroboyApplication extends GuiceApplication {

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new AstroboyModule());
    }

    /**
     * Used by {@link SomeServiceMockImpl}
     */
    public String sayWhoYouAre() {
        return "I am " + toString();
    }
}
