package com.xuhai.wngs.ui.shzl;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.main.ShequListAdapter;
import com.xuhai.wngs.adapters.shzl.ShzlBBSListAdapter;
import com.xuhai.wngs.adapters.wyfw.WyfwZXGGListAdapter;
import com.xuhai.wngs.beans.main.ShequListBean;
import com.xuhai.wngs.beans.shzl.ShzlBBSListBean;
import com.xuhai.wngs.beans.wyfw.WyfwZXGGBean;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShzlBbsMsgActivity extends BaseActionBarAsUpActivity {
    public static final String TAG = "ShzlBbsMsgActivity";

    private int p = 1;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;

    private List<ShzlBBSListBean> BbsMsgBeanList;
    private List<ShzlBBSListBean> totalBbsMsgList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private ShzlBBSListAdapter bbsListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bbs_msg);
        initView();

        httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + SQID + "&uid=" + UID + "&bid=" + "bbs" + "&tag=" + "1" +
                "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));

    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    bbsListAdapter = new ShzlBBSListAdapter(ShzlBbsMsgActivity.this, BbsMsgBeanList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bbsListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    bbsListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    bbsListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:

                    break;
            }
            return false;
        }
    });

    private class listitemclick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.setClass(ShzlBbsMsgActivity.this, ShzlBBSContentActivity.class);
            intent.putExtra("bid", totalBbsMsgList.get(position).getBid());
            intent.putExtra("bbs_id", totalBbsMsgList.get(position).getId());
            intent.putExtra("title","未读消息");
            startActivityForResult(intent, WEIDU_BBS);
        }
    }
private boolean buer = false;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == WEIDU_BBS){
             buer = true;
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
                httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + SQID + "&uid=" + UID + "&bid=" + "bbs" + "&tag=" + "1" +
                        "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (bbsListAdapter!=null) {
                    int lastIndex = bbsListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_BBS_LIST + "?sqid=" + SQID + "&uid=" + UID + "&bid=" + "bbs" + "&tag=" + "1" +
                                    "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                ShzlBbsMsgActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }


    private void httpRequest(final int loadstate, String url) {
        BbsMsgBeanList = new ArrayList<ShzlBBSListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalBbsMsgList = new ArrayList<ShzlBBSListBean>();
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
                                BbsMsgBeanList = gson.fromJson(list.toString(), new TypeToken<List<ShzlBBSListBean>>() {
                                }.getType());
                                if (BbsMsgBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalBbsMsgList.addAll(BbsMsgBeanList);
                            }
                            if (totalBbsMsgList.size() == 0) {
                                listView.setOnItemClickListener(new listitemclick());
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(ShzlBbsMsgActivity.this, "暂无数据", 1000);
                            } else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(ShzlBbsMsgActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(ShzlBbsMsgActivity.this, R.string.http_fail, 1000);
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(ShzlBbsMsgActivity.this, R.string.http_fail, 1000);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(ShzlBbsMsgActivity.this, R.string.http_fail, 1000);
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.wyfw_zxgg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (buer) {
                    setResult(WEIDU_BBS);
                }else {

                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (buer){
        setResult(WEIDU_BBS);}
        else {

        }
        finish();
        super.onBackPressed();
    }
}
