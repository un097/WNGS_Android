package com.xuhai.wngs.ui.sjfw;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.sjfw.SjfwStoreClassGridAdapter;
import com.xuhai.wngs.adapters.sjfw.SjfwStoreGridAdapter;
import com.xuhai.wngs.adapters.wyfw.WyfwZXGGListAdapter;
import com.xuhai.wngs.beans.sjfw.SjfwClassBean;
import com.xuhai.wngs.beans.sjfw.SjfwSplistBean;
import com.xuhai.wngs.ui.shzl.ShzlBldSPXQActivity;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SjfwStoreActivity extends BaseActionBarAsUpActivity {
    private PopupWindow popupWindow;
    private RelativeLayout layout_class,layout_hot;
    private GridView gv_class;
    private SjfwStoreClassGridAdapter classGridAdapter;

    private int p = 1;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;
    private PullRefreshLayout pullRefreshLayout;
    private GridView gv_store;
    private SjfwStoreGridAdapter storeGridAdapter;

//    private TextView tv_number,tv_price;
    private ImageView iv_detail;
    private List<SjfwClassBean> classBeanList;
    private List<SjfwSplistBean> splistBeanList;
    private List<SjfwSplistBean> totalBeanList;

    private String classid = "0";

//    private int allnumber = 0;
//    private double allprice = 0.00;

    private String weburl;

    private View line_hot,line_class;

    private SearchManager searchManager;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfw_store);
        initView();
        initPop();
//        dbdate();
        httpRequest(LOAD_REFRESH,HTTP_BLD + "?classid=" + classid + "&spid=" + getIntent().getStringExtra("spid"));
    }

    private void initView(){

        iv_detail = (ImageView) findViewById(R.id.iv_detail);
        line_hot = (View) findViewById(R.id.line_hot);
        line_class = (View) findViewById(R.id.line_class);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
        pullRefreshLayout.setRefreshing(true);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                p = 1;
                httpRequest(LOAD_REFRESH, HTTP_BLD + "?classid=" + classid + "&spid=" + getIntent().getStringExtra("spid"));
                //刷新
            }
        });

        iv_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SjfwStoreActivity.this, WebActivity.class);
                intent.putExtra("url", weburl);
                intent.putExtra("title", "商家");
                startActivity(intent);
            }
        });

        gv_store = (GridView) findViewById(R.id.gv_store);
        gv_store.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(SjfwStoreActivity.this, ShzlBldSPXQActivity.class);
                intent.putExtra("goodsid", totalBeanList.get(position).getGoodsid());
                intent.putExtra("storeid",getIntent().getStringExtra("spid"));
//                intent.putExtra("goodsimg", totalCPLBList.get(position).getGoodsimg());
                startActivityForResult(intent,1);
            }
        });
        gv_store.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (storeGridAdapter != null) {
                    int lastIndex = storeGridAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_BLD + "?classid=" + classid + "&spid=" + getIntent().getStringExtra("spid") + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                SjfwStoreActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });



        layout_class = (RelativeLayout) findViewById(R.id.layout_class);
        layout_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopw(view);
            }
        });
        layout_hot = (RelativeLayout)findViewById(R.id.layout_hot);
        layout_hot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                line_hot.setVisibility(View.VISIBLE);
                line_class.setVisibility(View.GONE);
                classid = "0";
                httpRequest(LOAD_REFRESH,HTTP_BLD + "?classid=" + classid + "&spid=" + getIntent().getStringExtra("spid"));
            }
        });
    }


    private PopupWindow showPopw(View v){
        if (popupWindow == null){
            initPop();
        }
//        popupWindow.showAsDropDown(v);
        popupWindow.showAsDropDown(v, 0, 2);
        return popupWindow;
    }


    private void initPop(){
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_sjfw_store_class, null);
         popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setAnimationStyle();
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (popupWindow != null & popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                return false;
            }
        });
        gv_class = (GridView) contentView.findViewById(R.id.gv_class);

        gv_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                line_hot.setVisibility(View.GONE);
                line_class.setVisibility(View.VISIBLE);
                classid = classBeanList.get(i).getClassid();
                httpRequest(LOAD_REFRESH,HTTP_BLD + "?classid=" + classid + "&spid=" + getIntent().getStringExtra("spid"));

                popupWindow.dismiss();
                classGridAdapter.refreshChoo(i);
            }
        });
    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

