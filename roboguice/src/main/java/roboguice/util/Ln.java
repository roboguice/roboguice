package roboguice.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.inject.Inject;

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
    /**
     * config is initially set to Config() with sensible defaults, then replaced
     * by Config(Context) during guice static injection pass.
     */
    @Inject protected static Config config = new Config();

    /**
     * print is initially set to Print(), then replaced by guice during
     * static injection pass.  This allows overriding where the log message is delivered to.
     */
    @Inject protected static Print print = new Print();



    private Ln() {}



    public static int v(Throwable t) {
        return config.isVerboseEnabled ? print.println(Log.VERBOSE, Log.getStackTraceString(t)) : 0;
    }

    public static int v(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return config.isVerboseEnabled ? print.println(Log.VERBOSE, message) : 0;
    }

    public static int v(Throwable throwable, Object s1, Object... args ) {
        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return config.isVerboseEnabled ? print.println(Log.VERBOSE, message) : 0;
    }

    public static int d(Throwable t) {
        return config.isDebugEnabled ? print.println(Log.DEBUG, Log.getStackTraceString(t)) : 0;
    }

    public static int d(Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return config.isDebugEnabled ? print.println(Log.DEBUG, message) : 0;
    }

    public static int d(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return config.isDebugEnabled ? print.println(Log.DEBUG, message) : 0;
    }

    public static int i(Throwable t) {
        return print.println(Log.INFO, Log.getStackTraceString(t));
    }

    public static int i( Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.INFO, message);
    }

    public static int i(Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.INFO, message);
    }

    public static int w(Throwable t) {
        return print.println(Log.WARN, Log.getStackTraceString(t));
    }

    public static int w( Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.WARN, message);
    }

    public static int w( Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.WARN, message);
    }

    public static int e(Throwable t) {
        return print.println(Log.ERROR, Log.getStackTraceString(t));
    }

    public static int e( Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.ERROR, message);
    }

    public static int e( Throwable throwable, Object s1, Object... args) {
        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.ERROR, message);
    }

    public static boolean isDebugEnabled() {
        return config.isDebugEnabled;
    }

    public static boolean isVerboseEnabled() {
        return config.isVerboseEnabled;
    }



    protected static class Config {
        protected boolean isVerboseEnabled = true;
        protected boolean isDebugEnabled = true;
        protected String packageName = "";
        protected String scope = "";

        protected Config() {
        }

        @Inject
        public Config(Context context) {
            synchronized(Config.class) {
                try {
                    packageName = context.getPackageName();
                    final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
                    isVerboseEnabled = isDebugEnabled = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                    scope = packageName.toUpperCase();

                    Ln.d("Configuring Logging, verbose=%s debug=%s",isVerboseEnabled,isDebugEnabled);

                } catch( PackageManager.NameNotFoundException e ) {
                    Log.e(packageName, "Error configuring logger", e);
                }
            }
        }

    }



    /** Default implementation logs to android.util.Log */
    public static class Print {
        public int println(int priority, String msg ) {
            if( config.isDebugEnabled )
                msg = String.format("%s %s %s", new SimpleDateFormat("HH:mm:ss.SSS").format(System.currentTimeMillis()), Thread.currentThread().getName(), msg);

            return Log.println(priority,getScope(),msg);
        }

        protected static String getScope() {
            if( config.isDebugEnabled ) {
                final StackTraceElement trace = Thread.currentThread().getStackTrace()[5];
                return config.scope + "/" + trace.getFileName() + ":" + trace.getLineNumber();
            }

            return config.scope;
        }
        
    }
}
