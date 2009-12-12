/*
 * Copyright 2009 Michael Burton
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package roboguice.inject;

import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * An object capable of providing objects of type {@code TO}, using extras of
 * type {@code FROM}.<br />
 * <br />
 * Converters enable you to convert Android Extras to complex objects. For
 * instance, sending a long timestamp as an extra, and getting a Date injected.<br />
 * <br />
 * Once you have created an {@link ExtraConverter} implementation, you should
 * bind it in your module configuration. Usage example: {@code bind(new
 * TypeLiteral<ExtraConverter<Long, Date>>() ).to(DateExtraConverter.class);}<br />
 * <br />
 * Usage in your components is as simple as : {@code
 * \@InjectExtra("timestampExtra") protected Date date; } <br />
 * <br />
 * You will usually want your converters to be singletons, so you should
 * annotate them with {@link Singleton}.<br />
 * <br />
 * Please notice that the context in which the bean that gets the extra injected
 * is scoped should implements {@link InjectorProvider} to get the convert
 * mechanism to work. This is because the {@link ExtrasMembersInjector} need an
 * {@link Injector} to get an {@link ExtraConverter} instance.
 * 
 * @param <FROM>
 * @param <TO>
 * 
 * @author Pierre-Yves Ricau (py.ricau+roboguice@gmail.com)
 */
public interface ExtraConverter<FROM, TO> {
    /**
     * Converts an instance of FROM to an instances of TO. May return null.
     * 
     * @param from
     *            The extra value to be converted.
     * @return The converted object that will be injected.
     */
    TO convert(FROM from);
}
