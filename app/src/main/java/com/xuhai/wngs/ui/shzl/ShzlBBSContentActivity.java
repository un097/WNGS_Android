package com.xuhai.wngs.ui.shzl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.ui.more.LoginActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.shzl.ShzlBBSContentListAdapter;
import com.xuhai.wngs.beans.shzl.ShzlBBSContentListBean;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShzlBBSContentActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "ShzlBBSContentActivity";
    private int p = 1;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<ShzlBBSContentListBean> bbsContentBeanList;
    private List<ShzlBBSContentListBean> totalBbsContentList;
    private String bbs_id, bid;
    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private CircleImageView touxiang;
    private ImageView img1, img2, img3, content_huifu;
    private TextView nickname, pinglunshu, date, title, content;
    private String bbs_nickname, bbs_pinglunshu, bbs_date, bbs_title, bbs_content, bbs_head, bbs_img1, bbs_img2, bbs_img3;
    private ShzlBBSContentListAdapter bbsContentListAdapter;
    private RelativeLayout layout_all;
    private ProgressDialogFragment newFragment;
    private ArrayList<String> imgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bbscontent);
        if (getActionBar()!=null){
            getActionBar().setTitle(getIntent().getStringExtra("title"));
        }
        initView();
        bbs_id = getIntent().getStringExtra("bbs_id");
        bid=getIntent().getStringExtra("bid");
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        httpRequest(LOAD_REFRESH, HTTP_BBS_CONTENT + "?sqid=" + SQID + "&uid=" + UID + "&bbs_id=" + bbs_id + "&bid=" + bid);

    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    setBbsContent();
                    bbsContentListAdapter = new ShzlBBSContentListAdapter(ShzlBBSContentActivity.this, totalBbsContentList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bbsContentListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    bbsContentListAdapter.notifyDataSetChanged();
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    bbsContentListAdapter.notifyDataSetChanged();
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
                httpRequest(LOAD_REFRESH, HTTP_BBS_CONTENT + "?sqid=" + SQID + "&uid=" + UID + "&bbs_id=" + bbs_id + "&bid=" + bid);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = LayoutInflater.from(ShzlBBSContentActivity.this);
        View layout = inflater.inflate(R.layout.item_list_shzl_bbs_content_header, null);
        nickname = (TextView) layout.findViewById(R.id.content_nickname);
        pinglunshu = (TextView) layout.findViewById(R.id.content_pinglunshu);
        date = (TextView) layout.findViewById(R.id.content_date);
        img1 = (ImageView) layout.findViewById(R.id.img1);
        img2 = (ImageView) layout.findViewById(R.id.img2);
        img3 = (ImageView) layout.findViewById(R.id.img3);
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        content_huifu = (ImageView) findViewById(R.id.content_huifu);
        content_huifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_LOGIN) {
                    Intent intent = new Intent(ShzlBBSContentActivity.this, ShzlBBSHFActivity.class);
                    intent.putExtra("bbs_id", bbs_id);
                    intent.putExtra("tag", "reply");
                    intent.putExtra("bid", bid);
                    intent.putExtra("title","回复"+bbs_nickname);
                    startActivityForResult(intent, LOAD_SUCCESS);
                } else {
                    Intent intent = new Intent(ShzlBBSContentActivity.this, LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
            }
        });
        title = (TextView) layout.findViewById(R.id.content_title);
        content = (TextView) layout.findViewById(R.id.content_content);
        touxiang = (CircleImageView) layout.findViewById(R.id.content_touxiang);
        listView.addHeaderView(layout);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShzlBBSContentActivity.this, ShzlBBSHFActivity.class);
                intent.putExtra("tag", "reply_reply");
                intent.putExtra("bbs_id", bbs_id);
                intent.putExtra("bid", bid);
                intent.putExtra("title", "回复"+totalBbsContentList.get(position-1).getReply_nickname());
                intent.putExtra("reply_other_id", totalBbsContentList.get(position-1).getReply_uid());
                startActivityForResult(intent, LOAD_SUCCESS);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (bbsContentListAdapter!=null) {
                    int lastIndex = bbsContentListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_BBS_CONTENT + "?sqid=" + SQID + "&uid=" + UID + "&bbs_id=" + bbs_id + "&bid=" + bid);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                ShzlBBSContentActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }


    public void setBbsContent() {
        layout_all = (RelativeLayout) findViewById(R.id.layout_all);
        layout_all.setVisibility(View.VISIBLE);
        if (bbs_nickname == null || bbs_nickname.equals("")) {
            nickname.setText("火星网友");
        } else {
            nickname.setText(bbs_nickname);
        }
        pinglunshu.setText(bbs_pinglunshu + "回复");
        date.setText(bbs_date);
        title.setText(bbs_title);
        content.setText(bbs_content);
        if (bbs_head == null || bbs_head.equals("")) {
            touxiang.setImageResource(R.drawable.head_qiuzhenxiang_public);
        } else {
            PicassoTrustAll.getInstance(ShzlBBSContentActivity.this).load(bbs_head).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(touxiang);
        }
        if (imgList.size() == 1) {
            if (bbs_img1 == null || bbs_img1.equals("")) {

            } else {
                img1.setVisibility(View.VISIBLE);
                PicassoTrustAll.getInstance(ShzlBBSContentActivity.this).load(bbs_img1).into(img1);
            }
        } else if (imgList.size() == 2) {
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.VISIBLE);
            PicassoTrustAll.getInstance(ShzlBBSContentActivity.this).load(bbs_img1).into(img1);
            PicassoTrustAll.getInstance(ShzlBBSContentActivity.this).load(bbs_img2).into(img2);
        } else if (imgList.size() == 3) {
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.VISIBLE);
            img3.setVisibility(View.VISIBLE);
            PicassoTrustAll.getInstance(ShzlBBSContentActivity.this).load(bbs_img1).into(img1);
            PicassoTrustAll.getInstance(ShzlBBSContentActivity.this).load(bbs_img2).into(img2);
            PicassoTrustAll.getInstance(ShzlBBSContentActivity.this).load(bbs_img3).into(img3);
        }
    }

    private void httpRequest(final int loadstate, String url) {
        bbsContentBeanList = new ArrayList<ShzlBBSContentListBean>();
        if (loadstate == LOAD_REFRESH) {
            imgList = new ArrayList<String>();
            totalBbsContentList = new ArrayList<ShzlBBSContentListBean>();
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
                                bbs_head = response.getString("head");
                            }
                            if (response.has("title")) {
                                bbs_title = response.getString("title");

                            }
                            if (response.has("nickname")) {
                                bbs_nickname = response.getString("nickname");
                            }
                            if (response.has("time")) {
                                bbs_date = response.getString("time");

                            }
                            if (response.has("comments_count")) {
                                bbs_pinglunshu = response.getString("comments_count");

                            }
                            if (response.has("content")) {
                                bbs_content = response.getString("content");
                            }
                            if (response.has("img1")) {
                                bbs_img1 = response.getString("img1");
                                imgList.add(bbs_img1);
                            }
                            if (response.has("img2")) {
                                bbs_img2 = response.getString("img2");
                                imgList.add(bbs_img2);
                            }
                            if (response.has("img3")) {
                                bbs_img3 = response.getString("img3");
                                imgList.add(bbs_img3);
                            }

                            if (response.has("replys")) {
                                JSONArray list = response.getJSONArray("replys");
                                Gson gson = new Gson();
                                bbsContentBeanList = gson.fromJson(list.toString(), new TypeToken<List<ShzlBBSContentListBean>>() {
                                }.getType());
                                if (bbsContentBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalBbsContentList.addAll(bbsContentBeanList);
                            }

                            handler.sendEmptyMessage(loadstate);
                            newFragment.dismiss();

                        } else {
                            pullRefreshLayout.setRefreshing(false);
                            CustomToast.showToast(ShzlBBSContentActivity.this, msg, 1000);
                            newFragment.dismiss();
                        }
                    } else {
                        pullRefreshLayout.setRefreshing(false);
                        CustomToast.showToast(ShzlBBSContentActivity.this, R.string.http_fail, 1000);
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    pullRefreshLayout.setRefreshing(false);
                    CustomToast.showToast(ShzlBBSContentActivity.this, R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(ShzlBBSContentActivity.this, R.string.http_fail, 1000);
                newFragment.dismiss();
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shzl_bbscontent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(WEIDU_BBS);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        setResult(WEIDU_BBS);
        finish();
        super.onBackPressed();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == LOGIN_SUCCESS && requestCode ==Constants.STATE_LOGIN) {
            IS_LOGIN = spn.getBoolean(SPN_IS_LOGIN, false);
            Intent intent = new Intent(ShzlBBSContentActivity.this, ShzlBBSHFActivity.class);
            intent.putExtra("bbs_id", bbs_id);
            intent.putExtra("tag", "reply");
            intent.putExtra("title","回复"+bbs_nickname);
            startActivityForResult(intent, LOAD_SUCCESS);
        }
        if (resultCode == LOAD_FAIL) {
            httpRequest(LOAD_REFRESH, HTTP_BBS_CONTENT + "?sqid=" + SQID + "&uid=" + UID + "&bbs_id=" + bbs_id + "&bid=" + bid);
            setResult(LOAD_FAIL);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img1:
                Intent intent = new Intent(ShzlBBSContentActivity.this, ShzlPublicTPFDActivity.class);
                intent.putExtra("size", 0);
                intent.putStringArrayListExtra("imglist", imgList);
                startActivity(intent);
                break;
            case R.id.img2:

                intent = new Intent(ShzlBBSContentActivity.this, ShzlPublicTPFDActivity.class);
                intent.putExtra("size", 1);
                intent.putStringArrayListExtra("imglist", imgList);
                startActivity(intent);
                break;
            case R.id.img3:

                intent = new Intent(ShzlBBSContentActivity.this, ShzlPublicTPFDActivity.class);
                intent.putExtra("size", 2);
                intent.putStringArrayListExtra("imglist", imgList);
                startActivity(intent);
                break;
        }
    }
}
