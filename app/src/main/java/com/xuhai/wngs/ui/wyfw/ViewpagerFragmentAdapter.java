package com.xuhai.wngs.ui.wyfw;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;

/**
 * Created by Wr on 2015/1/28.
 */
public class ViewpagerFragmentAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    public ViewpagerFragmentAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        this.fm=supportFragmentManager;
    }
    public ArrayList<Fragment> getFragments() {
        return fragments;
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


}
