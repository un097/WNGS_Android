package com.xuhai.wngs.ui.more;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
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
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.more.MoreWDDDListAdapter;
import com.xuhai.wngs.beans.more.MoreWDDDListBean;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreWDDDActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "MoreWDDDActivity";

    private int p = 1;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<MoreWDDDListBean> wdddBeanList;
    private List<MoreWDDDListBean> totalWdddList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;

    private MoreWDDDListAdapter wdddListAdapter;
    private String flag;
    private PopupWindow popupWindow;
    private LinearLayout ll1,ll2,ll3,ll4,ll5;
    private ImageView imageView,imageView2,imageView3,imageView4,imageView5;
    private TextView titleView;
    private ImageView wddd_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_wddd);
        if (getActionBar() != null) {
            //  getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowCustomEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(false);

            getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.actionbar_title_center_click, null), new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));
            titleView = (TextView) getActionBar().getCustomView().findViewById(R.id.title);
            titleView.setText("全部订单");
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
            titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wddd_image.setVisibility(View.VISIBLE);
                    titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoxia, 0);
                    titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                    popupWindow.showAsDropDown(getActionBar().getCustomView());
                    popupWindow.update();
                }
            });
        }
        initView();
        popuWindow();
        flag="0";
        httpRequest(LOAD_REFRESH, HTTP_WDDD_LIST + "?uid=" + UID + "&flag=" + flag +  "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }

    private void popuWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.activity_popup_dingdan, null);

        imageView=(ImageView)layout.findViewById(R.id.yes1);
        imageView2=(ImageView)layout.findViewById(R.id.yes2);
        imageView3=(ImageView)layout.findViewById(R.id.yes3);
        imageView4=(ImageView)layout.findViewById(R.id.yes4);
    //    imageView5=(ImageView)layout.findViewById(R.id.yes5);
        ll1=(LinearLayout)layout.findViewById(R.id.ll1);
        ll2=(LinearLayout)layout.findViewById(R.id.ll2);
        ll3=(LinearLayout)layout.findViewById(R.id.ll3);
        ll4=(LinearLayout)layout.findViewById(R.id.ll4);
       // ll5=(LinearLayout)layout.findViewById(R.id.ll5);
        ll1.setOnClickListener(this);
        ll2.setOnClickListener(this);
        ll3.setOnClickListener(this);
        ll4.setOnClickListener(this);
     //   ll5.setOnClickListener(this);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

//        popupWindow.setAnimationStyle(R.style.AnimTop);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//        WindowManager.LayoutParams lp=getWindow().getAttributes();
//        lp.alpha=0.7f;
//        getWindow().setAttributes(lp);
                wddd_image.setVisibility(View.GONE);
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
            }
        });
    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 4:
                    wdddListAdapter = new MoreWDDDListAdapter(MoreWDDDActivity.this, totalWdddList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(wdddListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    wdddListAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_REFRESH:
                    wdddListAdapter = new MoreWDDDListAdapter(MoreWDDDActivity.this, totalWdddList);

                    SwingBottomInAnimationAdapter sswingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(wdddListAdapter);
                    sswingBottomInAnimationAdapter.setAbsListView(listView);

                    assert sswingBottomInAnimationAdapter.getViewAnimator() != null;
                    sswingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(sswingBottomInAnimationAdapter);

                    wdddListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    wdddListAdapter.notifyDataSetChanged();
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
            intent.setClass(MoreWDDDActivity.this, MoreWdddItemActivity.class);
            intent.putExtra("orderid",totalWdddList.get(position).getOrderid());
            startActivityForResult(intent,LOAD_SUCCESS);
        }
    }

    private void initView() {
        wddd_image=(ImageView)findViewById(R.id.wddd_image);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
        pullRefreshLayout.setRefreshing(true);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.setOnItemClickListener(null);
                p = 1;
                httpRequest(LOAD_REFRESH, HTTP_WDDD_LIST + "?uid=" + UID + "&flag=" + flag +  "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new listitemclick());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (wdddListAdapter!=null) {
                    int lastIndex = wdddListAdapter.getCount() - 1;//数据集最后一项的索引
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_WDDD_LIST + "?uid=" + UID + "&flag=" + flag + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                MoreWDDDActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }

    private void httpRequest(final int loadstate, String url) {
        wdddBeanList = new ArrayList<MoreWDDDListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalWdddList = new ArrayList<MoreWDDDListBean>();
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
                                wdddBeanList = gson.fromJson(list.toString(), new TypeToken<List<MoreWDDDListBean>>() {
                                }.getType());
                                if (wdddBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalWdddList.addAll(wdddBeanList);
                            }

                            if (totalWdddList.size()==0){
                                listView.setOnItemClickListener(new listitemclick());
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(MoreWDDDActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(MoreWDDDActivity.this,msg,1000);
                            handler.sendEmptyMessage(4);
                        }
                    } else {
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                        CustomToast.showToast(MoreWDDDActivity.this,R.string.http_fail,1000);
                    }
                } catch (Exception e) {
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    CustomToast.showToast(MoreWDDDActivity.this,R.string.http_fail,1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
                CustomToast.showToast(MoreWDDDActivity.this,R.string.http_fail,1000);
            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_wddd, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll1:
                flag = "0";
                titleView.setText("全部订单");
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                imageView.setVisibility(View.VISIBLE);
                imageView3.setVisibility(View.GONE);
              //  imageView5.setVisibility(View.GONE);
                imageView4.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                popupWindow.dismiss();
                break;
            case R.id.ll2:
                titleView.setText("已完成订单");
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                flag = "3";
                imageView.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
                imageView4.setVisibility(View.GONE);
         //       imageView5.setVisibility(View.GONE);
                imageView2.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
                break;
            case R.id.ll3:
                titleView.setText("配送中订单");
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                flag = "2";
                imageView.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                imageView4.setVisibility(View.GONE);
         //       imageView5.setVisibility(View.GONE);
                imageView3.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
                break;
            case R.id.ll4:
                titleView.setText("已接收订单");
                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
                flag = "1";
                imageView.setVisibility(View.GONE);
                imageView2.setVisibility(View.GONE);
                imageView3.setVisibility(View.GONE);
         //       imageView5.setVisibility(View.GONE);
                imageView4.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
                break;
//            case R.id.ll5:
//                titleView.setText("退款订单");
//                titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.image_more_public_chaoshang, 0);
//                titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));
//                flag = "6";
//                imageView.setVisibility(View.GONE);
//                imageView2.setVisibility(View.GONE);
//                imageView3.setVisibility(View.GONE);
//                imageView5.setVisibility(View.VISIBLE);
//                imageView4.setVisibility(View.GONE);
//                popupWindow.dismiss();
//                break;
        }
        httpRequest(LOAD_REFRESH, HTTP_WDDD_LIST + "?uid=" + UID + "&flag=" + flag +  "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==LOAD_SUCCESS){
            if (resultCode==RESULT_OK){
                httpRequest(LOAD_REFRESH, HTTP_WDDD_LIST + "?uid=" + UID + "&flag=" + flag +  "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        }
    }
}
