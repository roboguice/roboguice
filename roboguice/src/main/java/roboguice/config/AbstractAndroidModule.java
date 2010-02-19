/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package roboguice.config;

import java.util.List;

import roboguice.inject.StaticTypeListener;

import com.google.inject.AbstractModule;

/**
 *
 * @author Mike Burton
 */
public abstract class AbstractAndroidModule extends AbstractModule {
    protected List<StaticTypeListener> listeners;

    @Override
    protected void requestStaticInjection(Class<?>... types) {
        super.requestStaticInjection(types);
        for (StaticTypeListener l : listeners) {
            l.requestStaticInjection(types);
        }
    }

    public void setStaticTypeListeners(List<StaticTypeListener> listeners) {
        this.listeners = listeners;
    }

}
