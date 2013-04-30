package roboguice.support.fragment;

import roboguice.provided.fragment.FragmentUtil.f;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

public class SupportFragmentUtil implements f<Fragment, FragmentManager>{
	// Incredible hack required to ensure that classes are loaded at construction time
	// I.E. so that it fails fast if they're not found. TODO fix this
	private final Class frag;
	private final Class fragM;

	public SupportFragmentUtil() throws ClassNotFoundException {
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
