package com.xuhai.wngs.ui.more;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.MyViewpager;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreJfdhItemActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "MoreJfdhItemActivity";
    private ViewPager vp_img;
    private ViewGroup vg_img;
    private TextView tv_title, tv_points, tv_content, tv_ok;
    private RelativeLayout ok_bg;
    private String title, content, points;
    private List imgs;
    private ImageView iv_cancel, iv_sub, iv_add;
    private TextView tv_number;
    private PopupWindow popupWindow;
    private String poiid;
    private int size = 0, height;
    private RelativeLayout layout_all;
    private ProgressDialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_jfdh_item);

        popView();
        initView();
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(),"1");
        httpRequest(LOAD_SUCCESS, HTTP_JFDHITEM_LIST + "?sqid=" + SQID + "&poiid=" + getIntent().getStringExtra("poiid"));

    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    layout_all=(RelativeLayout)findViewById(R.id.layout_all);
                    layout_all.setVisibility(View.VISIBLE);
                    tv_title.setText(title);
                    tv_content.setText(content);
                    tv_points.setText(points);
                    MyViewpager scvp = new MyViewpager(MoreJfdhItemActivity.this, imgs, vp_img, vg_img);
                    scvp.setBanner();

                    break;
                case LOAD_REFRESH:

                    break;
                case LOAD_FAIL:

                    break;
            }
            return false;
        }
    });


    private void initView() {
        vp_img = (ViewPager) findViewById(R.id.adv_pager);
        vg_img = (ViewGroup) findViewById(R.id.viewGroup);
        tv_title = (TextView) findViewById(R.id.title);
        tv_points = (TextView) findViewById(R.id.points);
        tv_content = (TextView) findViewById(R.id.content);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(this);
        ok_bg = (RelativeLayout) findViewById(R.id.ok_bg);
    }

    private void popView() {
        //popupwindow
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.pop_jfdh_item, null);
        iv_cancel = (ImageView) layout.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(this);
        iv_sub = (ImageView) layout.findViewById(R.id.iv_sub);
        iv_sub.setOnClickListener(this);
        iv_add = (ImageView) layout.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);
        tv_number = (TextView) layout.findViewById(R.id.tv_number);
        popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        // popupWindow.setAnimationStyle(R.style.AnimBottom);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv_ok.setText("我要兑换");
            }
        });
    }


    private void httpRequest(final int loadstate, String url) {
        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");
                        if (recode.equals("0")) {
                            if (response.has("title")) {
                                title = response.getString("title");
                            }
                            if (response.has("content")) {
                                content = response.getString("content");
                            }
                            if (response.has("points")) {

                                points = response.getString("points");
                            }
                            if (response.has("imgs")) {
                                imgs = new ArrayList();
                                JSONArray list = response.getJSONArray("imgs");
                                for (int i = 0; i < list.length(); i++) {//遍历JSONArray
                                    imgs.add(list.get(i));
                                }
                            }
                            newFragment.dismiss();
                            handler.sendEmptyMessage(loadstate);

                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreJfdhItemActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreJfdhItemActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreJfdhItemActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreJfdhItemActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_cancel:
                popupWindow.dismiss();
                break;
            case R.id.iv_sub:
                if (size <= 0) {
                    iv_sub.setImageResource(R.drawable.image_more_jfdh_duihuanhui);
                } else {
                    size--;
                }
                break;
            case R.id.iv_add:
                size++;
                iv_sub.setImageResource(R.drawable.image_more_jfdh_duihuanhong);
                break;
            case R.id.tv_ok:
                if (tv_ok.getText().toString().trim().equals("我要兑换")) {

                    popupWindow.showAtLocation(findViewById(R.id.ok_bg), Gravity.BOTTOM, 0, ok_bg.getHeight());
                    popupWindow.update();
                    tv_ok.setText("立即兑换");
                } else {
                    if (tv_number.getText().equals("0")){
                        CustomToast.showToast(MoreJfdhItemActivity.this,"兑换数量不能为0",1000);
                    }else {
                        httpPost(HTTP_JFDH_POST);
                    }
                }
                break;
        }
        tv_number.setText("" + size);
    }

    private void httpPost(String url) {
        newFragment.show(getFragmentManager(),"1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("sqid", SQID);
        params.put("poiid", getIntent().getStringExtra("poiid"));
        params.put("uid", UID);
        params.put("count", tv_number.getText().toString().trim());

        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreJfdhItemActivity.this, "兑换成功", 1000);
                            setResult(JIFEN);
                            finish();
//                            Intent intent = new Intent();
//                            intent.setClass(MoreJfdhItemActivity.this, MoreWDJFActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreJfdhItemActivity.this, msg, 1000);
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_jfdh_item, menu);
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
