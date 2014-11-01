package roboguice.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import com.google.inject.Inject;
import roboguice.RoboGuice;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import roboguice.event.EventManager;
import roboguice.fragment.event.OnActivityCreatedEvent;
import roboguice.fragment.event.OnAttachEvent;
import roboguice.fragment.event.OnConfigurationChangedEvent;
import roboguice.fragment.event.OnCreateEvent;
import roboguice.fragment.event.OnDestroyEvent;
import roboguice.fragment.event.OnDetachEvent;
import roboguice.fragment.event.OnPauseEvent;
import roboguice.fragment.event.OnResumeEvent;
import roboguice.fragment.event.OnStartEvent;
import roboguice.fragment.event.OnStopEvent;
import roboguice.fragment.event.OnViewCreatedEvent;

/**
 * Provides an injected {@link ListFragment} based on support library v4.
 * A RoboListFragment will see all its members and views injected after {@link #onViewCreated(View, Bundle)}.
 * @author Michael Burton
 */
public abstract class RoboListFragment extends ListFragment {
    @Inject
    protected EventManager eventManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventManager.fire(new OnCreateEvent(this, savedInstanceState));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
        eventManager.fire(new OnViewCreatedEvent(this, view, savedInstanceState));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent(this, currentConfig, newConfig));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventManager.fire(new OnActivityCreatedEvent(this, savedInstanceState));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        RoboGuice.getInjector(getActivity()).injectMembersWithoutViews(this);
        eventManager.fire(new OnAttachEvent(this, activity));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventManager.fire(new OnDetachEvent(this));
    }

    @Override
    public void onDestroy() {
        try {
            eventManager.fire(new OnDestroyEvent(this));
        } finally {
            super.onDestroy();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        eventManager.fire(new OnStartEvent(this));
    }

    @Override
    public void onStop() {
        try {
            eventManager.fire(new OnStopEvent(this));
        } finally {
            super.onStop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        eventManager.fire(new OnResumeEvent(this));
    }

    @Override
    public void onPause() {
        super.onPause();
        eventManager.fire(new OnPauseEvent(this));
    }
}
