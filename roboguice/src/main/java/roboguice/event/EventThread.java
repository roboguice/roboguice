package roboguice.event;

/**
 * Observes annotation parameter specifying which thread the annotated method will be called from.  Use this
 * parameter to ensure that event manager calls with special requirements are executed properly:
 *
 * Current:
 *  Default execution technique.  The same thread that fires the event calls to the observer.
 *
 * UI:
 *  When using events from background threads, use this parameter to
 *
 * NEW:
 *  When observing long running events, especially on the UI thread, use this observer technique to execute the
 *  observing method on a new thread.
 *
 * @author John Ericksen
 */
public enum EventThread {
    CURRENT,
    UI,
    BACKGROUND
}
