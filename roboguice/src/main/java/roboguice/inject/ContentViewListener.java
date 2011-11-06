package roboguice.inject;

import roboguice.activity.event.OnCreateEvent;
import roboguice.event.Observes;

import android.app.Activity;
import android.content.Context;

import com.google.inject.Inject;

@SuppressWarnings("UnusedParameters")
@ContextSingleton
public class ContentViewListener {
    @Inject protected Activity activity;

    public void optionallySetContentView( @Observes OnCreateEvent ignored ) {
        Class<?> c = activity.getClass();
        while( c != Context.class ) {
            final ContentView annotation = c.getAnnotation(ContentView.class);
            if( annotation!=null ) {
                activity.setContentView(annotation.value());
                return;
            }
            c = c.getSuperclass();
        }
    }
}
