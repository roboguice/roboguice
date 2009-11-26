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
package roboguice.astroboy.service;

import roboguice.astroboy.AstroboyApplication;
import roboguice.astroboy.R;
import roboguice.inject.StringResourceFactory;

import com.google.inject.Inject;

/**
 * Demonstrate how a dependency to the Application instance is resolved.
 */
public class TalkingThingMockImpl implements TalkingThing {

    @Inject protected AstroboyApplication application;

    @Inject
    protected StringResourceFactory stringRF;

    public String talk() {
        return stringRF.get(R.string.hello) + ", my master is " + application.getClass().getName();
    }
}
