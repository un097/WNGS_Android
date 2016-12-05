package com.xuhai.wngs.adapters.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xuhai.wngs.beans.shzl.ShzlBldBannerBean;
import com.xuhai.wngs.ui.pagers.ImagePagerFragment;

import java.util.List;

/**
 * Created by changliang on 15/1/13.
 */
public class ImagePagerAdapter extends FragmentPagerAdapter {

    private List<ShzlBldBannerBean> bldbannerList;

    public ImagePagerAdapter(FragmentManager fragmentManager, List<ShzlBldBannerBean> list) {
        super(fragmentManager);
        this.bldbannerList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return ImagePagerFragment.newInstance(bldbannerList.get(position).getImg(), position);
    }

    @Override
    public int getCount() {
        return bldbannerList.size();
    }
}
