package roboguice.astroboy;

import roboguice.application.GuiceApplication;

import com.google.inject.Binder;
import com.google.inject.name.Names;


public class AstroboyApplication extends GuiceApplication {

    @Override
    public void configure( Binder b ) {
        super.configure(b);

        // BUG need a better way to set default preferences context
        b.bindConstant().annotatedWith(Names.named("sharedPreferencesContext")).to("roboguice.astroboy");
    }
}
