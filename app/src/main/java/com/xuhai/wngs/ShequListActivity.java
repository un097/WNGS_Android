package com.xuhai.wngs;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.adapters.main.ShequListAdapter;
import com.xuhai.wngs.beans.main.ShequListBean;
import com.xuhai.wngs.push.Utils;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;


public class ShequListActivity extends BaseActionBarAsUpActivity {
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    public static final String TAG = "ShequListActivity";
    public static final String REQUEST_TAG = "request_tag";
    private int p = 1;
    public Context context;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();
    private String latitude, longitude;
    public List<ShequListBean> shequList;
    private List<ShequListBean> totalshequList;
    private ShequListAdapter shequListAdapter;
    private boolean buer;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private String key, mykey,cityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shequ_list);

        cityCode = getIntent().getStringExtra("cityCode");

        editor.putInt(SPN_PAGE_COUNT, 20);
        editor.commit();

        context = this;

        if (getActionBar() != null) {
            getActionBar().setTitle(R.string.title_activity_shequ_list);
            if (spn.getBoolean(SPN_IS_FIRST_OPEN, true)) {
                getActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        initView();

        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
        // mLocationClient.setAccessKey("8mrnaFzKu3DoduLnWuB5Lt2w"); //V4.1
        // mLocationClient.setAK("8mrnaFzKu3DoduLnWuB5Lt2w"); //V4.0
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        setLocationOption();
        mLocationClient.start();// 开始定位

    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_REFRESH:
                    buer = false;
                    shequListAdapter = new ShequListAdapter(ShequListActivity.this, totalshequList);

                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(shequListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    shequListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:

                    shequListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:
                    buer = true;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    shequListAdapter = new ShequListAdapter(ShequListActivity.this, totalshequList);

                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(shequListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    shequListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
            }
            return false;
        }
    });

    private class listitemclick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (!SQID.equals(totalshequList.get(position).getSqid())) {
                database.delete("shopcart", null, null);
            }
