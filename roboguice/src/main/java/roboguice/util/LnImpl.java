package roboguice.util;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LnImpl {
    /**
     * config is initially set to BaseConfig() with sensible defaults, then replaced
     * by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    @Inject protected BaseConfig config = new BaseConfig();

    /**
     * print is initially set to Print(), then replaced by guice during
     * static injection pass.  This allows overriding where the log message is delivered to.
     */
    @Inject protected Ln.Print print = new BasePrint();

    public int v(Throwable t) {
        return config.minimumLogLevel <= Log.VERBOSE ? print.println(Log.VERBOSE, Log.getStackTraceString(t)) : 0;
    }

    public int v(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.VERBOSE )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.VERBOSE, message);
    }

    public int v(Throwable throwable, Object s1, Object[] args) {
        if( config.minimumLogLevel > Log.VERBOSE )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.VERBOSE, message);
    }

    public int d(Throwable t) {
        return config.minimumLogLevel <= Log.DEBUG ? print.println(Log.DEBUG, Log.getStackTraceString(t)) : 0;
    }

    public int d(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.DEBUG )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.DEBUG, message);
    }

    public int d(Throwable throwable, Object s1, Object... args ) {
        if( config.minimumLogLevel > Log.DEBUG )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.DEBUG, message);
    }

    public int i(Throwable t) {
        return config.minimumLogLevel <= Log.INFO ? print.println(Log.INFO, Log.getStackTraceString(t)) : 0;
    }

    public int i(Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.INFO )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.INFO, message);
    }

    public int i(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.INFO )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.INFO, message);
    }

    public int w(Throwable t) {
        return config.minimumLogLevel <= Log.WARN ? print.println(Log.WARN, Log.getStackTraceString(t)) : 0;
    }

    public int w(Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.WARN )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.WARN, message);
    }

    public int w(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.WARN )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.WARN, message);
    }

    public int e(Throwable t) {
        return config.minimumLogLevel <= Log.ERROR ? print.println(Log.ERROR, Log.getStackTraceString(t)) : 0;
    }

    public int e(Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.ERROR )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.ERROR, message);
    }

    public int e(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.ERROR )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.ERROR, message);
    }

    public boolean isDebugEnabled() {
        return config.minimumLogLevel <= Log.DEBUG;
    }

    public boolean isVerboseEnabled() {
        return config.minimumLogLevel <= Log.VERBOSE;
    }

    public BaseConfig getConfig() {
        return config;
    }

    public String logLevelToString(int loglevel) {
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


    public static class BaseConfig implements Ln.Config {
        protected int minimumLogLevel = Log.VERBOSE;
        protected String packageName = "";
        protected String tag = "";

        protected BaseConfig() {
        }

        @Inject
        public BaseConfig(Application context) {
            try {
                packageName = context.getPackageName();
                final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
                minimumLogLevel = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ? Log.VERBOSE : Log.INFO;
                tag = packageName.toUpperCase();

                Ln.d("Configuring Logging, minimum log level is %s", Ln.logLevelToString(minimumLogLevel) );

            } catch( Exception e ) {
                try {
                    Log.e(packageName, "Error configuring logger", e);
                } catch( RuntimeException f ) {
                    // HACK ignore Stub! errors in mock objects during testing
                }
            }
        }

        public int getLoggingLevel() {
            return minimumLogLevel;
        }

        public void setLoggingLevel(int level) {
            minimumLogLevel = level;
        }

        public String getTag() {
            return tag;
        }
    }

    public static class BasePrint implements Ln.Print {
        public int println(int priority, String msg ) {
            return Log.println(priority, getTag(6), processMessage(msg));
        }

        public String processMessage(String msg) {
            if( Ln.getConfig().getLoggingLevel() <= Log.DEBUG )
                msg = String.format("%s %s", Thread.currentThread().getName(), msg);
            return msg;
        }

        protected static String getTag(int skipDepth) {
            final Ln.Config config = Ln.getConfig();
            final String tag = config.getTag();
            if( config.getLoggingLevel() <= Log.DEBUG ) {
                final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];
                return tag + "/" + trace.getFileName() + ":" + trace.getLineNumber();
            }

            return tag;
        }

    }

}
