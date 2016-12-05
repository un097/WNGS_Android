package com.xuhai.wngs.ui.wyfw;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.wyfw.WyfwHDListAdapter;

import com.xuhai.wngs.adapters.wyfw.WyfwXWYSListAdapter;
import com.xuhai.wngs.beans.wyfw.WyfwHDBean;
import com.xuhai.wngs.ui.more.LoginActivity;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.views.CustomToast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WyfwHDActivity extends BaseActionBarAsUpActivity {

    public static final String TAG = "WyfwHDActivity";

    private int p = 1;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;

    private List<WyfwHDBean> hdBeanList;
    private List<WyfwHDBean> totalHdList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private WyfwHDListAdapter hdListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyfw_hd);

        initView();
        httpRequest(LOAD_REFRESH, HTTP_HD_LIST + "?sqid=" + SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));

    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    hdListAdapter = new WyfwHDListAdapter(WyfwHDActivity.this, totalHdList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(hdListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    hdListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    hdListAdapter.notifyDataSetChanged();
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
            if (totalHdList.get(position).getExpired().equals("1")){
                return;
            }
            else
            if (totalHdList.get(position).getIslogin().equals("1")){
                if (IS_LOGIN){
                    if (totalHdList.get(position).getIscert().equals("1")){
                        if (IS_AUTH){
                            Intent intent = new Intent();
                            intent.setClass(WyfwHDActivity.this, WebActivity.class);
                            intent.putExtra("title","社区活动");
                            intent.putExtra("url",totalHdList.get(position).getUrl() + "&sqid=" + SQID +"&uid=" + UID);
                            startActivity(intent);
                        }else {
                            CustomToast.showToast(WyfwHDActivity.this, "请先认证！", 1000);
                        }
                    }else {
                        Intent intent = new Intent();
                        intent.setClass(WyfwHDActivity.this, WebActivity.class);
                        intent.putExtra("title","社区活动");
                        intent.putExtra("url",totalHdList.get(position).getUrl() + "&sqid=" + SQID +"&uid=" + UID);
                        startActivity(intent);
                    }
                }else {
                    Intent intent = new Intent();
                    intent.setClass(WyfwHDActivity.this, LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
//                    else {
//                        if (IS_LOGIN) {
//                            Intent intent = new Intent(context, WyfwTSBXActivity.class);
//                            startActivity(intent);
//                        } else {
//                             Intent intent = new Intent(context, LoginActivity.class);
//                            startActivityForResult(intent, Constants.STATE_LOGIN);
//                        }
            }
            else {
                Intent intent = new Intent();
                intent.setClass(WyfwHDActivity.this, WebActivity.class);
                intent.putExtra("title","活动");
                intent.putExtra("url",totalHdList.get(position).getUrl() + "&sqid=" + SQID +"&uid=" + UID);
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
                httpRequest(LOAD_REFRESH, HTTP_HD_LIST + "?sqid=" + SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (hdListAdapter!=null) {
                    int lastIndex = hdListAdapter.getCount() - 1;//数据集最后一项的索引

//                Log.d(TAG, "lastIndex === " + lastIndex);
//                Log.d(TAG, "visibleLastIndex === " + visibleLastIndex);
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_HD_LIST + "?sqid=" + SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                WyfwHDActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }


    private void httpRequest(final int loadstate, String url) {
        hdBeanList = new ArrayList<WyfwHDBean>();
        if (loadstate == LOAD_REFRESH) {
            totalHdList = new ArrayList<WyfwHDBean>();
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
                                hdBeanList = gson.fromJson(list.toString(), new TypeToken<List<WyfwHDBean>>() {
                                }.getType());
                                if (hdBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalHdList.addAll(hdBeanList);
                            }
                            if (totalHdList.size()==0){
                                listView.setOnItemClickListener(new listitemclick());
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(WyfwHDActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(WyfwHDActivity.this,msg,1000);
                        }
                    } else {
                        CustomToast.showToast(WyfwHDActivity.this, R.string.http_fail, 1000);
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(WyfwHDActivity.this, R.string.http_fail, 1000);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(WyfwHDActivity.this, R.string.http_fail, 1000);
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_SUCCESS || resultCode == RESULT_OK){
            IS_LOGIN = spn.getBoolean(SPN_IS_LOGIN, false);
            IS_AUTH = spn.getBoolean(SPN_AUTH,false);
            try {
                UID = AESEncryptor.decrypt(spn.getString(SPN_UID,""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onBackPressed() {
        WyfwHDActivity.this.setResult(LOGIN_SUCCESS);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(WyfwHDActivity.this, "huodong");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wyfw_hd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                WyfwHDActivity.this.setResult(LOGIN_SUCCESS);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
