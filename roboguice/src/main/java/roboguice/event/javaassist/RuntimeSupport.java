/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999-2007 Shigeru Chiba. All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */

package roboguice.event.javaassist;

import java.lang.reflect.Method;

/**
 * Runtime support routines that the classes generated by ProxyFactory use.
 *
 */
public class RuntimeSupport {

    //Respectfully Borrowed from the JavaAssist library, trimmed for only needed pieces.

    /**
     * Makes a descriptor for a given method.
     */
    public static String makeDescriptor(Method m) {
        Class<?>[] params = m.getParameterTypes();
        return makeDescriptor(params, m.getReturnType());
    }

    /**
     * Makes a descriptor for a given method.
     *
     * @param params    parameter types.
     * @param retType   return type.
     */
    public static String makeDescriptor(Class<?>[] params, Class<?> retType) {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append('(');
        for (int i = 0; i < params.length; i++)
            makeDesc(sbuf, params[i]);

        sbuf.append(')');
        makeDesc(sbuf, retType);
        return sbuf.toString();
    }

    private static void makeDesc(StringBuffer sbuf, Class<?> type) {
        if (type.isArray()) {
            sbuf.append('[');
            makeDesc(sbuf, type.getComponentType());
        }
        else if (type.isPrimitive()) {
            if (type == Void.TYPE)
                sbuf.append('V');
            else if (type == Integer.TYPE)
                sbuf.append('I');
            else if (type == Byte.TYPE)
                sbuf.append('B');
            else if (type == Long.TYPE)
                sbuf.append('J');
            else if (type == Double.TYPE)
                sbuf.append('D');
            else if (type == Float.TYPE)
                sbuf.append('F');
            else if (type == Character.TYPE)
                sbuf.append('C');
            else if (type == Short.TYPE)
                sbuf.append('S');
            else if (type == Boolean.TYPE)
                sbuf.append('Z');
            else
                throw new RuntimeException("bad type: " + type.getName());
        }
        else
            sbuf.append('L').append(type.getName().replace('.', '/'))
                .append(';');
    }
}
