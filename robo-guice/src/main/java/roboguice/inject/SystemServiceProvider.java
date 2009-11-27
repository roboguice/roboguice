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
package roboguice.inject;

import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SystemServiceProvider<T> implements Provider<T>{

    @Inject
    protected Application application;

    protected String            serviceName;

    protected T                 service;

    public SystemServiceProvider(String serviceName) {
        this.serviceName = serviceName;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        if (service == null) {
            synchronized (this) {
                if (service == null) {
                    service = (T) application.getSystemService(serviceName);
                    application = null;
                }
            }
        }
        return service;
    }
}
