package com.xuhai.wngs.ui.more;

import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.more.MoreJFDHListAdapter;
import com.xuhai.wngs.beans.more.MoreJFDHListBean;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreJFDHActivity extends BaseActionBarAsUpActivity {

    public static final String TAG = "MoreJFDHActivity";

    private int p = 1;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<MoreJFDHListBean> jfdhBeanList;
    private List<MoreJFDHListBean> totalJfdhList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;

    private MoreJFDHListAdapter jfdhListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_jfdh);

        initView();
        httpRequest(LOAD_REFRESH, HTTP_JFDH_LIST + "?sqid=" + SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }



    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    jfdhListAdapter = new MoreJFDHListAdapter(MoreJFDHActivity.this, totalJfdhList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(jfdhListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    jfdhListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    jfdhListAdapter.notifyDataSetChanged();
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
            intent.setClass(MoreJFDHActivity.this, MoreJfdhItemActivity.class);

            intent.putExtra("poiid", totalJfdhList.get(position).getPoiid());
            startActivityForResult(intent, JIFEN);
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
                httpRequest(LOAD_REFRESH, HTTP_JFDH_LIST + "?sqid=" + SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());

                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        if (jfdhListAdapter!=null) {
                            int lastIndex = jfdhListAdapter.getCount() - 1;//数据集最后一项的索引
                            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                                //如果是自动加载,可以在这里放置异步加载数据的代码
                                if (isLoadMore) {
                                    p += 1;
                                    httpRequest(LOAD_MORE, HTTP_JFDH_LIST + "?sqid=" + SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                                }
                            }
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        MoreJFDHActivity.this.visibleItemCount = visibleItemCount;
                        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
                    }
                });

    }

    private void httpRequest(final int loadstate, String url) {
        jfdhBeanList = new ArrayList<MoreJFDHListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalJfdhList = new ArrayList<MoreJFDHListBean>();
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
                                jfdhBeanList = gson.fromJson(list.toString(), new TypeToken<List<MoreJFDHListBean>>() {
                                }.getType());
                                if (jfdhBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalJfdhList.addAll(jfdhBeanList);
                            }

                            handler.sendEmptyMessage(loadstate);

                        } else {
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(MoreJFDHActivity.this, msg, 1000);
                        }
                    } else {
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                        CustomToast.showToast(MoreJFDHActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    CustomToast.showToast(MoreJFDHActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
                CustomToast.showToast(MoreJFDHActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == JIFEN){
            setResult(JIFEN);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_jfdh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
