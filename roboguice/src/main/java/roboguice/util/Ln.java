package roboguice.util;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.google.inject.Inject;

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
     * config is initially set to BaseConfig() with sensible defaults, then replaced
     * by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    @Inject protected static BaseConfig config = new BaseConfig();

    /**
     * print is initially set to Print(), then replaced by guice during
     * static injection pass.  This allows overriding where the log message is delivered to.
     */
    @Inject protected static Print print = new Print();



    private Ln() {}



    public static int v(Throwable t) {
        return config.minimumLogLevel <= Log.VERBOSE ? print.println(Log.VERBOSE, Log.getStackTraceString(t)) : 0;
    }

    public static int v(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.VERBOSE )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.VERBOSE, message);
    }

    public static int v(Throwable throwable, Object s1, Object... args ) {
        if( config.minimumLogLevel > Log.VERBOSE )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.VERBOSE, message);
    }

    public static int d(Throwable t) {
        return config.minimumLogLevel <= Log.DEBUG ? print.println(Log.DEBUG, Log.getStackTraceString(t)) : 0;
    }

    public static int d(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.DEBUG )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.DEBUG, message);
    }

    public static int d(Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.DEBUG )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.DEBUG, message);
    }

    public static int i(Throwable t) {
        return config.minimumLogLevel <= Log.INFO ? print.println(Log.INFO, Log.getStackTraceString(t)) : 0;
    }

    public static int i( Object s1, Object... args) {
        if( config.minimumLogLevel > Log.INFO )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.INFO, message);
    }

    public static int i(Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.INFO )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.INFO, message);
    }

    public static int w(Throwable t) {
        return config.minimumLogLevel <= Log.WARN ? print.println(Log.WARN, Log.getStackTraceString(t)) : 0;
    }

    public static int w( Object s1, Object... args) {
        if( config.minimumLogLevel > Log.WARN )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.WARN, message);
    }

    public static int w( Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.WARN )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.WARN, message);
    }

    public static int e(Throwable t) {
        return config.minimumLogLevel <= Log.ERROR ? print.println(Log.ERROR, Log.getStackTraceString(t)) : 0;
    }

    public static int e( Object s1, Object... args) {
        if( config.minimumLogLevel > Log.ERROR )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.ERROR, message);
    }

    public static int e( Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.ERROR )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.ERROR, message);
    }

    public static boolean isDebugEnabled() {
        return config.minimumLogLevel <= Log.DEBUG;
    }

    public static boolean isVerboseEnabled() {
        return config.minimumLogLevel <= Log.VERBOSE;
    }

    public static Config getConfig() {
        return config;
    }


    public static interface Config {
        public int getLoggingLevel();
        public void setLoggingLevel(int level);
    }

    public static class BaseConfig implements Config {
        protected int minimumLogLevel = Log.VERBOSE;
        protected String packageName = "";
        protected String scope = "";

        protected BaseConfig() {
        }

        @Inject
        public BaseConfig(Application context) {
            try {
                packageName = context.getPackageName();
                final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
                minimumLogLevel = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ? Log.VERBOSE : Log.INFO;
                scope = packageName.toUpperCase();

                Ln.d("Configuring Logging, minimum log level is %s", logLevelToString(minimumLogLevel) );

            } catch( Exception e ) {
                Log.e(packageName, "Error configuring logger", e);
            }
        }

        public int getLoggingLevel() {
            return minimumLogLevel;
        }

        public void setLoggingLevel(int level) {
            minimumLogLevel = level;
        }
    }

    public static String logLevelToString( int loglevel ) {
        switch( loglevel ) {
            case Log.VERBOSE:
                return "VERBOSE";
            case Log.DEBUG:
                return "DEBUG";
            case Log.INFO:
                return "INFO";
            case Log.WARN:
                return "WARN";
            case Log.ERROR:
                return "ERROR";
            case Log.ASSERT:
                return "ASSERT";
        }

        return "UNKNOWN";
    }


    /** Default implementation logs to android.util.Log */
    public static class Print {
        public int println(int priority, String msg ) {
            return Log.println(priority,getScope(5), processMessage(msg));
        }

        protected String processMessage(String msg) {
            if( config.minimumLogLevel <= Log.DEBUG )
                msg = String.format("%s %s", Thread.currentThread().getName(), msg);
            return msg;
        }

        protected static String getScope(int skipDepth) {
            if( config.minimumLogLevel <= Log.DEBUG ) {
                final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];
                return config.scope + "/" + trace.getFileName() + ":" + trace.getLineNumber();
            }

            return config.scope;
        }

    }
}
