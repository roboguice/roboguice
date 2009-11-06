package roboguice.astroboy;

import roboguice.config.AndroidModule;
import android.app.Application;

import com.google.inject.name.Names;

public class AstroboyModule extends AndroidModule {

    public AstroboyModule(Application app) {
        super(app);
    }

    @Override
    protected void configure() {
        super.configure();
        // BUG need a better way to set default preferences context
        bindConstant().annotatedWith(Names.named("sharedPreferencesContext")).to("roboguice.astroboy");

    }

}
