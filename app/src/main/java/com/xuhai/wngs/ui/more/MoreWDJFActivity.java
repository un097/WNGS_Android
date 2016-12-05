package com.xuhai.wngs.ui.more;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.xuhai.wngs.R;
import com.xuhai.wngs.ShequActivity;
import com.xuhai.wngs.adapters.more.MoreWDJFListAdapter;
import com.xuhai.wngs.adapters.wyfw.WyfwZFCXListAdapter;
import com.xuhai.wngs.beans.more.MoreWDJFListBean;
import com.xuhai.wngs.beans.wyfw.WyfwZFCXListBean;
import com.xuhai.wngs.ui.wyfw.WyfwTsbxPostActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreWDJFActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "MoreWDJFActivity";
    private int p = 1;
    private TextView tv_total;
    private String  total;
    private CircleImageView image;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<MoreWDJFListBean> wdjfBeanList;
    private List<MoreWDJFListBean> totalWdjfList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;

    private MoreWDJFListAdapter wdjfListAdapter;
private TextView titleView;
    private LinearLayout layout_ll1,layout_ll2,layout_ll3;
    private ImageView imageView,imageView2,imageView3,wdjf_image;
    private PopupWindow popupWindow;
    private String tag;
    private boolean buer = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_wdjf);
        if (getActionBar() != null) {
           // getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayShowCustomEnabled(true);

            getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.actionbar_title_center_click, null), new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));
            titleView = (TextView) getActionBar().getCustomView().findViewById(R.id.title);
            titleView.setText("全部积分");
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
            titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wdjf_image.setVisibility(View.VISIBLE);
                    titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoxia, 0);
                    titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                    popupWindow.showAsDropDown(getActionBar().getCustomView());
                    popupWindow.update();
                }
            });
        }
        initView();
        popupWindow();
        tag="0";
        httpRequest(LOAD_REFRESH, HTTP_WDJF_LIST + "?uid=" + UID + "&tag=" + tag + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }

    private void popupWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.activity_popup_jifen, null);
        imageView=(ImageView)layout.findViewById(R.id.yes1);
        imageView2=(ImageView)layout.findViewById(R.id.yes2);
        imageView3=(ImageView)layout.findViewById(R.id.yes3);
        layout_ll1=(LinearLayout)layout.findViewById(R.id.layout_ll1);
        layout_ll2=(LinearLayout)layout.findViewById(R.id.layout_ll2);
        layout_ll3=(LinearLayout)layout.findViewById(R.id.layout_ll3);
        layout_ll1.setOnClickListener(this);
        layout_ll2.setOnClickListener(this);
        layout_ll3.setOnClickListener(this);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        // popupWindow.setAnimationStyle(R.style.AnimTop);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wdjf_image.setVisibility(View.GONE);
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
            }
        });
    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    tv_total.setText(total);
                    if (USER_HEAD == null || USER_HEAD.equals("")) {
                        image.setImageResource(R.drawable.ic_huisewoniu);
                    } else {
                        PicassoTrustAll.getInstance(MoreWDJFActivity.this).load(USER_HEAD).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(image);
                    }

                    wdjfListAdapter = new MoreWDJFListAdapter(MoreWDJFActivity.this, totalWdjfList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(wdjfListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    wdjfListAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    wdjfListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:
                    wdjfListAdapter = new MoreWDJFListAdapter(MoreWDJFActivity.this, totalWdjfList);
                    listView.setAdapter(wdjfListAdapter);
                    wdjfListAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setRefreshing(false);
                    break;
            }
            return false;
        }
    });


    private void initView() {
        wdjf_image=(ImageView)findViewById(R.id.wdjf_image);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
        pullRefreshLayout.setRefreshing(true);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                p = 1;
                httpRequest(LOAD_REFRESH, HTTP_WDJF_LIST + "?uid=" + UID + "&tag=" + tag + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        LayoutInflater inflater = LayoutInflater.from(MoreWDJFActivity.this);
        View layout = inflater.inflate(R.layout.item_list_more_wdjf_header, null);
        tv_total = (TextView) layout.findViewById(R.id.tv_total);
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
                if (wdjfListAdapter!=null) {
                    int lastIndex = wdjfListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_WDJF_LIST + "?uid=" + UID + "&tag=" + tag + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                MoreWDJFActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 2;
            }
        });

    }

    private void httpRequest(final int loadstate, String url) {
        wdjfBeanList = new ArrayList<MoreWDJFListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalWdjfList = new ArrayList<MoreWDJFListBean>();
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
                            if (response.has("total")) {
                                total = response.getString("total");
                            }

                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                Gson gson = new Gson();
                                wdjfBeanList = gson.fromJson(list.toString(), new TypeToken<List<MoreWDJFListBean>>() {
                                }.getType());
                                if (wdjfBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalWdjfList.addAll(wdjfBeanList);
                            }

                            if (totalWdjfList.size()==0){
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(MoreWDJFActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(MoreWDJFActivity.this, msg, 1000);
                            handler.sendEmptyMessage(LOAD_FAIL);
                        }
                    } else {
                        pullRefreshLayout.setRefreshing(false);
                        CustomToast.showToast(MoreWDJFActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    pullRefreshLayout.setRefreshing(false);
                    CustomToast.showToast(MoreWDJFActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pullRefreshLayout.setRefreshing(false);
                CustomToast.showToast(MoreWDJFActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_wdj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if (buer){
                    setResult(LOGIN_SUCCESS);
                    finish();
                }else {
                    finish();
                }
                break;
            case R.id.jfdh:
                Intent intent=new Intent(MoreWDJFActivity.this,MoreJFDHActivity.class);
                startActivityForResult(intent, JIFEN);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (buer){
                setResult(LOGIN_SUCCESS);
                finish();
            }else {
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == JIFEN){
            httpRequest(LOAD_REFRESH, HTTP_WDJF_LIST + "?uid=" + UID + "&tag=" + tag + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            buer = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_ll1:
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                imageView.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                popupWindow.dismiss();
                tag="0";
                titleView.setText("全部积分");
                break;
            case R.id.layout_ll2:
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                imageView.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                imageView2.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
                tag="1";
                titleView.setText("获得积分");
                break;
            case R.id.layout_ll3:
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                imageView.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                imageView3.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
                tag="2";
                titleView.setText("支出积分");
                break;
        }

        httpRequest(LOAD_REFRESH, HTTP_WDJF_LIST + "?uid=" + UID + "&tag=" + tag + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }
}
