package com.xuhai.wngs.ui.wyfw;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.wyfw.WyfwBMFWListAdapter;
import com.xuhai.wngs.beans.wyfw.WyfwBMFWBean;
import com.xuhai.wngs.ui.main.MainFragment;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wr on 2015/1/28.
 */
public class WyfwFragment extends Fragment implements Constants {
    public static final int LOAD_SUCCESS = 0, LOAD_FAIL = -1, LOAD_REFRESH = 1, LOAD_MORE = 2;
    public static final String TAG = "WyfwBMFWActivity";
    public RequestQueue queue;
    public boolean isLoadMore = false; //是否加载更多
    private int p = 1;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<WyfwBMFWBean> bmfwBeanList;
    private List<WyfwBMFWBean> totalBmfwList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;

    private WyfwBMFWListAdapter bmfwListAdapter;
    private ImageView bmxx, ycfw;
    private boolean buer = true, youchang = true, xx = false;
    private int index;
    private View view;

    // TODO: Rename and change types and number of parameters
    public static WyfwFragment newInstance() {
        WyfwFragment fragment = new WyfwFragment();
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
        view = inflater.inflate(R.layout.fragment_wyfw, container, false);
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
            httpRequest(LOAD_REFRESH, HTTP_CONVENIENCE_LIST + "?sqid=" + ((WyfwBMFWActivity) getActivity()).SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
        } else if (index == 1) {
            httpRequest(LOAD_REFRESH, HTTP_PHONE_LIST + "?sqid=" + ((WyfwBMFWActivity) getActivity()).SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
        }
    }

    private void initButton() {
        bmxx = (ImageView) view.findViewById(R.id.bmxx);
        ycfw = (ImageView) view.findViewById(R.id.ycfw);
        if (index == 0) {
            ycfw.setImageResource(R.drawable.bar_ycwf_on);
            bmxx.setImageResource(R.drawable.bar_bmxx_off);
        } else if (index == 1) {
            ycfw.setImageResource(R.drawable.bar_ycfw_off);
            bmxx.setImageResource(R.drawable.bar_bmxx_on);
        }
        bmxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    ((WyfwBMFWActivity) getActivity()).setPagerItem(1);
                } else if (index == 1) {
                    return;
                }
            }
        });
        ycfw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    return;
                } else if (index == 1) {
                    ((WyfwBMFWActivity) getActivity()).setPagerItem(0);
                }
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
                    bmfwListAdapter = new WyfwBMFWListAdapter(getActivity(), totalBmfwList);
                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bmfwListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    bmfwListAdapter.notifyDataSetChanged();

                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    bmfwListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:
                    bmfwListAdapter = new WyfwBMFWListAdapter(getActivity(), totalBmfwList);
                    listView.setAdapter(bmfwListAdapter);
                    bmfwListAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setRefreshing(false);
                    break;
            }
            return false;
        }
    });


    private class listitemclick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra("url", totalBmfwList.get(position).getUrl());
            if (index == 0) {
                intent.putExtra("title", "有偿服务");
            } else {
                intent.putExtra("title", "便民信息");
            }
            startActivity(intent);
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
                if (index == 0) {
                    httpRequest(LOAD_REFRESH, HTTP_CONVENIENCE_LIST + "?sqid=" + ((WyfwBMFWActivity) getActivity()).SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                } else if (index == 1) {
                    httpRequest(LOAD_REFRESH, HTTP_PHONE_LIST + "?sqid=" + ((WyfwBMFWActivity) getActivity()).SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                }
            }
        });

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (bmfwListAdapter!=null) {
                    int lastIndex = bmfwListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            if (index == 0) {
                                httpRequest(LOAD_MORE, HTTP_CONVENIENCE_LIST + "?sqid=" + ((WyfwBMFWActivity) getActivity()).SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                            } else if (index == 1) {
                                httpRequest(LOAD_MORE, HTTP_PHONE_LIST + "?sqid=" + ((WyfwBMFWActivity) getActivity()).SQID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(20));
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }

        });

    }


    private void httpRequest(final int loadstate, String url) {
        bmfwBeanList = new ArrayList<WyfwBMFWBean>();
        if (loadstate == LOAD_REFRESH) {
            totalBmfwList = new ArrayList<WyfwBMFWBean>();
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
                                bmfwBeanList = gson.fromJson(list.toString(), new TypeToken<List<WyfwBMFWBean>>() {
                                }.getType());
                                if (bmfwBeanList.size() < 20) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalBmfwList.addAll(bmfwBeanList);
                            }

                            handler.sendEmptyMessage(loadstate);

                        } else {
                            pullRefreshLayout.setRefreshing(false);
                            listView.setOnItemClickListener(new listitemclick());
                            CustomToast.showToast(getActivity(), msg, 1000);
                            handler.sendEmptyMessage(LOAD_FAIL);
                        }
                    } else {
                        CustomToast.showToast(getActivity(), R.string.http_fail, 1000);
                        pullRefreshLayout.setRefreshing(false);
                        listView.setOnItemClickListener(new listitemclick());
                    }
                } catch (Exception e) {
                    CustomToast.showToast(getActivity(), R.string.http_fail, 1000);
                    pullRefreshLayout.setRefreshing(false);

                    listView.setOnItemClickListener(new listitemclick());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(getActivity(), R.string.http_fail, 1000);
                pullRefreshLayout.setRefreshing(false);
                listView.setOnItemClickListener(new listitemclick());
            }
        });
        queue.add(request);
    }
}
