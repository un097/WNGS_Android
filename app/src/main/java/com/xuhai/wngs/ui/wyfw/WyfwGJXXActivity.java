package com.xuhai.wngs.ui.wyfw;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.R;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;

public class WyfwGJXXActivity extends BaseActionBarAsUpActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private int p = 1;
    private String gj_img, gj_id, gj_name, gj_note, gj_area, gj_phone, gj_goods;
    private RelativeLayout tel_bg;
    private View view2,view1;
    private ImageView gj_image, dianzai_iv;
    private TextView gjname_tv, gjshuoming_tv, gjarea_tv, gjphone_tv, gjgoods_tv;
    private RelativeLayout gjxx_rl,callrl,rl_good;
    private ProgressDialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyfw_gjxx);


        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        httpRequest(LOAD_REFRESH, HTTP_GJXX + "?sqid=" + SQID + "&uid=" + UID + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));
    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    setGjxx();
                    break;

            }
            return false;
        }
    });

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
                            if (response.has("img")) {
                                gj_img = response.getString("img");
                            }
                            if (response.has("butid")) {
                                gj_id = response.getString("butid");
                            }
                            if (response.has("butler")) {
                                gj_name = response.getString("butler");
                            }
                            if (response.has("content")) {
                                gj_note = response.getString("content");

                            }
                            if (response.has("area")) {
                                gj_area = response.getString("area");

                            }
                            if (response.has("phone")) {
                                gj_phone = response.getString("phone");
                            }
                            if (response.has("good")) {
                                gj_goods = response.getString("good");

                            }

                            handler.sendEmptyMessage(LOAD_SUCCESS);
                            newFragment.dismiss();

                        } else {
                            CustomToast.showToast(WyfwGJXXActivity.this, msg, 1000);
                            newFragment.dismiss();
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

    public void setGjxx() {
        gjxx_rl = (RelativeLayout) findViewById(R.id.gjxx_rl);

        gjname_tv = (TextView) findViewById(R.id.gj_name);
        gjshuoming_tv = (TextView) findViewById(R.id.shuoming);
        gjarea_tv = (TextView) findViewById(R.id.fuwuquyu);
        gjphone_tv = (TextView) findViewById(R.id.tel);
        gjgoods_tv = (TextView) findViewById(R.id.zannum);
        dianzai_iv = (ImageView) findViewById(R.id.dianzan);
        callrl = (RelativeLayout) findViewById(R.id.callphone);
        rl_good = (RelativeLayout) findViewById(R.id.rl_good);
        gj_image = (ImageView) findViewById(R.id.gj_img);
        tel_bg = (RelativeLayout) findViewById(R.id.tel_bg);

//        tel_bg.getViewTreeObserver().addOnGlobalLayoutListener(this);
        gj_image.getViewTreeObserver().addOnGlobalLayoutListener(this);
        gjxx_rl.setVisibility(View.VISIBLE);
        gjname_tv.setText(gj_name);
        gjshuoming_tv.setText(gj_note);
        gjarea_tv.setText(gj_area);
        gjphone_tv.setText(gj_phone);
        gjgoods_tv.setText(gj_goods);
        if (gj_img != null || !gj_img.equals("")) {
            PicassoTrustAll.getInstance(WyfwGJXXActivity.this).load(gj_img).placeholder(R.drawable.ic_huisewoniu).into(gj_image);
        } else {

        }

        rl_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpGood(HTTP_BUTLER);
            }
        });

        callrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
    }


    public void httpGood(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sqid", SQID);
        params.put("uid", UID);
        params.put("butid", gj_id);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            CustomToast.showToast(WyfwGJXXActivity.this, "评价成功", 1000);

                        }else {
                            CustomToast.showToast(WyfwGJXXActivity.this, msg, 1000);
                        }
                    } else {

                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    protected void call() {

        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("提示");
        mMaterialDialog.setMessage("确认拨打?");
        mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + gj_phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setNegativeButton("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.show();

    }

    @Override
    public void onGlobalLayout() {
        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);
        view1.setMinimumHeight(gj_image.getHeight()-30);
        view2.setMinimumHeight(tel_bg.getHeight());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tel_bg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            tel_bg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onEvent(WyfwGJXXActivity.this, "gjxx");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wyfw_gjxx, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long


        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