//            List<String> list = new ArrayList<String>();
//            for (int i = 0; i < shequList.size(); i++) {
//                list.add(shequList.get(i).getSqid());
//            }
            List<String> tags = Utils.getTagsList(SQID);
            PushManager.delTags(ShequListActivity.this, tags);

            try {
//                Log.d("sqi1=====",AESEncryptor.encrypt(shequList.get(position).getSqid()));
                editor.putString(SPN_SQID, AESEncryptor.encrypt(totalshequList.get(position).getSqid()));
                editor.putString(SPN_SQNAME, AESEncryptor.encrypt(totalshequList.get(position).getShequ()));
                editor.putString(SPN_SQIMG, AESEncryptor.encrypt(totalshequList.get(position).getImg()));
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }


            IS_SELECT_SHEQU = spn.getBoolean(SPN_IS_SELECT_SHEQU, false);

            if (IS_SELECT_SHEQU) {
                editor.remove(SPN_AUTH);
                editor.remove(SPN_AUTH_NAME);
                editor.remove(SPN_AUTH_ROOM);
                editor.remove(SPN_AUTH_UNIT);
                editor.remove(SPN_AUTH_PHONE);
                editor.remove(SPN_AUTH_BUILDING);
                editor.commit();
                setResult(RESULT_OK);
                finish();
            } else {
                editor.putBoolean(SPN_IS_SELECT_SHEQU, true);
                editor.commit();
                setResult(RESULT_OK);
                finish();
                Intent intent = new Intent(ShequListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void initView() {

        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
        pullRefreshLayout.setRefreshing(true);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.setOnItemClickListener(null);
                p = 1;


                httpRequest(LOAD_REFRESH, HTTP_SHEQU_LIST + "?latitude=" + latitude + "&longitude=" + longitude + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT) + "&cityCode=" + cityCode);
//                mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
//                // mLocationClient.setAccessKey("8mrnaFzKu3DoduLnWuB5Lt2w"); //V4.1
//                // mLocationClient.setAK("8mrnaFzKu3DoduLnWuB5Lt2w"); //V4.0
//                mLocationClient.registerLocationListener(myListener); // 注册监听函数
//                setLocationOption();
//                mLocationClient.start();// 开始定位

            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());

                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        if (shequListAdapter!=null) {
                            int lastIndex = shequListAdapter.getCount() - 1;//数据集最后一项的索引
//                Log.d(TAG, "lastIndex === " + lastIndex);
//                Log.d(TAG, "visibleLastIndex === " + visibleLastIndex);
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                                //如果是自动加载,可以在这里放置异步加载数据的代码
                                if (isLoadMore) {

                                    Log.d("load==","here");
                                    p += 1;
                                    if (key == null || key.equals("")) {
                                        httpRequest(LOAD_MORE, HTTP_SHEQU_LIST + "?latitude=" + latitude + "&longitude=" + longitude + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT) + "&cityCode=" + cityCode);
                                    } else {
                                        httpRequest(LOAD_MORE, HTTP_SHEQU_LIST + "?latitude=" + latitude + "&longitude=" + longitude + "&key=" + key + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT) + "&cityCode=" + cityCode);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.d(TAG, "firstVisibleItem === " + firstVisibleItem);
//                Log.d(TAG, "visibleItemCount === " + visibleItemCount);


                        ShequListActivity.this.visibleItemCount = visibleItemCount;
                        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
                    }
                });
    }

    private void httpRequest(final int loadstate, String url) {
        shequList = new ArrayList<ShequListBean>();
        if (loadstate == LOAD_REFRESH || loadstate == LOAD_FAIL) {
            totalshequList = new ArrayList<ShequListBean>();
        }

        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                Gson gson = new Gson();
                                shequList = gson.fromJson(list.toString(), new TypeToken<List<ShequListBean>>() {
                                }.getType());
                                if (shequList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalshequList.addAll(shequList);
                            }

                            handler.sendEmptyMessage(loadstate);

                        } else {
                            CustomToast.showToast(ShequListActivity.this, msg, Toast.LENGTH_SHORT);
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        CustomToast.showToast(ShequListActivity.this, R.string.http_fail, 1000);
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(ShequListActivity.this, R.string.http_fail, 1000);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(ShequListActivity.this, R.string.http_fail, 1000);
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
            }
        });

//        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "response ==== " + response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//
//        };
        request.setTag(REQUEST_TAG);
        queue.add(request);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mLocationClient.stop();// 停止定位
    }

    /**
     * 设置相关参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(100000);// 设置发起定位请求的间隔时间为5000ms

        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            latitude = String.valueOf(location.getLatitude());

            longitude = String.valueOf(location.getLongitude());

            httpRequest(LOAD_REFRESH, HTTP_SHEQU_LIST + "?latitude=" + latitude + "&longitude=" + longitude + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT) + "&cityCode=" + cityCode);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        queue.cancelAll(new RequestQueue.RequestFilter() {
//            @Override
//            public boolean apply(Request<?> request) {
//                return false;
//            }
//        });
        queue.cancelAll(REQUEST_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shequ_list, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mykey = query;
                try {
                    key = URLEncoder.encode(mykey, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (key == null || key.equals("")) {

                } else {
                    httpRequest(LOAD_FAIL, HTTP_SHEQU_LIST + "?latitude=" + latitude + "&longitude=" + longitude + "&key=" + key  + "&cityCode=" + cityCode);
                }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (buer) {
                httpRequest(LOAD_REFRESH, HTTP_SHEQU_LIST + "?latitude=" + latitude + "&longitude=" + longitude + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT) + "&cityCode=" + cityCode);
            } else {
                finish();

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (buer) {
                httpRequest(LOAD_REFRESH, HTTP_SHEQU_LIST + "?latitude=" + latitude + "&longitude=" + longitude + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT) + "&cityCode=" + cityCode);
            } else {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
