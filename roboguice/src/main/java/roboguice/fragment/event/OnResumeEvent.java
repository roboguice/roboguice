package roboguice.fragment.event;

/**
 * Class representing the event raised by RoboFragment.onResume()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnResumeEvent<T> extends AbstractFragmentEvent<T> {

    public OnResumeEvent(T fragment) {
        super(fragment);
    }
}
