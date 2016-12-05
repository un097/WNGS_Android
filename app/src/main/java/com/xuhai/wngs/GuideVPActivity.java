package com.xuhai.wngs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xuhai.wngs.ui.main.MainCitySelActivity;

import java.util.ArrayList;
import java.util.List;


public class GuideVPActivity extends BaseActionBarAsUpActivity implements ViewPager.OnPageChangeListener {

    private List<View> views;
    private ViewPager viewPager;
    private ImageView dot[];
    private static String FirstPrefrences = "FirstPrefrences";
    private int currindex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_vp);
        initView();
        initDot();
    }

    private void initDot() {
        dot = new ImageView[4];
        LinearLayout ll = (LinearLayout) findViewById(R.id.linerLayout);
        for (int i = 0; i < 4; i++) {
            dot[i] = (ImageView) ll.getChildAt(i);
            dot[i].setEnabled(false);
        }
        currindex = 0;
        dot[currindex].setEnabled(true);
    }


    private void initView() {
        views = new ArrayList<View>();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        views.add(layoutInflater.inflate(R.layout.viewpager1, null));
        views.add(layoutInflater.inflate(R.layout.viewpager2, null));
        views.add(layoutInflater.inflate(R.layout.viewpager3, null));
        views.add(layoutInflater.inflate(R.layout.viewpager4, null));
        viewPager = (ViewPager) this.findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);

    }

    private void setCurrentDot(int position) {
        // TODO Auto-generated method stub
        if (position < 0 || position > views.size() - 1
                || currindex == position) {
            return;
        }

        dot[position].setEnabled(true);
        dot[currindex].setEnabled(false);

        currindex = position;
    }


    PagerAdapter adapter = new PagerAdapter() {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return views.size();
        }

        // 销毁arg1位置的界面
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return (arg0 == arg1);
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            if (arg1 == views.size() - 1) {
                ImageView button = (ImageView) arg0
                        .findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 设置已经引导
                        setGuided();
                        goMain();
                    }

                });
            }
            return views.get(arg1);
        }
    };

    private void setGuided() {
        // TODO Auto-generated method stub
        editor.putBoolean(SPN_IS_FIRST_OPEN, false);
        editor.commit();
    }


    private void goMain() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(GuideVPActivity.this, MainCitySelActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        setCurrentDot(arg0);
    }

}


