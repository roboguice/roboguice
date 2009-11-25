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
package roboguice.astroboy.bean;

import java.util.Date;

import roboguice.inject.ExtraConverter;

import com.google.inject.Singleton;

/**
 * This class converts an integer timestamp to a date, but doubles the value of
 * the integer.
 */
@Singleton
public class DateTwiceExtraConverter implements ExtraConverter<Integer, Date> {

    @Override
    public Date convert(Integer from) {
        return new Date(2 * from);
    }

}
