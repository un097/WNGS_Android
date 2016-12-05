package com.xuhai.wngs.ui.shzl;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.android.volley.toolbox.MultiPartStack;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.shzl.ShzlBBSListAdapter;
import com.xuhai.wngs.beans.shzl.ShzlBBSListBean;
import com.xuhai.wngs.ui.more.LoginActivity;
import com.xuhai.wngs.ui.wyfw.WyfwBMFWActivity;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wr on 2015/1/28.
 */
public class BBSFragment extends Fragment implements Constants {
    public static final String TAG = "ShzlBBSActivity";
    public static final String REQUEST_TAG = "request_tag";

    private String bid;
    private int position;
    private int p = 1;
    private Button newxx;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private int lastVisibleItemPosition = 0;// 标记上次滑动位置
    private boolean scrollFlag = false;// 标记是否滑动

    private List<ShzlBBSListBean> bbsListBeanList;
    private List<ShzlBBSListBean> bbsTotalList;
    private ShzlBBSListAdapter bbsListAdapter;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private FloatingActionButton fab;
    public RequestQueue queue;
    public boolean isLoadMore = false; //是否加载更多
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int index;
    private View view;

    // TODO: Rename and change types and number of parameters
    public static BBSFragment newInstance() {
        BBSFragment fragment = new BBSFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt("index");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bbs, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(getActivity(), new MultiPartStack());
        queue.start();
        //initButton();
        initView();
        if (index == 0) {
            httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + ((ShzlBBSActivity)getActivity()).SQID + "&uid=" + ((ShzlBBSActivity)getActivity()).UID + "&bid=" + "bbs" + "&tag=" + "0" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
        } else if (index == 1) {
            httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + ((ShzlBBSActivity)getActivity()).SQID + "&uid=" + ((ShzlBBSActivity)getActivity()).UID + "&bid=" + "business" + "&tag=" + "0" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
        }
    }

    private class listitemclick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), ShzlBBSContentActivity.class);
            intent.putExtra("bbs_id", bbsTotalList.get(position).getId());
            if (index==0) {
                intent.putExtra("bid", "bbs");
                intent.putExtra("title", "邻里圈");
            } else if (index==1){
                intent.putExtra("bid", "business");
                intent.putExtra("title", "邻商圈");
            }
            startActivityForResult(intent, LOAD_SUCCESS);
        }
    }

    private void initView() {

        pullRefreshLayout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);
        pullRefreshLayout.setRefreshing(true);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.setOnItemClickListener(null);
                p = 1;
                if (index==0) {
                    httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + ((ShzlBBSActivity)getActivity()).SQID + "&uid=" + ((ShzlBBSActivity)getActivity()).UID + "&bid=" + "bbs" + "&tag=" + "0" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                } else if (index==1){
                    httpRequest(LOAD_REFRESH, HTTP_BBS_LIST + "?sqid=" + ((ShzlBBSActivity)getActivity()).SQID + "&uid=" + ((ShzlBBSActivity)getActivity()).UID + "&bid=" + "business" + "&tag=" + "0" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                }

            }
        });

        fab = (FloatingActionButton)view. findViewById(R.id.fab);
//        fab.attachToListView(listView);
//        fab.attachToRecyclerView(mRecyclerView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ShzlBBSActivity)getActivity()).IS_LOGIN) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ShzlBbsPostActivity.class);
                    if (index==0) {
                        intent.putExtra("bid", "bbs");
                        intent.putExtra("title", "邻里圈");
                    } else if (index==1){
                        intent.putExtra("bid", "business");
                        intent.putExtra("title", "邻商圈");
                    }
                    startActivityForResult(intent, LOAD_SUCCESS);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }

            }
        });

        listView = (ListView)view. findViewById(R.id.listView);
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
                            pullRefreshLayout.setRefreshing(true);
                            if (index == 0) {
                                httpRequest(LOAD_MORE, HTTP_BBS_LIST + "?sqid=" + ((ShzlBBSActivity) getActivity()).SQID + "&uid=" + ((ShzlBBSActivity)getActivity()).UID + "&bid=" + "bbs" + "&tag=" + "0" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                            } else if (index == 1) {
                                httpRequest(LOAD_MORE, HTTP_BBS_LIST + "?sqid=" + ((ShzlBBSActivity) getActivity()).SQID + "&uid=" + ((ShzlBBSActivity)getActivity()).UID + "&bid=" + "business" + "&tag=" + "0" + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                            }
                        }

                    }
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        scrollFlag = true;
                    } else {
                        scrollFlag = false;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;

                if (firstVisibleItem > lastVisibleItemPosition) {
                    fab.hide(true);
                }
                if (firstVisibleItem < lastVisibleItemPosition) {
                    fab.show(true);
                }

                lastVisibleItemPosition = firstVisibleItem;
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_REFRESH:
                    bbsListAdapter = new ShzlBBSListAdapter(getActivity(), bbsTotalList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bbsListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    bbsListAdapter.notifyDataSetChanged();

//                    mAdapter = new ShzlBBSRecyclerAdapter(ShzlBBSActivity.this, bbsTotalList);
//
//                    mRecyclerView.setAdapter(mAdapter);
//
//                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    pullRefreshLayout.setRefreshing(false);
                    bbsListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:
                    bbsListAdapter = new ShzlBBSListAdapter(getActivity(), bbsTotalList);
                    listView.setAdapter(bbsListAdapter);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    bbsListAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });

    private void httpRequest(final int loadstate, final String url) {
        bbsListBeanList = new ArrayList<ShzlBBSListBean>();
        if (loadstate == LOAD_REFRESH) {
            bbsTotalList = new ArrayList<ShzlBBSListBean>();
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
                                bbsListBeanList = gson.fromJson(list.toString(), new TypeToken<List<ShzlBBSListBean>>() {
                                }.getType());
                                if (bbsListBeanList.size() < 20) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                bbsTotalList.addAll(bbsListBeanList);
                            }

                            if (bbsTotalList.size() == 0) {
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(getActivity(), "暂无数据", 1000);
                                handler.sendEmptyMessage(LOAD_FAIL);
                            } else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                            if (index==0){
                                CustomToast.showToast(getActivity(), msg, 1000);
                            }else if (index==1 && ((ShzlBBSActivity) getActivity()).getCurrentItem() == 1){
                                CustomToast.showToast(getActivity(), msg, 1000);
                            }
                            handler.sendEmptyMessage(LOAD_FAIL);
                        }
                    } else {
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                        CustomToast.showToast(getActivity(), R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    CustomToast.showToast(getActivity(), R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
                CustomToast.showToast(getActivity(), R.string.http_fail, 1000);
            }
        });
        request.setTag(REQUEST_TAG);
        queue.add(request);
    }

    @Override
    public void onStop() {
        queue.cancelAll(REQUEST_TAG);
        super.onStop();
    }

}
