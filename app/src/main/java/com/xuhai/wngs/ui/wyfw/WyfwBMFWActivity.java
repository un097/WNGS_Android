package com.xuhai.wngs.ui.wyfw;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.wyfw.WyfwBMFWListAdapter;
import com.xuhai.wngs.beans.wyfw.WyfwBMFWBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WyfwBMFWActivity extends BaseActionBarAsUpActivity {

    private ViewPager viewPager;
    private ViewpagerFragmentAdapter viewpagerFragmentAdapter;
    private ImageView bmxx, ycfw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyfw_bmfw);

        initViewPager();

    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewpagerFragmentAdapter = new ViewpagerFragmentAdapter(getSupportFragmentManager());

        for (int i = 0; i < viewpagerFragmentAdapter.getCount(); i++) {
            if (i < viewpagerFragmentAdapter.getCount()) {
                Fragment fragment = WyfwFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("index", i);
                fragment.setArguments(bundle);
                viewpagerFragmentAdapter.getFragments().add(fragment);
            }
        }

        viewPager.setOffscreenPageLimit(viewpagerFragmentAdapter.getCount());
        viewPager.setAdapter(viewpagerFragmentAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    bmxx.setImageResource(R.drawable.bar_bmxx_off);
                    ycfw.setImageResource(R.drawable.bar_ycwf_on);
                    if (getActionBar()!=null){
                        getActionBar().setTitle("有偿服务");
                    }
                } else if (position == 1) {
                    bmxx.setImageResource(R.drawable.bar_bmxx_on);
                    ycfw.setImageResource(R.drawable.bar_ycfw_off);
                    if (getActionBar()!=null){
                        getActionBar().setTitle("便民信息");
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bmxx = (ImageView) findViewById(R.id.bmxx);
        ycfw = (ImageView) findViewById(R.id.ycfw);
        bmxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        ycfw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });


    }

    public void setPagerItem(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wyfw_bmfw, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(WyfwBMFWActivity.this, "bmfw");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
