package roboguice.astroboy;

import roboguice.application.GuiceApplication;

import com.google.inject.Module;

public class AstroboyApplication extends GuiceApplication {

    @Override
    public Module getModule() {
        return new AstroboyModule(this);
    }
}
