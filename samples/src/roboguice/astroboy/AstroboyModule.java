package roboguice.astroboy;

import roboguice.astroboy.bean.Person;
import roboguice.astroboy.bean.PersonFromNameExtraProvider;
import roboguice.astroboy.service.TalkingThing;
import roboguice.astroboy.service.TalkingThingMockImpl;
import roboguice.config.AbstractAndroidModule;
import roboguice.inject.GuiceApplicationProvider;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class AstroboyModule extends AbstractAndroidModule {

    @Override
    protected void configure() {
        /*
         * Here is the place to write the configuration specific to your application, i.e. your own custom bindings.
         */
        bind(TalkingThing.class).to(TalkingThingMockImpl.class);

        // BUG it would be nice if this particular binding could be done automatically somehow
        bind(AstroboyApplication.class).toProvider(Key.get(new TypeLiteral<GuiceApplicationProvider<AstroboyApplication>>(){}));

        bind(Person.class).toProvider(PersonFromNameExtraProvider.class);

        // BUG need a better way to set default preferences context
        bindConstant().annotatedWith(Names.named("sharedPreferencesContext")).to("roboguice.astroboy");
    }
}
