package com.xuhai.wngs.ui.shzl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShzlBBSHFActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "ShzlBBSHFActivity";
    private ImageView confirmation;
    private EditText et_huifu;
    private ProgressDialogFragment newFragment;
    private String bid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bbshf);
        if (getActionBar()!=null){
            getActionBar().setTitle(getIntent().getStringExtra("title"));
        }
        bid=getIntent().getStringExtra("bid");
        initView();
    }

    private void initView() {
        et_huifu = (EditText) findViewById(R.id.et_huifu);
        confirmation = (ImageView) findViewById(R.id.confirmation);
        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_huifu.getText().toString().trim().equals("")) {
                    CustomToast.showToast(getBaseContext(), "请输入回复内容", 1000);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_huifu.getWindowToken(), 0);
                    if (getIntent().getStringExtra("tag").equals("reply")) {
                        httpPost(HTTP_BBS_CONTENT_HUIFU);
                    } else {
                        httpPost(HTTP_BBS_CONTENT_HUIFU);
                    }

                }
            }
        });
    }

    private void httpPost(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");
        Map<String, String> params = new HashMap<String, String>();
        if (getIntent().getStringExtra("tag").equals("reply")) {
            params.put("sqid", SQID);
            params.put("uid", UID);
            params.put("bid", bid);
            params.put("content", et_huifu.getText().toString().trim());
            params.put("bbs_id", getIntent().getStringExtra("bbs_id"));
        } else {
            params.put("uid", UID);
            params.put("reply_other_id", getIntent().getStringExtra("reply_other_id"));
            params.put("content", et_huifu.getText().toString().trim());
            params.put("bbs_id", getIntent().getStringExtra("bbs_id"));
        }

        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg = null;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");
                        if (recode.equals("0")) {
                            newFragment.dismiss();
                            if (getIntent().getStringExtra("tag").equals("reply")) {
                                CustomToast.showToast(ShzlBBSHFActivity.this, "评论成功", 1000);
                            } else {
                                CustomToast.showToast(ShzlBBSHFActivity.this, "回复成功", 1000);
                            }
                            setResult(LOAD_FAIL);
                            finish();
                        } else {
                            confirmation.setOnClickListener(ShzlBBSHFActivity.this);
                            newFragment.dismiss();
                            CustomToast.showToast(ShzlBBSHFActivity.this, msg, 1000);
                        }
                    } else {
                        confirmation.setOnClickListener(ShzlBBSHFActivity.this);
                        newFragment.dismiss();
                        CustomToast.showToast(ShzlBBSHFActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    confirmation.setOnClickListener(ShzlBBSHFActivity.this);
                    CustomToast.showToast(ShzlBBSHFActivity.this, R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                confirmation.setOnClickListener(ShzlBBSHFActivity.this);
                CustomToast.showToast(ShzlBBSHFActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shzl_bbsh, menu);
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

    }
}
