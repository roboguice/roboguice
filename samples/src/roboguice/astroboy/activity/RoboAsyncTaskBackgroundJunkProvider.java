package roboguice.astroboy.activity;


import com.google.inject.Provider;

/**
 * @author John Ericksen
 */
public class RoboAsyncTaskBackgroundJunkProvider implements Provider<RoboAsyncTaskBackgroundJunk> {
    public RoboAsyncTaskBackgroundJunk get() {
        return new RoboAsyncTaskBackgroundJunk();
    }
}
