package com.xuhai.wngs.ui.more;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.more.MoreSHDZListAdapter;
import com.xuhai.wngs.beans.more.MoreSHDZListBean;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

public class MoreSHDZActivity extends BaseActionBarAsUpActivity {
    public static final String TAG = "MoreSHDZActivity";
    private ProgressDialogFragment newFragment;

    private int p = 1;

    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数

    private List<MoreSHDZListBean> shdzBeanList;
    private List<MoreSHDZListBean> totalShdzList;

    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;

    private MoreSHDZListAdapter shdzListAdapter;

    private String bldintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_shdz);
        bldintent = getIntent().getStringExtra("bldon");

        initView();

        httpRequest(LOAD_REFRESH, HTTP_SHDZ_LIST + "?uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));

    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    shdzListAdapter = new MoreSHDZListAdapter(MoreSHDZActivity.this, totalShdzList);

                    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(shdzListAdapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);

                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);

                    listView.setAdapter(swingBottomInAnimationAdapter);

                    shdzListAdapter.notifyDataSetChanged();
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    shdzListAdapter.notifyDataSetChanged();
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
            if (bldintent != null ){
                Intent intent = new Intent();
                intent.putExtra("c_sh_name", totalShdzList.get(position).getName());
                intent.putExtra("c_sh_tel", totalShdzList.get(position).getTel());
                intent.putExtra("c_sh_addr", totalShdzList.get(position ).getAddress());
                setResult(RESULT_OK,intent);
                finish();
            }else {
                Intent intent = new Intent(MoreSHDZActivity.this, MoreXGSHDZ.class);
                intent.putExtra("c_sh_addid", totalShdzList.get(position).getAddid());
                intent.putExtra("c_sh_name", totalShdzList.get(position).getName());
                intent.putExtra("c_sh_tel", totalShdzList.get(position).getTel());
                intent.putExtra("c_sh_addr", totalShdzList.get(position).getAddress());
                intent.putExtra("c_sh_default", totalShdzList.get(position).getIsdefault());
                startActivityForResult(intent, LOAD_SUCCESS);
            };
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
                httpRequest(LOAD_REFRESH, HTTP_SHDZ_LIST + "?uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new listitemclick());
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

//                new MaterialDialog().

                Dialog dialog = new AlertDialog.Builder(MoreSHDZActivity.this)
                        .setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delet(totalShdzList.get(position).getAddid());
                                dialog.dismiss();

                            }
                        }).show();
                return true;
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (shdzListAdapter != null) {
                    int lastIndex = shdzListAdapter.getCount() - 1;//数据集最后一项的索引

                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
                        //如果是自动加载,可以在这里放置异步加载数据的代码
                        if (isLoadMore) {
                            p += 1;
                            httpRequest(LOAD_MORE, HTTP_SHDZ_LIST + "?uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                MoreSHDZActivity.this.visibleItemCount = visibleItemCount;
                visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
            }
        });

    }
    private void delet(String addid) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(),"1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", UID);
        params.put("addid", addid);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, "http://shequ.haixusoft.com/api/address_del.php", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreSHDZActivity.this, "删除成功", 1000);
                            httpRequest(LOAD_REFRESH, HTTP_SHDZ_LIST + "?uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
                        }
                    } else {
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
            }
        });
        queue.add(request);


    }


    private void httpRequest(final int loadstate, String url) {
        shdzBeanList = new ArrayList<MoreSHDZListBean>();
        if (loadstate == LOAD_REFRESH) {
            totalShdzList = new ArrayList<MoreSHDZListBean>();
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
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject json = list.getJSONObject(i);
                                    MoreSHDZListBean bean = new MoreSHDZListBean();
                                    if (json.has("addid")) {
                                        bean.setAddid(json.getString("addid"));
                                    }
                                    if (json.has("name")) {
                                        bean.setName(json.getString("name"));

                                    }
                                    if (json.has("tel")) {
                                        bean.setTel(json.getString("tel"));
                                    }
                                    if (json.has("address")) {
                                        bean.setAddress(json.getString("address"));

                                    }
                                    if (json.has("default")) {
                                        bean.setIsdefault(json.getString("default"));

                                    }
                                    shdzBeanList.add(bean);

                                }

                                if (shdzBeanList.size() < PAGE_COUNT) {
                                    isLoadMore = false;
                                } else {
                                    isLoadMore = true;
                                }
                                totalShdzList.addAll(shdzBeanList);
                            }

                            if (totalShdzList.size()==0){
                                listView.setOnItemClickListener(new listitemclick());
                                pullRefreshLayout.setRefreshing(false);
                                CustomToast.showToast(MoreSHDZActivity.this,"暂无数据",1000);
                            }else {
                                handler.sendEmptyMessage(loadstate);
                            }

                        } else {
                            if (loadstate == LOAD_REFRESH) {
                                totalShdzList = new ArrayList<MoreSHDZListBean>();
                                handler.sendEmptyMessage(loadstate);
                            }
                            CustomToast.showToast(MoreSHDZActivity.this, msg, 1000);
                            listView.setOnItemClickListener(new listitemclick());
                            pullRefreshLayout.setRefreshing(false);
                        }
                    } else {
                        CustomToast.showToast(MoreSHDZActivity.this, R.string.http_fail, 1000);
                        listView.setOnItemClickListener(new listitemclick());
                        pullRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(MoreSHDZActivity.this, R.string.http_fail, 1000);
                    listView.setOnItemClickListener(new listitemclick());
                    pullRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(MoreSHDZActivity.this, R.string.http_fail, 1000);
                listView.setOnItemClickListener(new listitemclick());
                pullRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_shdz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.jiahao:
                Intent intent=new Intent(MoreSHDZActivity.this,MoreTJSHDZActivity.class);
                startActivityForResult(intent,LOAD_SUCCESS);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
      public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_SUCCESS && resultCode == RESULT_OK){
            httpRequest(LOAD_REFRESH, HTTP_SHDZ_LIST + "?uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
        }

    }
}
