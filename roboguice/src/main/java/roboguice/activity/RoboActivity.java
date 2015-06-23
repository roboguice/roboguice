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
package roboguice.activity;

import android.app.Activity;
import android.os.Bundle;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.internal.util.Stopwatch;
import java.util.HashMap;
import java.util.Map;
import roboguice.RoboGuice;
import roboguice.util.RoboContext;

/**
 * A {@link RoboActivity} extends from {@link Activity} to provide dynamic
 * injection of collaborators, using Google Guice.<br />
 * <br />
 * Your own activities that usually extend from {@link Activity} should now
 * extend from {@link RoboActivity}.<br />
 * <br />
 * If your activities extend from subclasses of {@link Activity} provided by the
 * Android SDK, we provided Guice versions as well for the most used : see
 * {@link RoboExpandableListActivity}, {@link RoboListActivity}, and other
 * classes located in package <strong>roboguice.activity</strong>.<br />
 * <br />
 * If we didn't provide what you need, you have two options : either post an
 * issue on <a href="http://code.google.com/p/roboguice/issues/list">the bug
 * tracker</a>, or implement it yourself. Have a look at the source code of this
 * class ({@link RoboActivity}), you won't have to write that much changes. And
 * of course, you are welcome to contribute and send your implementations to the
 * RoboGuice project.<br />
 * <br />
 * Please be aware that collaborators are not injected into this until you call
 * {@link #setContentView(int)} (calling any of the overloads of this methods
 * will work).<br />
 * <br />
 *
 * @author Mike Burton
 */
public class RoboActivity extends Activity implements RoboContext {
    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();

    private Stopwatch stopwatch;
    protected Injector injector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stopwatch = new Stopwatch();
        super.onCreate(savedInstanceState);
        stopwatch.resetAndLog("RoboActivity super onCreate");
        injector = RoboGuice.getInjector(this);
        stopwatch.resetAndLog("RoboActivity creation of injector");
        stopwatch.resetAndLog("RoboActivity creation of eventmanager");
        injector.injectMembers(this);
        stopwatch.resetAndLog("RoboActivity inject members without views");
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {
        return scopedObjects;
    }
}
