package roboguice.astroboy.service;

import android.content.Context;
import android.widget.Toast;
import com.google.inject.Inject;
import roboguice.event.Observes;

/**
 *@author John Ericksen
 */
public class ToastContextObserverService {
    @Inject Context context;

    public void toast(@Observes ToastEvent event){
        Toast.makeText(context, event.getMessage(), Toast.LENGTH_LONG).show();
    }

    public ToastEvent buildToastEvent(String message) {
        return new ToastEvent(message);
    }

    public static class ToastEvent{
        String message;

        public ToastEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
