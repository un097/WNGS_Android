package com.xuhai.wngs.ui.sjfw;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.sjfw.SjfwPublicListAdapter;
import com.xuhai.wngs.adapters.wyfw.WyfwBMFWListAdapter;
import com.xuhai.wngs.beans.sjfw.SjfwPublicListBean;
import com.xuhai.wngs.beans.wyfw.WyfwBMFWBean;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SjfwPublicListActivity extends BaseActionBarAsUpActivity {
    private String funcid, title;
    public static final String TAG = "WyfwBMFWActivity";

    private int p = 1;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<SjfwPublicListBean> sjfwBeanList;
    private List<SjfwPublicListBean> totalSjfwList;

    private List<SjfwPublicListBean> oldBeanList;
    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;

    private SjfwPublicListAdapter PublicListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfw_public_list);
        //接pid
        funcid = getIntent().getStringExtra("funcid");
        title = getIntent().getStringExtra("title");
        if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
        initView();

        httpRequest(LOAD_REFRESH, HTTP_LIVE_LIST + "?sqid=" + SQID + "&funcid=" + funcid + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    PublicListAdapter = new SjfwPublicListAdapter(SjfwPublicListActivity.this, totalSjfwList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(PublicListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    PublicListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    PublicListAdapter.notifyDataSetChanged();
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
            if (totalSjfwList.get(position).getIssell().equals("1")){
                Intent intent = new Intent(SjfwPublicListActivity.this,SjfwStoreActivity.class);
                intent.putExtra("spid",totalSjfwList.get(position).getStoreid());
                startActivity(intent);
            }
           else {
                Intent intent = new Intent();
                intent.setClass(SjfwPublicListActivity.this, WebActivity.class);
                intent.putExtra("url", totalSjfwList.get(position).getUrl());
                intent.putExtra("title", title);
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
                httpRequest(LOAD_REFRESH, HTTP_LIVE_LIST + "?sqid=" + SQID + "&funcid=" + funcid + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (PublicListAdapter!=null) {
                    int lastIndex = PublicListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_LIVE_LIST + "?sqid=" + SQID + "&funcid=" + funcid + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                SjfwPublicListActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

    private void httpRequest(final int loadstate, String url) {
        sjfwBeanList = new ArrayList<SjfwPublicListBean>();
        oldBeanList = new ArrayList<SjfwPublicListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalSjfwList = new ArrayList<SjfwPublicListBean>();
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
                                sjfwBeanList = gson.fromJson(list.toString(), new TypeToken<List<SjfwPublicListBean>>() {
                                }.getType());
                                totalSjfwList.addAll(sjfwBeanList);
                            }
                            if (response.has("oldlist")){
                                JSONArray oldlist = response.getJSONArray("oldlist");
                                Gson gson = new Gson();
                                oldBeanList = gson.fromJson(oldlist.toString(), new TypeToken<List<SjfwPublicListBean>>() {
                                }.getType());
                                totalSjfwList.addAll(oldBeanList);
                            }

                            if (sjfwBeanList.size() < PAGE_COUNT & oldBeanList.size() < PAGE_COUNT) {
                                isLoadMore = false;
                            } else {
                                isLoadMore = true;
                            }

                            if (totalSjfwList.size()==0){
                                listView.setOnItemClickListener(new listitemclick());
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(SjfwPublicListActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(SjfwPublicListActivity.this,msg,1000);
                        }
                    } else {
                        CustomToast.showToast(SjfwPublicListActivity.this, R.string.http_fail, 1000);
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(SjfwPublicListActivity.this, R.string.http_fail, 1000);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(SjfwPublicListActivity.this, R.string.http_fail, 1000);
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(SjfwPublicListActivity.this, "sjfw");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sjfw_public_list, menu);
        return true;
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
