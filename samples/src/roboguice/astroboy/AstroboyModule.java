package roboguice.astroboy;

import roboguice.astroboy.service.ISomeService;
import roboguice.astroboy.service.SomeServiceMockImpl;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class AstroboyModule extends AbstractModule {

    @Override
    protected void configure() {
        /*
         * Here is the place to write the configuration specific to your application, i.e. your own custom bindings.
         */
        bind(ISomeService.class).to(SomeServiceMockImpl.class);

        // BUG need a better way to set default preferences context
        bindConstant().annotatedWith(Names.named("sharedPreferencesContext")).to("roboguice.astroboy");
    }
}
