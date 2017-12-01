package com.example.nhan.clinicalnotebook2.managers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class ScreenManager {
    private FragmentManager fragmentManager;
    private int fragmentContainerId;
    private static FragmentType currentFragment = FragmentType.MAIN;

    public ScreenManager(FragmentManager fragmentManager, int fragmentContainerId) {
        this.fragmentManager = fragmentManager;
        this.fragmentContainerId = fragmentContainerId;
    }

    public static FragmentType getCurrentFragment() {
        return currentFragment;
    }

    public static void setCurrentFragment(FragmentType currentFragment) {
        ScreenManager.currentFragment = currentFragment;
    }

    public void openFragment(Fragment fragment, boolean addToBackStack) {
//        if (fragment instanceof FragmentListFolder) {
//            currentFragment = FragmentType.FOLDER_NOTE;
//        }else if (fragment instanceof FragmentListNote) {
//            currentFragment = FragmentType.LIST_NOTE;
//        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerId, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (addToBackStack) {
            if (fragmentManager.getBackStackEntryCount() > 0 &&
                    fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1)
                            .toString()
                            .equals(fragment.getClass().getName())){
                fragmentManager.popBackStack();
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            } else if (fragmentManager.getBackStackEntryCount() == 0){
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }


        }
        fragmentTransaction.commit();
    }
}
