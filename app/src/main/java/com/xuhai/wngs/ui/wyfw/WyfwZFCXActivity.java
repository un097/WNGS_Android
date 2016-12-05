package com.xuhai.wngs.ui.wyfw;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Message;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.wyfw.WyfwZFCXListAdapter;
import com.xuhai.wngs.beans.wyfw.WyfwZFCXListBean;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WyfwZFCXActivity extends BaseActionBarAsUpActivity {
    public static final String TAG = "WyfwZFCXActivity";
    private int p = 1;
    private TextView area, addr, nickname;
    private String m_area, m_addr, m_nickname, m_image;
    private CircleImageView image;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<WyfwZFCXListBean> zfcxBeanList;
    private List<WyfwZFCXListBean> totalZfcxList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;

    private WyfwZFCXListAdapter zfcxListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyfw_zfcx);
        initView();

        httpRequest(LOAD_REFRESH, HTTP_ZFCX_LIST + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));

    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    area.setText(m_area);
                    addr.setText(m_addr);
                    nickname.setText(m_nickname);
                    if (m_image == null || m_image.equals("")) {
                        image.setImageResource(R.drawable.head_qiuzhenxiang_public);
                    } else {
                        PicassoTrustAll.getInstance(WyfwZFCXActivity.this).load(m_image).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(image);
                    }
                    zfcxListAdapter = new WyfwZFCXListAdapter(WyfwZFCXActivity.this, totalZfcxList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(zfcxListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    zfcxListAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    zfcxListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:

                    break;
            }
            return false;
        }
    });

    private void initView() {

        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
        pullRefreshLayout.setRefreshing(true);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                p = 1;
                httpRequest(LOAD_REFRESH, HTTP_ZFCX_LIST + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        LayoutInflater inflater = LayoutInflater.from(WyfwZFCXActivity.this);
        View layout = inflater.inflate(R.layout.item_list__wyfw_zfxc_header, null);
        area = (TextView) layout.findViewById(R.id.area);
        addr = (TextView) layout.findViewById(R.id.addr);
        nickname = (TextView) layout.findViewById(R.id.nickname);
        image = (CircleImageView) layout.findViewById(R.id.image);
        listView.addHeaderView(layout);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Intent intent = new Intent();
//                intent.setClass(WyfwZFCXActivity.this, WebActivity.class);
//                intent.putExtra("url",totalZfcxList.get(position).getUrl());
//                intent.putExtra("title","便民信息");
//                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (zfcxListAdapter!=null) {
                    int lastIndex = zfcxListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_ZFCX_LIST + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                WyfwZFCXActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

    private void httpRequest(final int loadstate, String url) {
        zfcxBeanList = new ArrayList<WyfwZFCXListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalZfcxList = new ArrayList<WyfwZFCXListBean>();
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
                            if (response.has("head")) {
                                m_image = response.getString("head");
                            }
                            if (response.has("nickname")) {
                                m_nickname = response.getString("nickname");
                            }
                            if (response.has("addr")) {
                                m_addr = response.getString("addr");
                            }
                            if (response.has("head")) {
                                m_area = response.getString("area");
                            }
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                Gson gson = new Gson();
                                zfcxBeanList = gson.fromJson(list.toString(), new TypeToken<List<WyfwZFCXListBean>>() {
                                }.getType());
                                if (zfcxBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalZfcxList.addAll(zfcxBeanList);
                            }
                            if (totalZfcxList.size()==0){
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(WyfwZFCXActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(WyfwZFCXActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(WyfwZFCXActivity.this, R.string.http_fail, 1000);
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(WyfwZFCXActivity.this, R.string.http_fail, 1000);
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(WyfwZFCXActivity.this, R.string.http_fail, 1000);
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(WyfwZFCXActivity.this, "zfcx");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wyfw_zfcx, menu);
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
