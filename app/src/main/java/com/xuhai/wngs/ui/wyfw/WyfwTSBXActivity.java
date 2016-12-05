package com.xuhai.wngs.ui.wyfw;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import com.xuhai.wngs.BaseActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.wyfw.WyfwXWYSListAdapter;
import com.xuhai.wngs.adapters.wyfw.WyfwZXGGListAdapter;
import com.xuhai.wngs.beans.wyfw.WyfwXWYSBean;
import com.xuhai.wngs.beans.wyfw.WyfwZXGGBean;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WyfwTSBXActivity extends BaseActionBarAsUpActivity {

    public static final String TAG = "WyfwTSBXActivity";

    private int p = 1;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;

    private List<WyfwXWYSBean> xwysBeanList;
    private List<WyfwXWYSBean> totalXwysList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private WyfwXWYSListAdapter xwysListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyfw_tsbx);

        initView();

        httpRequest(LOAD_REFRESH, HTTP_REPAIR_LIST + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));

    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    xwysListAdapter = new WyfwXWYSListAdapter(WyfwTSBXActivity.this, totalXwysList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(xwysListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    xwysListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    xwysListAdapter.notifyDataSetChanged();
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
            intent.setClass(WyfwTSBXActivity.this, WebActivity.class);
            intent.putExtra("title","向物业说");
            intent.putExtra("url",totalXwysList.get(position).getUrl());
            startActivity(intent);
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
                httpRequest(LOAD_REFRESH, HTTP_REPAIR_LIST + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (xwysListAdapter!=null) {
                    int lastIndex = xwysListAdapter.getCount() - 1;//数据集最后一项的索引

//                Log.d(TAG, "lastIndex === " + lastIndex);
//                Log.d(TAG, "visibleLastIndex === " + visibleLastIndex);
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_REPAIR_LIST + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                WyfwTSBXActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }


    private void httpRequest(final int loadstate, String url) {
        listView.setOnItemClickListener(null);
        xwysBeanList = new ArrayList<WyfwXWYSBean>();
        if (loadstate == LOAD_REFRESH) {
            totalXwysList = new ArrayList<WyfwXWYSBean>();
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
                                xwysBeanList = gson.fromJson(list.toString(), new TypeToken<List<WyfwXWYSBean>>() {
                                }.getType());
                                if (xwysBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalXwysList.addAll(xwysBeanList);

                            }
                            if (totalXwysList.size()==0){
                                listView.setOnItemClickListener(new listitemclick());
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(WyfwTSBXActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }
                        } else {
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(WyfwTSBXActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(WyfwTSBXActivity.this, R.string.http_fail, 1000);
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(WyfwTSBXActivity.this, R.string.http_fail, 1000);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(WyfwTSBXActivity.this, R.string.http_fail, 1000);
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(WyfwTSBXActivity.this, "xwys");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wyfw_tsbx, menu);
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
                break;
            case R.id.tsbx:
                Intent intent=new Intent(WyfwTSBXActivity.this,WyfwTsbxPostActivity.class);
                startActivityForResult(intent,LOAD_SUCCESS);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            httpRequest(LOAD_REFRESH, HTTP_REPAIR_LIST + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
