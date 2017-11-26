package com.example.ledsoon.ghostssip;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ledsoon on 26.11.17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] fragments;

    public ViewPagerAdapter(FragmentManager fragmentManager, Fragment[] fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getClass().getSimpleName();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
