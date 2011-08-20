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

import android.content.Context;

import com.google.inject.Provider;

public class ContextScopedSystemServiceProvider<T> implements Provider<T> {

    protected Provider<Context> contextProvider;
    protected String serviceName;

    public ContextScopedSystemServiceProvider(Provider<Context> contextProvider, String serviceName) {
        this.contextProvider = contextProvider;
        this.serviceName = serviceName;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) contextProvider.get().getSystemService(serviceName);
    }

}
