package roboguice.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.text.SimpleDateFormat;

/**
 * A more natural android logging facility.
 *
 * WARNING: CHECK OUT COMMON PITFALLS BELOW
 *
 * Unlike {@link android.util.Log}, Log provides sensible defaults.
 * Debug and Verbose logging is enabled for applications that
 * have "android:debuggable=true" in their AndroidManifest.xml.
 * For apps built using SDK Tools r8 or later, this means any debug
 * build.  Release builds built with r8 or later will have verbose
 * and debug log messages turned off.
 *
 * The default tag is automatically set to your app's packagename,
 * and the current context (eg. activity, service, application, etc)
 * is appended as well.  You can add an additional parameter to the
 * tag using {@link #Log(String)}.
 *
 * Log-levels can be programatically overridden for specific instances
 * using {@link #Log(String, boolean, boolean)}.
 *
 * Log messages may optionally use {@link String#format(String, Object...)}
 * formatting, which will not be evaluated unless the log statement is output.
 * Additional parameters to the logging statement are treated as varrgs parameters
 * to {@link String#format(String, Object...)}
 *
 * Also, the current file and line is automatically appended to the tag
 * (this is only done if debug is enabled for performance reasons).
 *
 * COMMON PITFALLS:
 * * Make sure you put the exception FIRST in the call.  A common
 *   mistake is to place it last as is the android.util.Log convention,
 *   but then it will get treated as varargs parameter.
 * * vararg parameters are not appended to the log message!  You must
 *   insert them into the log message using %s or another similar
 *   format parameter
 *
 * Usage Examples:
 *
 * Ln.v("hello there");
 * Ln.d("%s %s", "hello", "there");
 * Ln.e( exception, "Error during some operation");
 * Ln.w( exception, "Error during %s operation", "some other");
 *
 *
 */
@SuppressWarnings({"ImplicitArrayToString"})
public class Ln  {
    protected static boolean isVerboseEnabled = true;
    protected static boolean isDebugEnabled = true;
    protected static String packageName = "";
    protected static boolean configured = false;
    protected static String scope = "";

    @Inject protected static Provider<Context> contextProvider;
    @Inject protected static Provider<SharedPreferences> prefsProvider;

    private Ln() {}

    public static void init() {
        if(!configured && contextProvider!=null ) {
            synchronized(Ln.class) {
                if(!configured) {
                    try {
                        final Context context = contextProvider.get();
                        packageName = context.getPackageName();
                        final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
                        isVerboseEnabled = isDebugEnabled = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                        scope = packageName.toUpperCase();
                        configured = true;
                    } catch( PackageManager.NameNotFoundException e ) {
                        android.util.Log.e(packageName, "Error configuring logger", e);
                    }
                }
            }
        }
    }


    public static int v(Throwable t) {
        init();
        return isVerboseEnabled ? println(Log.VERBOSE, Log.getStackTraceString(t) ) : 0;
    }

    public static int v(Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return isVerboseEnabled ? println(Log.VERBOSE, args.length>0 ? String.format(s,args) : s) : 0;
    }

    public static int v(Throwable throwable, Object s1, Object... args ) {
        init();
        final String s = Strings.toString(s1);
        return isVerboseEnabled ? println(Log.VERBOSE,(args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable)) : 0;
    }

    public static int d(Throwable t) {
        init();
        return isDebugEnabled ? println(Log.DEBUG, Log.getStackTraceString(t)) : 0;
    }

    public static int d(Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return isDebugEnabled ? println(Log.DEBUG, args.length>0 ? String.format(s,args) : s) : 0;
    }

    public static int d(Throwable throwable, Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return isDebugEnabled ? println(Log.DEBUG, (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable)) : 0;
    }

    public static int i(Throwable t) {
        init();
        return println(Log.INFO,Log.getStackTraceString(t));
    }

    public static int i( Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return println(Log.INFO,args.length>0 ? String.format(s,args) : s );
    }

    public static int i(Throwable throwable, Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return println(Log.INFO, (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable));
    }

    public static int w(Throwable t) {
        init();
        return println(Log.WARN, Log.getStackTraceString(t));
    }

    public static int w( Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return println(Log.WARN,args.length>0 ? String.format(s,args) : s);
    }

    public static int w( Throwable throwable, Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return println(Log.WARN, (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable));
    }

    public static int e(Throwable t) {
        init();
        return println(Log.ERROR, Log.getStackTraceString(t));
    }

    public static int e( Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return println(Log.ERROR,args.length>0 ? String.format(s,args) : s);
    }

    public static int e( Throwable throwable, Object s1, Object... args) {
        init();
        final String s = Strings.toString(s1);
        return println(Log.ERROR, (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable));
    }

    public static boolean isDebugEnabled() {
        init();
        return isDebugEnabled;
    }

    public static boolean isVerboseEnabled() {
        init();
        return isVerboseEnabled;
    }

    protected static String getScope() {
        init();
        if( isDebugEnabled ) {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[5];
            return scope + "/" + trace.getFileName() + ":" + trace.getLineNumber();
        }

        return scope;
    }

    public static int println( int priority, String message ) {
        if( isDebugEnabled )
            message = String.format("%s %s %s", new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis()), Thread.currentThread().getName(), message);
        
        return Log.println(priority, getScope(), message);
    }

    protected static String toSafeLabelString( String str ) {
        str = Strings.toString(str);
        str = str.replaceAll("\\W+","_");
        if( str.length()>30 )
            str = str.substring(0,30);
        return str;
    }

}
