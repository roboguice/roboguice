package org.roboguice.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementKindVisitor6;

import lombok.Getter;
import lombok.extern.java.Log;
import android.accounts.AccountManager;
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
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * @author SNI
 */
@Log
public class SystemServiceInjectScanner extends ElementKindVisitor6<Void, Void> {

    private static final List<String> INJECTABLE_ANDROID_SERVICES_CLASSES = Arrays.asList(//
            LocationManager.class.getName(),//
            WindowManager.class.getName(),//
            ActivityManager.class.getName(),//
            PowerManager.class.getName(),//
            AlarmManager.class.getName(),//
            NotificationManager.class.getName(),//
            KeyguardManager.class.getName(),//
            Vibrator.class.getName(),//
            ConnectivityManager.class.getName(),//
            WifiManager.class.getName(),//
            InputMethodManager.class.getName(),//
            SensorManager.class.getName(),//
            TelephonyManager.class.getName(),//
            AudioManager.class.getName(),//
            LayoutInflater.class.getName(),//
            SearchManager.class.getName(),
            AccountManager.class.getName());

    @Getter
    private List<String> androidServiceClassList = new ArrayList<String>();

    public List<String> scan(Element variableElement) {
        androidServiceClassList.clear();
        variableElement.accept(this, null);
        return androidServiceClassList;
    }

    @Override
    public Void visitVariableAsField(VariableElement e, Void v) {
        String variableClazz = e.asType().toString();
        log.info(String.format("Scanning : %s.", variableClazz));
        if (INJECTABLE_ANDROID_SERVICES_CLASSES.contains(variableClazz)) {
            log.info(String.format("Injection of Android service class : %s detected.", variableClazz));
            androidServiceClassList.add(variableClazz);
        }
        return super.visitVariableAsField(e, v);
    }
}
