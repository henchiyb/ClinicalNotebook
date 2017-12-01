package com.example.nhan.clinicalnotebook2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.nhan.clinicalnotebook2.fragments.FragmentListFolder;

/**
 * Created by Nhan on 1/13/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int countPage;
    public PagerAdapter(FragmentManager fm, int countPage) {
        super(fm);
        this.countPage = countPage;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentListFolder();
            case 1: return new FragmentListFolder();
            default: return new FragmentListFolder();
        }
    }

    @Override
    public int getCount() {
        return countPage;
    }
}
