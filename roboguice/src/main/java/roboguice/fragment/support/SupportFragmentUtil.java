package roboguice.fragment.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import roboguice.fragment.FragmentUtil.f;

/**
 * Support v4 based implementation of Fragment utility class.
 * @author Charles Munger
 */
@SuppressWarnings("unchecked") //we're being ambiguous on purpose
public class SupportFragmentUtil implements f<Fragment, FragmentManager>{
	// Incredible hack required to ensure that classes are loaded at construction time
	// I.E. so that it fails fast if they're not found. TODO fix this
	public SupportFragmentUtil() throws ClassNotFoundException {
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

	@SuppressWarnings("rawtypes")
	@Override
	public Class fragmentManagerProviderType() {
		return FragmentManagerProvider.class;
	}

}

