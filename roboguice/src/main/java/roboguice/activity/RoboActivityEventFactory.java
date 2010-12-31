package roboguice.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.inject.Singleton;

/**
 * Defined @ContextObserves Events called by the RoboActivity Class
 *
 * @author John Ericksen
 */
@Singleton
public class RoboActivityEventFactory {

    //Non-parameter static class instance definitions;
    private static final OnRestartEvent ON_RESTART_INSTANCE = new OnRestartEvent();
    private static final OnStartEvent ON_START_INSTANCE = new OnStartEvent();
    private static final OnResumeEvent ON_RESUME_INSTANCE = new OnResumeEvent();
    private static final OnPauseEvent ON_PAUSE_INSTANCE = new OnPauseEvent();
    private static final OnNewIntentEvent ON_NEW_INTENT_INSTANCE = new OnNewIntentEvent();
    private static final OnStopEvent ON_STOP_INSTANCE = new OnStopEvent();
    private static final OnDestroyEvent ON_DESTROY_INSTANCE = new OnDestroyEvent();
    private static final OnContentChangedEvent ON_CONTENT_CHANGED_INSTANCE = new OnContentChangedEvent();

    //Event class definitions
    public static class OnRestartEvent {}
    public static class OnStartEvent {}
    public static class OnResumeEvent {}
    public static class OnPauseEvent {}
    public static class OnNewIntentEvent {}
    public static class OnStopEvent {}
    public static class OnContentChangedEvent {}
    public static class OnDestroyEvent {}

    public class OnCreateEvent{
        private Bundle savedInstanceState;

        public OnCreateEvent(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        public Bundle getSavedInstanceState() {
            return savedInstanceState;
        }
    }

    public class OnConfigurationChangedEvent {

        private Configuration config;

        public OnConfigurationChangedEvent(Configuration config) {
            this.config = config;
        }

        public Configuration getConfig() {
            return config;
        }
    }

    public class OnKeyDownEvent {
        private int keyCode;
        private KeyEvent event;

        private OnKeyDownEvent(int keyCode, KeyEvent event) {
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

    public class OnKeyUpEvent {
        private int keyCode;
        private KeyEvent event;

        private OnKeyUpEvent(int keyCode, KeyEvent event) {
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

    public class OnActivityResultEvent {
        private int requestCode;
        private int resultCode;
        private Intent data;

        private OnActivityResultEvent(int requestCode, int resultCode, Intent data) {
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

    public OnCreateEvent buildOnCreateEvent(Bundle savedInstanceState){
        return new OnCreateEvent(savedInstanceState);
    }

    public OnRestartEvent buildOnRestartEvent() {
        return ON_RESTART_INSTANCE;
    }

    public OnStartEvent buildOnStartEvent() {
        return ON_START_INSTANCE;
    }

    public OnResumeEvent buildOnResumeEvent() {
        return ON_RESUME_INSTANCE;
    }

    public OnPauseEvent buildOnPauseEvent() {
        return ON_PAUSE_INSTANCE;
    }

    public OnNewIntentEvent buildOnNewIntentEvent() {
        return ON_NEW_INTENT_INSTANCE;
    }

    public OnStopEvent buildOnStopEvent() {
        return ON_STOP_INSTANCE;
    }

    public OnDestroyEvent buildOnDestroyEvent() {
        return ON_DESTROY_INSTANCE;
    }

    public OnConfigurationChangedEvent buildOnConfigurationChangedEvent(Configuration newConfig) {
        return new OnConfigurationChangedEvent(newConfig);
    }

    public OnKeyDownEvent buildOnKeyDownEvent(int keyCode, KeyEvent event) {
        return new OnKeyDownEvent(keyCode, event);
    }

    public OnKeyUpEvent buildOnKeyUpEvent(int keyCode, KeyEvent event) {
        return new OnKeyUpEvent(keyCode, event);
    }

    public OnContentChangedEvent buildOnContentChangedEvent() {
        return ON_CONTENT_CHANGED_INSTANCE;
    }

    public OnActivityResultEvent buildOnActivityResultEvent(int requestCode, int resultCode, Intent data) {
        return new OnActivityResultEvent(requestCode, resultCode, data);
    }
}