//                    tv_number.setText(String.valueOf(allnumber));
//                    tv_price.setText(String.valueOf(allprice));

                    break;
                case LOAD_REFRESH:
                    storeGridAdapter = new SjfwStoreGridAdapter(SjfwStoreActivity.this, totalBeanList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(storeGridAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(gv_store);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    gv_store.setAdapter(swingBottomInAnimationAdapter);

                    storeGridAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setRefreshing(false);


                    classGridAdapter = new SjfwStoreClassGridAdapter(SjfwStoreActivity.this,classBeanList);
                    gv_class.setAdapter(classGridAdapter);

                    break;
                case LOAD_MORE:
                    storeGridAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:

                    Log.d("listsize====",totalBeanList.size() + "");
                    storeGridAdapter = new SjfwStoreGridAdapter(SjfwStoreActivity.this, totalBeanList);
                    gv_store.setAdapter(storeGridAdapter);

                    break;
            }
            return false;
        }
    });



    private void httpRequest(final int loadstate, final String url) {
        classBeanList = new ArrayList<SjfwClassBean>();
        splistBeanList = new ArrayList<SjfwSplistBean>();
        if (loadstate == LOAD_REFRESH) {
            totalBeanList = new ArrayList<SjfwSplistBean>();
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
                            if (response.has("url")){
                                weburl = response.getString("url");
                            }
                            if (response.has("begintime")) {
                                editor.putString(SPN_START_TIME,  AESEncryptor.encrypt(response.getString("begintime")));
                            }
                            if (response.has("endtime")) {
                                editor.putString(SPN_END_TIME,  AESEncryptor.encrypt(response.getString("endtime")));
                            }
                            editor.commit();
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                Gson gson = new Gson();
                                classBeanList = gson.fromJson(list.toString(), new TypeToken<List<SjfwClassBean>>() {
                                }.getType());
                            }

                            if (response.has("splist")){
                                JSONArray splist = response.getJSONArray("splist");
                                Gson gson = new Gson();
                                splistBeanList = gson.fromJson(splist.toString(), new TypeToken<List<SjfwSplistBean>>() {
                                }.getType());
                                if (splistBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalBeanList.addAll(splistBeanList);
                            }

                            handler.sendEmptyMessage(loadstate);
                        } else {
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(SjfwStoreActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(SjfwStoreActivity.this, R.string.http_fail, 1000);
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(SjfwStoreActivity.this, R.string.http_fail, 1000);
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(SjfwStoreActivity.this, R.string.http_fail, 1000);
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }



    private void httpSearch(final int loadstate, final String url) {
        splistBeanList = new ArrayList<SjfwSplistBean>();
        if (loadstate == LOAD_FAIL) {
            totalBeanList = new ArrayList<SjfwSplistBean>();
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

                            if (response.has("list")){
                                JSONArray splist = response.getJSONArray("list");
                                Gson gson = new Gson();
                                splistBeanList = gson.fromJson(splist.toString(), new TypeToken<List<SjfwSplistBean>>() {
                                }.getType());
                                if (splistBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalBeanList.addAll(splistBeanList);
                            }

                            handler.sendEmptyMessage(loadstate);
                        } else {
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(SjfwStoreActivity.this, msg, 1000);
                            handler.sendEmptyMessage(LOAD_FAIL);
                        }
                    } else {
                        CustomToast.showToast(SjfwStoreActivity.this, R.string.http_fail, 1000);
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(SjfwStoreActivity.this, R.string.http_fail, 1000);
                    pullRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(SjfwStoreActivity.this, R.string.http_fail, 1000);
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }


    @Override
    protected void onDestroy() {
        database.delete("shopcart", null, null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sjfw_store, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconified(true);


                try {
                    httpSearch(LOAD_FAIL,HTTP_BLD_CPLB + "?sqid=" + SQID + "&storeid=" + getIntent().getStringExtra("spid") + "&classid=0&word=" + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
