package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesProvider implements Provider<SharedPreferences> {
    protected static final String DEFAULT = "default";

    @Inject(optional=true) @Named("sharedPreferencesContext") protected String context;
    @Inject protected Provider<Context> contextProvider;


    public SharedPreferences get() {
        return contextProvider.get().getSharedPreferences(context!=null ? context : DEFAULT, Context.MODE_PRIVATE);
    }
}