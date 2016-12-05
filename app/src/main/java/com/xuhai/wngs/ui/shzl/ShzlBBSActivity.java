package com.xuhai.wngs.ui.shzl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.shzl.ShzlBBSRecyclerAdapter;
import com.xuhai.wngs.beans.more.MoreSHDZListBean;
import com.xuhai.wngs.ui.more.LoginActivity;
import com.xuhai.wngs.ui.wyfw.ViewpagerFragmentAdapter;
import com.xuhai.wngs.ui.wyfw.WyfwFragment;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.shzl.ShzlBBSListAdapter;
import com.xuhai.wngs.beans.shzl.ShzlBBSListBean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lucasr.twowayview.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public class ShzlBBSActivity extends BaseActionBarAsUpActivity {

    private final String TAG = "ShzlBBSActivity";
    private boolean buer = false;
    private ViewPager viewPager;
    private ViewpagerFragmentAdapter viewpagerFragmentAdapter;
    private ImageView linli, linshang;

    public int currentItem;
    private ImageView newxx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bbs);





        initViewPager();
    }

    private void initViewPager() {

        linli = (ImageView) findViewById(R.id.linli);
        linshang = (ImageView) findViewById(R.id.linshang);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewpagerFragmentAdapter = new ViewpagerFragmentAdapter(getSupportFragmentManager());

        for (int i = 0; i < viewpagerFragmentAdapter.getCount(); i++) {
            if (i < viewpagerFragmentAdapter.getCount()) {
                Fragment fragment = BBSFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putInt("index", i);
                fragment.setArguments(bundle);
                viewpagerFragmentAdapter.getFragments().add(fragment);
            }
        }

        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(viewpagerFragmentAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    linli.setImageResource(R.drawable.linli_on);
                    linshang.setImageResource(R.drawable.linshang_off);
                    if (getActionBar()!=null){
                        getActionBar().setTitle("邻里圈");
                    }
                } else if (position == 1) {
                    linli.setImageResource(R.drawable.linli_off);
                    linshang.setImageResource(R.drawable.linshang_on);
                    if (getActionBar()!=null){
                        getActionBar().setTitle("邻商圈");
                    }
                }

            }

            @Override
            public void onPageSelected(int position) {
                setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        linli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        linshang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }
    private Menu mMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shzl_bb, menu);
        mMenu = menu;
        //newxx = (ImageView) menu.findItem(R.id.weidu).getActionView();
        if (BBS.equals("1")){
            menu.findItem(R.id.weidu).setVisible(true);
        }else {
            menu.findItem(R.id.weidu).setVisible(false);
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(ShzlBBSActivity.this, "llq");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (buer){
                    setResult(WEIDU_BBS);
                    finish();
                }else {
                ShzlBBSActivity.this.setResult(LOGIN_SUCCESS);
                finish();}
                return true;
            case R.id.weidu:
                        Intent intent =new Intent();
                        intent.setClass(ShzlBBSActivity.this,ShzlBbsMsgActivity.class);
                        startActivityForResult(intent,WEIDU_BBS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (buer){
                setResult(WEIDU_BBS);
                finish();
            }else {
            ShzlBBSActivity.this.setResult(LOGIN_SUCCESS);
            finish();}
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int p = 1;
        if (resultCode == LOAD_FAIL) {
            //httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + SQID + "&bid=" + "bbs" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            viewpagerFragmentAdapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_OK) {
            //httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + SQID + "&bid=" + "bbs" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            viewpagerFragmentAdapter.notifyDataSetChanged();
        } else if (resultCode == LOGIN_SUCCESS) {
            IS_LOGIN = spn.getBoolean(SPN_IS_LOGIN, false);
            Intent intent = new Intent();
            intent.setClass(ShzlBBSActivity.this, ShzlBbsPostActivity.class);
            startActivityForResult(intent, LOAD_SUCCESS);
        }else if (resultCode == WEIDU_BBS && requestCode == WEIDU_BBS){
            mMenu.findItem(R.id.weidu).setVisible(false);
            buer = true;
        }
    }

}
