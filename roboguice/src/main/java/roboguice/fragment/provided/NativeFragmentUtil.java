package roboguice.fragment.provided;

import com.google.inject.Inject;
import com.google.inject.Provider;

import roboguice.fragment.FragmentUtil.f;
import roboguice.inject.ContextSingleton;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.view.View;

/**
 * Native implementation of Fragment utility class.
 * @author Charles Munger
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressWarnings("unchecked") //No point in seeing warnings when you're being ambiguous on purpose
public class NativeFragmentUtil implements f<Fragment,FragmentManager> {
    //Incredible hack required to ensure that classes are loaded at construction time
    //I.E. so that it fails fast if they're not found. TODO fix this
    
    //I thought about just depending on the API level, but that broke Robolectric.
    public NativeFragmentUtil() throws ClassNotFoundException {
        Class.forName(Fragment.class.getName());
        Class.forName(FragmentManager.class.getName());
    }
    
    @Override
    public View getView(Fragment frag) {
        return frag.getView();
    }

    @Override
    public Fragment findFragmentById(FragmentManager fm, int id) {
        return fm.findFragmentById(id);
    }

    @Override
    public Fragment findFragmentByTag(FragmentManager fm, String tag) {
        return fm.findFragmentByTag(tag);
    }

    @Override
    public Class<Fragment> fragmentType() {
        return Fragment.class;
    }

    @Override
    public Class<FragmentManager> fragmentManagerType() {
        return FragmentManager.class;
    }

    @SuppressWarnings("rawtypes") //not technically a Class<Provider<FragmentManager>>
    @Override
    public Class fragmentManagerProviderType() {
        return FragmentManagerProvider.class;
    }

    @ContextSingleton
    public static class FragmentManagerProvider implements Provider<FragmentManager> {
        @Inject protected Activity activity;

        @Override
        public FragmentManager get() {
            return activity.getFragmentManager();
        }
    }

}
