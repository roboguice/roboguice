package roboguice.fragment.event;

/**
 * Class representing the event raised by RoboFragment.onRestart()
 *
 * @author Adam Tybor
 * @author John Ericksen
 * @author Cherry Development
 */
public class OnRestartEvent<T> extends AbstractFragmentEvent<T> {

    public OnRestartEvent(T fragment) {
        super(fragment);
    }
}
