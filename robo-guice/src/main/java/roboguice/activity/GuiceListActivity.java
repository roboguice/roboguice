package roboguice.activity;

import roboguice.application.GuiceApplication;
import roboguice.inject.ContextScope;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.google.inject.Injector;

/**
 * A {@link GuiceListActivity} extends from {@link ListActivity} to provide dynamic injection of collaborators, using
 * Google Guice.<br />
 * 
 * @see GuiceActivity
 */
public class GuiceListActivity extends ListActivity {
    protected ContextScope scope;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getInjector().injectMembers(this);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        getInjector().injectMembers(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getInjector().injectMembers(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        scope = getInjector().getInstance(ContextScope.class);
        scope.enter(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        scope.enter(this);
        super.onRestart();
    }

    @Override
    protected void onStart() {
        scope.enter(this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        scope.enter(this);
        super.onResume();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        scope.exit(this);
    }

    /**
     * @see GuiceApplication#getInjector()
     */
    public Injector getInjector() {
        return ((GuiceApplication) getApplication()).getInjector();
    }

}
