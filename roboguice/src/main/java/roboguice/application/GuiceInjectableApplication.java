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
package roboguice.application;

import roboguice.inject.ContextScope;

import com.google.inject.Injector;

/**
 * Like GuiceApplication, but allows you to inject resource into the application
 * itself.
 *
 * Introduces additional cost to the app's startup time, so may not be desirable
 * in all cases.
 *
 * @author mike
 *
 */
public class GuiceInjectableApplication extends GuiceApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        final Injector i = getInjector();
        final ContextScope scope = i.getInstance(ContextScope.class);
        scope.enter(this);
        i.injectMembers(this);
    }

}
