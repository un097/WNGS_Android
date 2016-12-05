package com.xuhai.wngs.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xuhai.wngs.beans.shzl.ShzlBldBannerBean;
import com.xuhai.wngs.ui.pagers.ImagePagerFragment;
import com.xuhai.wngs.ui.pagers.SpxqImagePagerFragment;

import java.util.List;

/**
 * Created by changliang on 15/1/13.
 */
public class SpxqImagePagerAdapter extends FragmentPagerAdapter {

    private List<String> imageList;

    public SpxqImagePagerAdapter(FragmentManager fragmentManager, List<String> list) {
        super(fragmentManager);
        this.imageList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return SpxqImagePagerFragment.newInstance(imageList.get(position), position);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }
}
