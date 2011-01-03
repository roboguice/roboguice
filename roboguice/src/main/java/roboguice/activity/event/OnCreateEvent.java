package roboguice.activity.event;

import android.os.Bundle;

/**
 * Class representing the event raised by RoboActivity.onCreate()
 *
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnCreateEvent{

        protected final Bundle savedInstanceState;

        public OnCreateEvent(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        public Bundle getSavedInstanceState() {
            return savedInstanceState;
        }
    }