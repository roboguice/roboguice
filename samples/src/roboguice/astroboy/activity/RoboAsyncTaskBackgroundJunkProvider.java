package roboguice.astroboy.activity;


import com.google.inject.Provider;

/**
 * @author John Ericksen
 */
public class RoboAsyncTaskBackgroundJunkProvider implements Provider<ExampleBackgroundTask> {
    public ExampleBackgroundTask get() {
        return new ExampleBackgroundTask();
    }
}
