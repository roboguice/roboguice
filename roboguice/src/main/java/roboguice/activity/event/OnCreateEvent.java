package roboguice.activity.event;

import android.os.Bundle;

/**
 * Class representing the event raised by RoboActivity.onCreate()
 *
 * You may also be interested in roboguice.activity.event.OnContentViewAvailableEvent
 * 
 * @author Adam Tybor
 * @author John Ericksen
 */
public class OnCreateEvent{

        protected Bundle savedInstanceState;

        public OnCreateEvent(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        public Bundle getSavedInstanceState() {
            return savedInstanceState;
        }
    }
