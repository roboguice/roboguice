package roboguice.activity.event;

import android.os.Bundle;

public class OnCreateEvent{
        private Bundle savedInstanceState;

        public OnCreateEvent(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        public Bundle getSavedInstanceState() {
            return savedInstanceState;
        }
    }