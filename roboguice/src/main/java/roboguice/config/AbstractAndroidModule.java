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
import java.lang.annotation.Annotation;
import roboguice.inject.SharedPreferencesName;

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


    /**
     * Setup shared preferences storage name.
     * @param   name    Name of the shared preferences storage.
     */
    protected void setSharedPreferencesName(String name) {

        setParameter(SharedPreferencesName.class, name);
    }


    /**
     * Bind object's parameter to the specific value.
     * Here is an example:
     * Let's say we have class FoodServiceImpl and we want to configure server URL
     * that this class uses. Our first step should be creating annotation FoodServiceURL:
     * <code>
     *      @Retention(RUNTIME)
     *      @Target( { ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
     *      @BindingAnnotation
     *      public @interface FoodServiceURL {
     *      }
     * </code>
     * Then we add the constructor to our FoodServiceImpl class, such as:
     * <code>
     *      @Inject
     *      public FoodServiceImpl(@FoodServiceURL String serverURL) {
     *          this.serverURL = serverAddr;
     *      }
     * </code>
     * Now, to set up the server URL for food service, you might want to call
     * setParameter method from your configure() method in your module:
     * <code>
     *      ...
     *      bind(FoodService.class).to(FoodServiceImpl.class);
     *      setParameter(FoodServiceURL.class, "http://mynewcompany.com/myfoodservice");
     *      ...
     * </code>
     * @param   clazz   Parameter annotated with clazz.
     * @param   value   Value to set.
     */
	protected void setParameter(Class<? extends Annotation> clazz, String value) {

		bindConstant()
				.annotatedWith(clazz)
				.to(value);
	}


    /**
     * @see AbstractAndroidModule#setParameter(java.lang.Class, java.lang.String)
     */
	protected void setParameter(Class<? extends Annotation> clazz, int value) {

		bindConstant()
				.annotatedWith(clazz)
				.to(value);
	}


    /**
     * @see AbstractAndroidModule#setParameter(java.lang.Class, java.lang.String)
     */
	protected void setParameter(Class<? extends Annotation> clazz, boolean value) {

		bindConstant()
				.annotatedWith(clazz)
				.to(value);
	}
}
