package roboguice.support.fragment;

import roboguice.provided.fragment.FragmentUtil.f;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

public class SupportFragmentUtil implements f<Fragment, FragmentManager>{

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

}
