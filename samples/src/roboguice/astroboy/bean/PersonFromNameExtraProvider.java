package roboguice.astroboy.bean;

import roboguice.inject.ContextScoped;
import roboguice.inject.InjectExtra;

import com.google.inject.Provider;

@ContextScoped
public class PersonFromNameExtraProvider implements Provider<Person> {

    @InjectExtra("nameExtra")
    protected String nameExtra;

    @Override
    public Person get() {
        return new Person(nameExtra);
    }

}
