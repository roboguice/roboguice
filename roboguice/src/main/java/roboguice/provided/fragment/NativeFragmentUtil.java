package roboguice.provided.fragment;

import roboguice.provided.fragment.FragmentUtil.f;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;
@TargetApi(13)
public class NativeFragmentUtil implements f<Fragment,FragmentManager> {
	//Incredible hack required to ensure that classes are loaded at construction time
	//I.E. so that it fails fast if they're not found. TODO fix this
	
	//I thought about just depending on the API level, but that broke Robolectric.
	private final Class frag;
	private final Class fragM;
	
	public NativeFragmentUtil() throws ClassNotFoundException {
		frag = Class.forName(Fragment.class.getName());
		fragM = Class.forName(FragmentManager.class.getName());
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

	@Override
	public Class fragmentManagerProviderType() {
		return FragmentManagerProvider.class;
	}

}
