package com.xuhai.wngs.ui.more;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.more.MoreJFDDListAdapter;
import com.xuhai.wngs.adapters.more.MoreWDKDListAdapter;
import com.xuhai.wngs.beans.more.MoreJFDDListBean;
import com.xuhai.wngs.beans.more.MoreWDKDListBean;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreWDKDActivity extends BaseActionBarAsUpActivity {
    public static final String TAG = "MoreJFDDActivity";
    private int p = 1;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private List<MoreWDKDListBean> wdkdBeanList;
    private List<MoreWDKDListBean> totalWdkdList;
    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private MoreWDKDListAdapter wdkdListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_wdkd);
        initView();

        httpRequest(LOAD_REFRESH,  HTTP_WDKD_LIST + "?phone=" + USER_PHONE + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    wdkdListAdapter = new MoreWDKDListAdapter(MoreWDKDActivity.this, totalWdkdList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(wdkdListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    wdkdListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    wdkdListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:

                    break;
            }
            return false;
        }
    });

    private class listitemclick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(MoreWDKDActivity.this, MoreWdkdItemActivity.class);
            intent.putExtra("exid",totalWdkdList.get(position).getExid());
            startActivityForResult(intent,LOAD_REFRESH);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_REFRESH && resultCode==RESULT_OK){
            httpRequest(LOAD_REFRESH,  HTTP_WDKD_LIST + "?phone=" + USER_PHONE + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
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
                httpRequest(LOAD_REFRESH,  HTTP_WDKD_LIST + "?phone=" + USER_PHONE + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (wdkdListAdapter!=null) {
                    int lastIndex = wdkdListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_WDKD_LIST + "?phone=" + USER_PHONE + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                MoreWDKDActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

    private void httpRequest(final int loadstate, String url) {
        wdkdBeanList = new ArrayList<MoreWDKDListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalWdkdList = new ArrayList<MoreWDKDListBean>();
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
                                wdkdBeanList = gson.fromJson(list.toString(), new TypeToken<List<MoreWDKDListBean>>() {
                                }.getType());
                                if (wdkdBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalWdkdList.addAll(wdkdBeanList);
                            }

                            if (totalWdkdList.size()==0){
                                listView.setOnItemClickListener(new listitemclick());
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(MoreWDKDActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(MoreWDKDActivity.this, msg, 1000);
                        }
                    } else {
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                        CustomToast.showToast(MoreWDKDActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    CustomToast.showToast(MoreWDKDActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
                CustomToast.showToast(MoreWDKDActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(MoreWDKDActivity.this, "wnkd");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_wdkd, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(11);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        setResult(11);
        finish();
        super.onBackPressed();
    }
}

