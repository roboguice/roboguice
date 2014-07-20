package roboguice.util;

import java.util.Locale;

import com.google.inject.Inject;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class LnImpl implements LnInterface {

    protected int minimumLogLevel = Log.VERBOSE;
    protected String packageName = "";
    protected String tag = "";

    public LnImpl() {
        // do nothing, used by Ln before injection is set up
    }

    @Inject
    public LnImpl(Application context) {
        try {
            packageName = context.getPackageName();
            final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
            minimumLogLevel = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ? Log.VERBOSE : Log.INFO;
            tag = packageName.toUpperCase(Locale.US);

            Ln.d("Configuring Logging, minimum log level is %s", Ln.logLevelToString(minimumLogLevel) );

        } catch( Exception e ) {
            try {
                Log.e(packageName, "Error configuring logger", e);
            } catch( RuntimeException f ) { // NOPMD - Legal empty catch block
                // HACK ignore Stub! errors in mock objects during testing
            }
        }
    }



    @Override
    public int v(Throwable t) {
        return getLoggingLevel() <= Log.VERBOSE ? println(Log.VERBOSE, Log.getStackTraceString(t)) : 0;
    }

    @Override
    public int v(Object s1, Object... args) {
        if( getLoggingLevel()> Log.VERBOSE )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return println(Log.VERBOSE, message);
    }

    @Override
    public int v(Throwable throwable, Object s1, Object[] args) {
        if( getLoggingLevel()> Log.VERBOSE )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return println(Log.VERBOSE, message);
    }

    @Override
    public int d(Throwable t) {
        return getLoggingLevel()<= Log.DEBUG ? println(Log.DEBUG, Log.getStackTraceString(t)) : 0;
    }

    @Override
    public int d(Object s1, Object... args) {
        if( getLoggingLevel()> Log.DEBUG )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return println(Log.DEBUG, message);
    }

    @Override
    public int d(Throwable throwable, Object s1, Object... args) {
        if( getLoggingLevel()> Log.DEBUG )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return println(Log.DEBUG, message);
    }

    @Override
    public int i(Throwable t) {
        return getLoggingLevel()<= Log.INFO ? println(Log.INFO, Log.getStackTraceString(t)) : 0;
    }

    @Override
    public int i(Throwable throwable, Object s1, Object... args) {
        if( getLoggingLevel()> Log.INFO )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return println(Log.INFO, message);
    }

    @Override
    public int i(Object s1, Object... args) {
        if( getLoggingLevel()> Log.INFO )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return println(Log.INFO, message);
    }

    @Override
    public int w(Throwable t) {
        return getLoggingLevel()<= Log.WARN ? println(Log.WARN, Log.getStackTraceString(t)) : 0;
    }

    @Override
    public int w(Throwable throwable, Object s1, Object... args) {
        if( getLoggingLevel()> Log.WARN )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return println(Log.WARN, message);
    }

    @Override
    public int w(Object s1, Object... args) {
        if( getLoggingLevel()> Log.WARN )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return println(Log.WARN, message);
    }

    @Override
    public int e(Throwable t) {
        return getLoggingLevel()<= Log.ERROR ? println(Log.ERROR, Log.getStackTraceString(t)) : 0;
    }

    @Override
    public int e(Throwable throwable, Object s1, Object... args) {
        if( getLoggingLevel()> Log.ERROR )
            return 0;

        final String s = Strings.toString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return println(Log.ERROR, message);
    }

    @Override
    public int e(Object s1, Object... args) {
        if( getLoggingLevel()> Log.ERROR )
            return 0;

        final String s = Strings.toString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return println(Log.ERROR, message);
    }

    @Override
    public boolean isDebugEnabled() {
        return getLoggingLevel()<= Log.DEBUG;
    }

    @Override
    public boolean isVerboseEnabled() {
        return getLoggingLevel()<= Log.VERBOSE;
    }

    @Override
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

            default:
                return "UNKNOWN";
        }
    }


    @Override
    public int getLoggingLevel() {
        return minimumLogLevel;
    }

    @Override
    public void setLoggingLevel(int level) {
        minimumLogLevel = level;
    }

    public int println(int priority, String msg ) {
        return Log.println(priority, getTag(), processMessage(msg));
    }

    protected String processMessage(String msg) {
        if( getLoggingLevel() <= Log.DEBUG )
            msg = String.format("%s %s", Thread.currentThread().getName(), msg);
        return msg;
    }

    protected String getTag() {
        final int skipDepth = 6; // skip 6 stackframes to find the location where this was called
        if( getLoggingLevel() <= Log.DEBUG ) {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];
            return tag + "/" + trace.getFileName() + ":" + trace.getLineNumber();
        }

        return tag;
    }


}
