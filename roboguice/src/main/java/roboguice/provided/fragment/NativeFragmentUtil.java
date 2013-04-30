package roboguice.provided.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.View;

import roboguice.provided.fragment.FragmentUtil.f;
@TargetApi(13)
public class NativeFragmentUtil implements f<Fragment,FragmentManager> {

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
