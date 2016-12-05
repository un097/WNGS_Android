package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.media.Image;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreWDKDXQBean;
import com.xuhai.wngs.utils.CreateQRImageTest;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;


public class MoreWdkdItemActivity extends BaseActionBarAsUpActivity {
    private String exid;
    ProgressDialogFragment newFragment;
    private LinearLayout layout_all;
    private MoreWDKDXQBean moreWDKDXQBean;
    private TextView title,flag,date,info;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_wdkd_item);
        exid = getIntent().getStringExtra("exid");
        initView();
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        httpRequest(LOAD_SUCCESS, HTTP_WDKD_XQ + "?uid=" + UID + "&phone=" + USER_PHONE + "&exid=" + exid);
    }



    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    layout_all = (LinearLayout) findViewById(R.id.layout_all);
                    layout_all.setVisibility(View.VISIBLE);
                    title.setText(moreWDKDXQBean.getTitle());
                    date.setText(moreWDKDXQBean.getDate());
                    info.setText(moreWDKDXQBean.getInfo());
                    if (moreWDKDXQBean.getOut().equals("0")){
                        flag.setText("未领取"); flag.setTextColor(R.color.orange_dark);
                    }else  if (moreWDKDXQBean.getOut().equals("1")){
                        flag.setText("已领取"); flag.setTextColor(R.color.green_dark);
                    }else  if (moreWDKDXQBean.getOut().equals("2")){
                        flag.setText("保留"); flag.setTextColor(R.color.color_text_gray);
                    }
                   // new CreateQRImageTest(moreWDKDXQBean.getQrcode(),image);
                    new CreateQRImageTest(moreWDKDXQBean.getQrcode(),image);
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
        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.date);
        flag = (TextView) findViewById(R.id.flag);
        info = (TextView) findViewById(R.id.info);
        image = (ImageView) findViewById(R.id.image);
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
                            Gson gson = new Gson();
                            moreWDKDXQBean = gson.fromJson(String.valueOf(response), MoreWDKDXQBean.class);
                            newFragment.dismiss();
                            handler.sendEmptyMessage(loadstate);

                        } else {
                            CustomToast.showToast(MoreWdkdItemActivity.this, msg, 1000);
                            newFragment.dismiss();
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreWdkdItemActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreWdkdItemActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();CustomToast.showToast(MoreWdkdItemActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_wdkd_item, menu);
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
            setResult(RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
