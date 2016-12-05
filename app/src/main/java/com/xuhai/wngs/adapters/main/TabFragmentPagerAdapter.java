package com.xuhai.wngs.adapters.main;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;
import com.xuhai.wngs.R;
import com.xuhai.wngs.TestFragment;

import java.util.ArrayList;

/**
 * Created by changliang on 14-10-9.
 */
public class TabFragmentPagerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentManager fm;

    private static final int[] ICONS = new int[]{
            R.drawable.selector_ic_tab_business,
            R.drawable.selector_ic_tab_shequ,
            R.drawable.selector_ic_tab_live,
            R.drawable.selector_ic_tab_me,
    };

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public ArrayList<Fragment> getFragments() {
        return fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return this.fragments.get(i);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index];
    }

    @Override
    public int getCount() {
        return ICONS.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
