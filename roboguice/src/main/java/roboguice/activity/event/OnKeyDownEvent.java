package roboguice.activity.event;

import android.view.KeyEvent;

/**
 * Class representing the event raised by RoboActivity.onKeyDown()
 *
 * @author Adam Tabor
 * @author John Ericksen
 */
public class OnKeyDownEvent {
    
        protected final int keyCode;
        protected final KeyEvent event;

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