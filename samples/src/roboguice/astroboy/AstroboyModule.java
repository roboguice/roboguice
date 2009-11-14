package roboguice.astroboy;

import roboguice.astroboy.service.SomeService;
import roboguice.astroboy.service.SomeServiceMockImpl;
import roboguice.inject.GuiceApplicationProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class AstroboyModule extends AbstractModule {

    @Override
    protected void configure() {
        /*
         * Here is the place to write the configuration specific to your application, i.e. your own custom bindings.
         */
        bind(SomeService.class).to(SomeServiceMockImpl.class);

        // BUG it would be nice if this particular binding could be done automatically somehow
        bind(AstroboyApplication.class).toProvider(Key.get(new TypeLiteral<GuiceApplicationProvider<AstroboyApplication>>(){}));


        // BUG need a better way to set default preferences context
        bindConstant().annotatedWith(Names.named("sharedPreferencesContext")).to("roboguice.astroboy");
    }
}
