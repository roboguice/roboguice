package roboguice.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class Strings {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * Like join, but allows for a distinct final delimiter.  For english sentences such
     * as "Alice, Bob and Charlie" use ", " and " and " as the delimiters.
     *
     * @param delimiter     usually ", "
     * @param lastDelimiter usually " and "
     * @param objs          the objects
     * @param <T>           the type
     * @return a string
     */
    public static <T> String joinAnd(final String delimiter, final String lastDelimiter, final Collection<T> objs) {
        if (objs == null || objs.isEmpty()) {
            return "";
        }

        final Iterator<T> iter = objs.iterator();
        final StringBuilder buffer = new StringBuilder(Strings.toString(iter.next()));
        int i = 1;
        while (iter.hasNext()) {
            final T obj = iter.next();
            if (notEmpty(obj)) {
                buffer.append(++i == objs.size() ? lastDelimiter : delimiter);
                buffer.append(Strings.toString(obj));
            }
        }
        return buffer.toString();
    }

    public static <T> String joinAnd(final String delimiter, final String lastDelimiter, final T... objs) {
        return joinAnd(delimiter, lastDelimiter, Arrays.asList(objs));
    }

    public static <T> String join(final String delimiter, final Collection<T> objs) {
        if (objs == null || objs.isEmpty()) {
            return "";
        }

        final Iterator<T> iter = objs.iterator();
        final StringBuilder buffer = new StringBuilder(Strings.toString(iter.next()));

        while (iter.hasNext()) {
            final T obj = iter.next();
            if (notEmpty(obj)) {
                buffer.append(delimiter);
                buffer.append(Strings.toString(obj));
            }
        }
        return buffer.toString();
    }

    public static <T> String join(final String delimiter, final T... objects) {
        return join(delimiter, Arrays.asList(objects));
    }

    public static String toString(final InputStream input) {
        final StringWriter sw = new StringWriter();
        copy(new InputStreamReader(input), sw);
        return sw.toString();
    }

    public static String toString(final Reader input) {
        final StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static int copy(final Reader input, final Writer output) {
        final long count = copyLarge(input, output);
        return count > Integer.MAX_VALUE ? -1 : (int) count;
    }

    public static long copyLarge(final Reader input, final Writer output) throws RuntimeException {
        try {
            final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            long count = 0;
            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(final Object o) {
        return toString(o, "");
    }

    public static String toString(final Object o, final String def) {
        return o == null ? def :
               o instanceof InputStream ? toString((InputStream) o) :
               o instanceof Reader ? toString((Reader) o) :
               o instanceof Object[] ? Strings.join(", ", (Object[]) o) :
               o instanceof Collection ? Strings.join(", ", (Collection<?>) o) :
               o.toString();
    }

    public static boolean isEmpty(final Object o) {
        return toString(o).trim().length() == 0;
    }

    public static boolean notEmpty(final Object o) {
        return !isEmpty(o);
    }

    public static String md5(final String s) {
        // http://stackoverflow.com/questions/1057041/difference-between-java-and-php5-md5-hash
        // http://code.google.com/p/roboguice/issues/detail?id=89
        try {

            final byte[] hash = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"));
            final StringBuilder hashString = new StringBuilder();

            for (final byte aHash : hash) {
                final String hex = Integer.toHexString(aHash);

                if (hex.length() == 1) {
                    hashString.append('0');
                    hashString.append(hex.charAt(hex.length() - 1));
                } else {
                    hashString.append(hex.substring(hex.length() - 2));
                }
            }

            return hashString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String capitalize(final String s) {
        final String c = Strings.toString(s);
        return c.length() >= 2 ? c.substring(0, 1).toUpperCase() + c.substring(1) : c.length() >= 1 ? c.toUpperCase() : c;
    }

    public static boolean equals(final Object a, final Object b) {
        return toString(a).equals(toString(b));
    }

    public static boolean equalsIgnoreCase(final Object a, final Object b) {
        return toString(a).equalsIgnoreCase(toString(b));
    }

    public static String[] chunk(final String str, final int chunkSize) {
        if (isEmpty(str) || chunkSize == 0) {
            return new String[0];
        }

        final int len = str.length();
        final int arrayLen = ((len - 1) / chunkSize) + 1;
        final String[] array = new String[arrayLen];
        for (int i = 0; i < arrayLen; ++i) {
            array[i] = str.substring(i * chunkSize, (i * chunkSize) + chunkSize < len ? (i * chunkSize) + chunkSize : len);
        }

        return array;
    }

    public static String namedFormat(final String str, final Map<String, String> substitutions) {
        String result = str;
        for (final Map.Entry<String, String> entry : substitutions.entrySet()) {
            result = result.replace("$" + entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static String namedFormat(final String str, final Object... nameValuePairs) {
        if (nameValuePairs.length % 2 != 0) {
            throw new InvalidParameterException("You must include one value for each parameter");
        }

        final HashMap<String, String> map = new HashMap<String, String>(nameValuePairs.length / 2);
        for (int i = 0; i < nameValuePairs.length; i += 2) {
            map.put(Strings.toString(nameValuePairs[i]), Strings.toString(nameValuePairs[i + 1]));
        }

        return namedFormat(str, map);
    }

    /**
     * Private default constructor.
     * This is a utilities class and should not be instantiated.
     */
    private Strings() {

    }

}

