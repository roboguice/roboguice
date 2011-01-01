package roboguice.activity.event;

import android.view.KeyEvent;

public class OnKeyDownEvent {
        private int keyCode;
        private KeyEvent event;

        public OnKeyDownEvent(int keyCode, KeyEvent event) {
            this.keyCode = keyCode;
            this.event = event;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public KeyEvent getEvent() {
            return event;
        }
    }