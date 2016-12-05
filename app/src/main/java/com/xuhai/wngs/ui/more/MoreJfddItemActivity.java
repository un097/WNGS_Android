package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreJfddItemActivity extends BaseActionBarAsUpActivity {

    private TextView tv_date,tv_flag,tv_title,tv_points,tv_count,tv_total,tv_redeemcode,tv_orderid;
    private CircleImageView iv_img;
    private String date,flag,title,points,count,total,redeemcode,orderid,img;
    private CardView layout_all;
    private ProgressDialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_jfdd_item);

        orderid = getIntent().getStringExtra("orderid");
        initView();
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(),"1");
        httpRequest(LOAD_SUCCESS,HTTP_JFDD_ITEM_LIST + "?sqid=" + SQID + "&uid=" + UID + "&orderid=" + orderid);
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    layout_all=(CardView)findViewById(R.id.layout_all);
                    layout_all.setVisibility(View.VISIBLE);
                    tv_date.setText(date);
                    if (flag.equals("0")){ tv_flag.setText("未领取"); }else { tv_flag.setText("已领取"); }
                    tv_title.setText(title);
                    tv_points.setText(points  + "积分");
                    tv_count.setText("×" + count);
                    tv_total.setText(total + "积分");
                    tv_redeemcode.setText(redeemcode);
                    tv_orderid.setText(orderid);
                    if (img == null || img.equals("")) {
                        iv_img.setImageResource(R.drawable.ic_huisewoniu);
                    } else {
                        PicassoTrustAll.getInstance(MoreJfddItemActivity.this).load(img).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(iv_img);
                    }


                    break;
            }
            return false;
        }
    });

    private void initView() {
        tv_date = (TextView) findViewById(R.id.date);
        tv_flag = (TextView) findViewById(R.id.flag);
        tv_title = (TextView) findViewById(R.id.title);
        tv_points = (TextView) findViewById(R.id.points);
        tv_count = (TextView) findViewById(R.id.count);
        tv_total = (TextView) findViewById(R.id.total);
        tv_redeemcode = (TextView) findViewById(R.id.redeemcode);
        tv_orderid = (TextView) findViewById(R.id.orderid);
        iv_img = (CircleImageView) findViewById(R.id.img);

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
                            if (response.has("date")){
                                date = response.getString("date");
                            }
                            if (response.has("flag")){
                                flag = response.getString("flag");
                            }
                            if (response.has("img")){
                                img = response.getString("img");
                            }
                            if (response.has("title")){
                                title = response.getString("title");
                            }
                            if (response.has("points")){
                                points = response.getString("points");
                            }
                            if (response.has("count")){
                                count = response.getString("count");
                            }
                            if (response.has("total")){
                                total = response.getString("total");
                            }
                            if (response.has("redeemcode")) {
                                redeemcode = response.getString("redeemcode");
                            }
                            newFragment.dismiss();
                            handler.sendEmptyMessage(loadstate);

                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreJfddItemActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreJfddItemActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreJfddItemActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreJfddItemActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_jfdd_item, menu);
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
