package roboguice.fragment.provided;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.inject.AbstractModule;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.event.EventManager;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

//TODO : this could be made easier when switching to Robolectric 2.3
@RunWith(RobolectricTestRunner.class)
public class FragmentEventInjectionTest {

    private EventManager eventManager = EasyMock.createMock(EventManager.class);

    @Before
    public void setup() {
        RoboGuice.overrideApplicationInjector(Robolectric.application, new EventTestModule());
    }

    //http://blog.nikhaldimann.com/2013/10/10/robolectric-2-2-some-pages-from-the-missing-manual/
    //http://stackoverflow.com/questions/11333354/how-can-i-test-fragments-with-robolectric
    @TargetApi(HONEYCOMB)
    @Test
    public void testShouldReceiveEvents() {
        //GIVEN
        eventManager.registerObserver((Class) EasyMock.anyObject(), (roboguice.event.EventListener<?>) EasyMock.anyObject());
        //try to keep FQN to see what belongs to activity and fragment
        eventManager.fire(EasyMock.isA(roboguice.context.event.OnCreateEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.activity.event.OnContentChangedEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnAttachEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnCreateEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnActivityCreatedEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnViewCreatedEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.context.event.OnStartEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnStartEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.activity.event.OnResumeEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnResumeEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.activity.event.OnPauseEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnPauseEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.activity.event.OnStopEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnStopEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.context.event.OnDestroyEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnDestroyEvent.class));
        eventManager.fire(EasyMock.isA(roboguice.fragment.event.OnDetachEvent.class));
        eventManager.destroy();

        EasyMock.replay(eventManager);

        //WHEN
        final ActivityController<ActivityEvent> activityController = Robolectric.buildActivity(ActivityEvent.class);

        final ActivityEvent activity = activityController.get();
        activityController.create();
        activityController.postCreate(null);
        activityController.start();
        activityController.resume();
        activityController.postResume();
        activityController.visible();
        startFragment(activity, activity.fragmentRef, ActivityEvent.CONTAINER_ID);
        activityController.pause();
        activityController.stop();
        activityController.destroy();

        //THEN
        EasyMock.verify(eventManager);
    }

    public static class ActivityEvent extends RoboActivity {
        public static final int CONTAINER_ID = 1;
        FragmentEvent fragmentRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(CONTAINER_ID);

            setContentView(view);

            fragmentRef = new FragmentEvent();
        }

        public static class FragmentEvent extends RoboFragment {

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                return new View(inflater.getContext());
            }
        }
    }

    // http://stackoverflow.com/questions/11333354/how-can-i-test-fragments-with-robolectric
    protected static void startFragment(Activity activity, Fragment fragment, int containerId) {
        final FragmentManager fragmentManager = activity.getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerId, fragment, "tag");
        fragmentTransaction.commit();
    }

    private class EventTestModule extends AbstractModule {
        @Override protected void configure() {
            bind(EventManager.class).toInstance(eventManager);
        }
    }
}
