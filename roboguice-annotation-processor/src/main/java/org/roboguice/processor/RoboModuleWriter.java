package org.roboguice.processor;

import java.awt.LayoutManager;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.lang.model.element.Modifier;

import lombok.Getter;
import lombok.Setter;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.squareup.javawriter.JavaWriter;

public class RoboModuleWriter {

    @Setter
    private List<String> androidServiceClassList = new ArrayList<String>();
    @Getter
    @Setter
    private String roboModulePackageName;
    @Getter
    @Setter
    private String roboModuleClassName;

    private HashMap<String, Class<?>> mapFeatureVariableToAndroidServiceClass = new HashMap<String, Class<?>>();

    public RoboModuleWriter() {
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureLocationService", LocationManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureWindowManagerService", WindowManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureActivityManagerService", ActivityManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeaturePowerManagerService", PowerManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureAlarmManagerService", AlarmManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureNotificationManagerService", NotificationManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureKeyguardManagerService", KeyguardManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureVibratorService", Vibrator.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureConnectivityManagerService", ConnectivityManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureWifiManagerService", WifiManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureInputMethodManagerService", InputMethodManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureSensorManagerService", SensorManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureTelephonyManagerService", TelephonyManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureAudioManagerService", AudioManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureSearchManagerService", SearchManager.class);
        mapFeatureVariableToAndroidServiceClass.put("hasFeatureLayoutInflaterService", LayoutManager.class);
    }

    public void writeRoboModule(Writer out) throws IOException {
        JavaWriter writer = new JavaWriter(out);
        // TODO javawriter doesn't handle imports properly. V3.0.0 should change this
        // but for now just don't use imports, except a few.
        writer.setCompressingTypes(false);

        writer.emitPackage(roboModulePackageName);

        writer.emitImports("roboguice.inject.ContextScope", //
                "roboguice.inject.ResourceListener", //
                "roboguice.inject.ViewListener", //
                "android.app.Application");
        writer.emitEmptyLine();

        writer.beginType(roboModuleClassName, "class", EnumSet.of(Modifier.PUBLIC), "roboguice.config.DefaultRoboModule");
        writer.emitEmptyLine();

        //constructor
        writer.beginMethod("", roboModuleClassName, EnumSet.of(Modifier.PUBLIC), "Application", "application", "ContextScope", "contextScope", "ViewListener", "viewListener", "ResourceListener",
                "resourceListener");
        writer.emitStatement("super(application, contextScope, viewListener, resourceListener)");
        writer.endMethod();
        writer.emitEmptyLine();

        //system services
        writer.beginMethod("void", "configure", EnumSet.of(Modifier.PROTECTED));
        for (Entry<String, Class<?>> entry : mapFeatureVariableToAndroidServiceClass.entrySet()) {
            writer.emitStatement("%s = %s", entry.getKey(), androidServiceClassList.contains(entry.getValue().getName()));
        }
        writer.emitStatement("super.configure()");
        writer.endMethod();

        writer.endType();

        writer.close();
    }
}
