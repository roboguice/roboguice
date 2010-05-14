package roboguice.test;

import android.app.Activity;
import android.test.ActivityUnitTestCase;

/**
 * A new instance of your activity is constructed for each test method in your RoboActivityUnitTestCase,
 * so this is a great way to run many tests on an Activity in isolation. Note, however, that Activities
 * instantiated by RoboActivityUnitTestCase won't be running in a "normal" system, it may not be
 * appropriate for every kind of test case. See the ActivityUnitTestCase docs for specifics.
 *
 * Here's an example to get you started.
 *
 * <code>
 * public class MyActivityUnitTest extends ActivityUnitTestCase<MyActivity> {
 *
 *     protected Intent intent = new Intent(Intent.ACTION_MAIN);
 *
 *     public MyActivityTest() {
 *         super(MyActivity.class);
 *     }
 *
 *     // Make sure you use one of the @*Test annotations AND begin
 *     // your testcase's name with "test"
 *     \@MediumTest
 *     public void test01() {
 *         setApplication( new MyApplication( getInstrumentation().getTargetContext() ) );
 *         final Activity activity = startActivity(intent, null, null);
 *
 *         // Test some things
 *         assertNotNull(activity);
 *         assertEquals( ((TextView)activity.findViewById(R.id.hello)).getText(), "Hello World, Lop!");
 *     }
 * }
 * </code>
 *
 * Note that, as for RoboUnitTestCase, you'll need to add a constructor like the following to your
 * Application class:
 *
 * <code>
 * public MyApplication( Context context ) {
 *      super();
 *      attachBaseContext(context);
 * }
 * </code>
 *
 *
 */
public class RoboActivityUnitTestCase<ActivityType extends Activity> extends ActivityUnitTestCase<ActivityType> {

    public RoboActivityUnitTestCase(Class<ActivityType> activityClass) {
        super(activityClass);
    }
}
