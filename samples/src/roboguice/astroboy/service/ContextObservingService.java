package roboguice.astroboy.service;

import roboguice.event.ContextObserver;
import roboguice.event.OnCreateEvent;
import roboguice.util.Ln;

public class ContextObservingService {

    @ContextObserver
    public void onCreate(OnCreateEvent event) {
        Ln.v("onCreate");
    }

}
