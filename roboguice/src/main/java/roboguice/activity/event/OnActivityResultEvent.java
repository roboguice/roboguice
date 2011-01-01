package roboguice.activity.event;

import android.content.Intent;

public class OnActivityResultEvent {
        private int requestCode;
        private int resultCode;
        private Intent data;

        public OnActivityResultEvent(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public int getResultCode() {
            return resultCode;
        }

        public Intent getData() {
            return data;
        }
    }