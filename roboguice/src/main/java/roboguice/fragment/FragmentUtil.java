package roboguice.fragment;

import com.google.inject.Provider;

import android.app.Activity;
import android.view.View;

/**
 * Fragment utility class, it's actual implementation will use native or support library v4 fragment's based 
 * on whether or not the underlying app uses support library or not.
 * @author Charles Munger
 */
@SuppressWarnings({ "unchecked", "rawtypes","PMD" }) //Need an unchecked conversion 
public final class FragmentUtil {
    public static final String SUPPORT_PACKAGE = "android.support.v4.app.";
    public static final String NATIVE_PACKAGE = "android.app.";

    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_REFACTORED_TO_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier","checkstyle:staticvariablename"})
    public static f nativeFrag = null;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_REFACTORED_TO_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier","checkstyle:staticvariablename"})
    public static f supportFrag = null;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_REFACTORED_TO_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier","checkstyle:staticvariablename"})
    public static Class<? extends Activity> supportActivity = null;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_REFACTORED_TO_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier","checkstyle:staticvariablename"})
    public static boolean hasNative = false;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("MS_SHOULD_BE_REFACTORED_TO_BE_FINAL")
    @SuppressWarnings({"checkstyle:visibilitymodifier","checkstyle:staticvariablename"})
    public static boolean hasSupport = false;

    @SuppressWarnings("checkstyle:typename")
    public interface f<fragType,fragManagerType> {
        View getView(fragType frag);
        fragType findFragmentById(fragManagerType fm, int id);
        fragType findFragmentByTag(fragManagerType fm, String tag);
        Class<fragType> fragmentType();
        Class<fragManagerType> fragmentManagerType();
        Class<Provider<fragManagerType>> fragmentManagerProviderType();
    }

    static {
        try {
            nativeFrag = (f) Class.forName("roboguice.fragment.provided.NativeFragmentUtil").newInstance();
            hasNative = nativeFrag != null;
        } catch (Throwable e) {}

        try {
            supportFrag = (f) Class.forName("roboguice.fragment.support.SupportFragmentUtil").newInstance();
            supportActivity = (Class<? extends Activity>) Class.forName(SUPPORT_PACKAGE+"FragmentActivity");
            hasSupport = supportFrag != null && supportActivity != null;
        } catch (Throwable e) {}
    }

    private FragmentUtil() {
        //private utility class constructor
    }
}

