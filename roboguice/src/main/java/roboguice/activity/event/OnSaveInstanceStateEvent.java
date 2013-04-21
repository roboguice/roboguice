package roboguice.activity.event;

import android.os.Bundle;

public class OnSaveInstanceStateEvent {

        protected Bundle savedInstanceState;

        public OnSaveInstanceStateEvent(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        public Bundle getSavedInstanceState() {
            return savedInstanceState;
        }
    }
