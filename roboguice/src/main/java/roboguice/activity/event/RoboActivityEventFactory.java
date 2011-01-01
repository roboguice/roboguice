package roboguice.activity.event;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.inject.Singleton;

/**
 * Factory for the @ContextObserves pre-defined RoboActivy Events.
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
    public OnContentChangedEvent buildOnContentChangedEvent() {
        return ON_CONTENT_CHANGED_INSTANCE;
    }

    public OnCreateEvent buildOnCreateEvent(Bundle savedInstanceState){
        return new OnCreateEvent(savedInstanceState);
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

    public OnActivityResultEvent buildOnActivityResultEvent(int requestCode, int resultCode, Intent data) {
        return new OnActivityResultEvent(requestCode, resultCode, data);
    }
}
