package roboguice.fragment.provided.shadow;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowActivity;

import android.app.Activity;
import android.app.Fragment;
import android.app.Fragment.SavedState;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;


@Implements(Activity.class)
public class ShadowNativeFragmentActivity extends ShadowActivity {

    @Implementation
    public FragmentManager getFragmentManager() {
        return new FragmentManager() {
            @Override
            public void addOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
            }

            @Override
            public FragmentTransaction beginTransaction() {
                return new FragmentTransaction() {
                    @Override
                    public FragmentTransaction add(Fragment fragment, String s) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction add(int i, Fragment fragment) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction add(int i, Fragment fragment, String s) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction replace(int i, Fragment fragment) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction replace(int i, Fragment fragment, String s) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction remove(Fragment fragment) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction hide(Fragment fragment) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction show(Fragment fragment) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction detach(Fragment fragment) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction attach(Fragment fragment) {
                        return null;
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public FragmentTransaction setCustomAnimations(int i, int i1) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction setTransition(int i) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction setTransitionStyle(int i) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction addToBackStack(String s) {
                        return null;
                    }

                    @Override
                    public boolean isAddToBackStackAllowed() {
                        return false;
                    }

                    @Override
                    public FragmentTransaction disallowAddToBackStack() {
                        return null;
                    }

                    @Override
                    public FragmentTransaction setBreadCrumbTitle(int i) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction setBreadCrumbTitle(CharSequence charSequence) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction setBreadCrumbShortTitle(int i) {
                        return null;
                    }

                    @Override
                    public FragmentTransaction setBreadCrumbShortTitle(CharSequence charSequence) {
                        return null;
                    }

                    @Override
                    public int commit() {
                        return 0;
                    }

                    @Override
                    public int commitAllowingStateLoss() {
                        return 0;
                    }

                    @Override
                    public FragmentTransaction setCustomAnimations(int i, int i1, int i2, int i3) {
                        return this;
                    }
                };
            }

            @Override
            public boolean executePendingTransactions() {
                return false;
            }

            @Override
            public Fragment findFragmentById(int i) {
                return null;
            }

            @Override
            public Fragment findFragmentByTag(String s) {
                return null;
            }

            @Override
            public void popBackStack() {
            }

            @Override
            public boolean popBackStackImmediate() {
                return false;
            }

            @Override
            public void popBackStack(String s, int i) {
            }

            @Override
            public boolean popBackStackImmediate(String s, int i) {
                return false;
            }

            @Override
            public void popBackStack(int i, int i1) {
            }

            @Override
            public boolean popBackStackImmediate(int i, int i1) {
                return false;
            }

            @Override
            public int getBackStackEntryCount() {
                return 0;
            }

            @Override
            public BackStackEntry getBackStackEntryAt(int i) {
                return null;
            }

            @Override
            public void removeOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
            }

            @Override
            public void putFragment(Bundle bundle, String s, Fragment fragment) {
            }

            @Override
            public Fragment getFragment(Bundle bundle, String s) {
                return null;
            }

            @Override
            public SavedState saveFragmentInstanceState(Fragment fragment) {
                return null;
            }

            @Override
            public void dump(String s, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strings) {
            }

			//@Override
			public boolean isDestroyed() {
				// TODO Auto-generated method stub
				return false;
			}
        };
    }


}
