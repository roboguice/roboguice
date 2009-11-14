package roboguice.astroboy;

import java.util.List;

import roboguice.application.GuiceApplication;

import com.google.inject.Module;

public class AstroboyApplication extends GuiceApplication {

    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new AstroboyModule());
    }

}
