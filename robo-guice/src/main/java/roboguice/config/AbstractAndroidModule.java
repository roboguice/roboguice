package roboguice.config;

import java.util.List;

import roboguice.inject.StaticTypeListener;

import com.google.inject.AbstractModule;

public abstract class AbstractAndroidModule extends AbstractModule {
    protected List<StaticTypeListener> listeners;

    @Override
    protected void requestStaticInjection(Class<?>... types) {
        super.requestStaticInjection(types);
        for( StaticTypeListener l : listeners )
            l.requestStaticInjection(types);
    }

    public void setStaticTypeListeners(List<StaticTypeListener> listeners) {
        this.listeners = listeners;
    }

}
