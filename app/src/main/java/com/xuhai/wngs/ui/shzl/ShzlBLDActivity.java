package com.xuhai.wngs.ui.shzl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.pager.ImagePagerAdapter;
import com.xuhai.wngs.adapters.shzl.BLDhotAdapter;
import com.xuhai.wngs.adapters.shzl.BLDdlAdapter;
import com.xuhai.wngs.adapters.shzl.BLDlistAdapter;
import com.xuhai.wngs.beans.shzl.ShzlBldBannerBean;
import com.xuhai.wngs.beans.shzl.ShzlBldDailyBean;
import com.xuhai.wngs.beans.shzl.ShzlBldHotBean;
import com.xuhai.wngs.beans.shzl.ShzlBldListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.MyGridView;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class ShzlBLDActivity extends BaseActionBarAsUpActivity {
    private ProgressDialogFragment newFragment;
    private HorizontalScrollView horizontalScrollView;
    private BLDdlAdapter blDdlAdapter;
    private BLDlistAdapter blDlistAdapter;
    private BLDhotAdapter bldHotAdapter;
    private ImageView[] imageViews = null;
    private ImageView imageView = null;
    private AutoScrollViewPager advPager = null;
    private ImagePagerAdapter imagePagerAdapter;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
    private List<ShzlBldListBean> bldBeanList;
    private List<ShzlBldBannerBean> bldbannerList;
    private List<ShzlBldDailyBean> blddailyList;
    private List<ShzlBldHotBean> bldhotList;
    ScrollView scrollView;
    private LinearLayout mGallery;
    private RelativeLayout rl_adpager;
    private LayoutInflater mInflater;
    private ImageView img;
    private String m_storeid, m_storename, m_img, m_star, m_sales, m_comments, m_begintime, m_endtime;
    private SearchManager searchManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bld);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        initADView();

        httpRequest(HTTP_BLD + "?sqid=" + SQID);
    }

    private void initADView () {
        rl_adpager = (RelativeLayout) findViewById(R.id.rl_adpager);
        advPager = (AutoScrollViewPager) findViewById(R.id.adv_pager);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_adpager.getLayoutParams();
        params.height = screenWidth * 250 / 640;
        rl_adpager.setLayoutParams(params);
    }

    private void httpRequest(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");
        bldBeanList = new ArrayList<ShzlBldListBean>();
        bldbannerList = new ArrayList<ShzlBldBannerBean>();
        blddailyList = new ArrayList<ShzlBldDailyBean>();
        bldhotList = new ArrayList<ShzlBldHotBean>();
        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            if (response.has("storeid")) {
                                m_storeid = response.getString("storeid");
                                editor.putString(SPN_STOREID, m_storeid);
                                editor.commit();
                            }
                            if (response.has("storename")) {
                                m_storename = response.getString("storename");
                            }
                            if (response.has("sales")) {
                                m_sales = response.getString("sales");
                            }
                            if (response.has("comments")) {
                                m_comments = response.getString("comments");
                            }
                            if (response.has("star")) {
                                m_star = response.getString("star");
                            }
                            if (response.has("img")) {
                                m_img = response.getString("img");
                            }
                            if (response.has("begintime")) {
                                //     m_begintime=response.getString("begintime");
                                editor.putString(SPN_START_TIME, response.getString("begintime"));
                            }
                            if (response.has("endtime")) {
                                //   m_endtime=response.getString("endtime");
                                editor.putString(SPN_END_TIME, response.getString("endtime"));
                            }
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject json = list.getJSONObject(i);
                                    ShzlBldListBean bean = new ShzlBldListBean();
                                    if (json.has("classid")) {
                                        bean.setClassid(json.getString("classid"));
                                    }
                                    if (json.has("classimg")) {
                                        bean.setClassimg(json.getString("classimg"));
                                    }
                                    if (json.has("class")) {
                                        bean.setClasss(json.getString("class"));
                                    }
                                    bldBeanList.add(bean);
                                }
                            }
                            if (response.has("banner")) {
                                JSONArray list = response.getJSONArray("banner");
                                Gson gson = new Gson();
                                bldbannerList = gson.fromJson(list.toString(), new TypeToken<List<ShzlBldBannerBean>>() {
                                }.getType());
                            }
                            if (response.has("daily")) {
                                JSONArray list = response.getJSONArray("daily");
                                Gson gson = new Gson();
                                blddailyList = gson.fromJson(list.toString(), new TypeToken<List<ShzlBldDailyBean>>() {
                                }.getType());
                            }
                            if (response.has("hot")) {
                                JSONArray list = response.getJSONArray("hot");
                                Gson gson = new Gson();
                                bldhotList = gson.fromJson(list.toString(), new TypeToken<List<ShzlBldHotBean>>() {
                                }.getType());
                            }
                            editor.commit();
                            handler.sendEmptyMessage(LOAD_SUCCESS);
                            newFragment.dismiss();

                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(ShzlBLDActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(ShzlBLDActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(ShzlBLDActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(ShzlBLDActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    setBanner();
                    setHot();
                    setDaily();
                    setList();

                    scrollView.setVisibility(View.VISIBLE);
                    scrollView.smoothScrollTo(0, 20);

                    break;
            }
            return false;
        }
    });

    private void setList() {

        GridView gv_list = (GridView) findViewById(R.id.gv_list);
        gv_list.setSelector(new ColorDrawable(Color.TRANSPARENT));
        blDlistAdapter = new BLDlistAdapter(getApplicationContext(), (ArrayList<ShzlBldListBean>) bldBeanList);
        gv_list.setAdapter(blDlistAdapter);
//        gv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(ShzlBLDActivity.this, ShzlBldCPLBActivity.class);
//                intent.putExtra("classid", bldBeanList.get(position).getClassid());
//                intent.putExtra("class", bldBeanList.get(position).getClasss());
//                intent.putExtra("storeid", m_storeid);
//                startActivity(intent);
//            }
//        });
    }

    private void setDaily() {
        mGallery = (LinearLayout) findViewById(R.id.id_gallery);
        mInflater = LayoutInflater.from(ShzlBLDActivity.this);
        for (int i = 0; i < blddailyList.size(); i++) {
            View view = mInflater.inflate(R.layout.item_gv_wyfw_bld_daily,
                    mGallery, false);
            img = (ImageView) view
                    .findViewById(R.id.img);
            PicassoTrustAll.getInstance(ShzlBLDActivity.this).load(blddailyList.get(i).getGoodsimg()).into(img);
            TextView txt = (TextView) view
                    .findViewById(R.id.text);
            txt.setText(getResources().getString(R.string.yang) + " " + blddailyList.get(i).getPrice());
            mGallery.addView(view);

            final int finalI = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(ShzlBLDActivity.this, ShzlBldSPXQActivity.class);
                    intent.putExtra("storeid", m_storeid);
                    intent.putExtra("goodsid", blddailyList.get(finalI).getGoodsid());
                    startActivity(intent);
                }
            });
        }

    }

    private void setHot() {
        GridView gv_list = (MyGridView) findViewById(R.id.gv_hot);
        gv_list.setSelector(new ColorDrawable(Color.TRANSPARENT));
        bldHotAdapter = new BLDhotAdapter(getApplicationContext(), (ArrayList<ShzlBldHotBean>) bldhotList);
        gv_list.setAdapter(bldHotAdapter);
        gv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(ShzlBLDActivity.this, ShzlBldSPXQActivity.class);
                intent.putExtra("storeid", m_storeid);
                intent.putExtra("goodsid", bldhotList.get(position).getGoodsid());
                startActivity(intent);
            }
        });
    }

    private void setBanner() {

        imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), bldbannerList);

        advPager.setAdapter(imagePagerAdapter);

        advPager.setInterval(3000);
        advPager.startAutoScroll();
    }

    public void onPagerClick(int position) {
//        Intent intent = new Intent(ShzlBLDActivity.this, ShzlBldCPLBActivity.class);
//        intent.putExtra("storeid", m_storeid);
//        intent.putExtra("banner_word", bldbannerList.get(position).getWord());
//        intent.putExtra("classid", bldbannerList.get(position).getClassid());
//        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shzl_bld, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm.isActive()) {
//                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                }
                searchView.setIconified(true);

//                Intent intent = new Intent(ShzlBLDActivity.this, ShzlBldCPLBActivity.class);
//                intent.putExtra("storeid", m_storeid);
//                intent.putExtra("word", query);
//                startActivityForResult(intent, LOAD_SUCCESS);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOAD_SUCCESS && resultCode == RESULT_OK) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        ShzlBLDActivity.this.setResult(LOGIN_SUCCESS);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                ShzlBLDActivity.this.setResult(LOGIN_SUCCESS);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        advPager.stopAutoScroll();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // start auto scroll when onResume

        MobclickAgent.onEvent(ShzlBLDActivity.this, "xnxd");

        advPager.startAutoScroll();
        if (imagePagerAdapter != null) {
            imagePagerAdapter.notifyDataSetChanged();
        }
    }

}
