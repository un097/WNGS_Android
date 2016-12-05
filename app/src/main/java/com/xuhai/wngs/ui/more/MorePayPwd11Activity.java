package com.xuhai.wngs.ui.more;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.CountDownButtonHelper;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MorePayPwd11Activity extends BaseActionBarAsUpActivity {
    private CountDownButtonHelper helper;
    private Button btn_sms,btn_bd;
    private EditText et_sms;
    private ProgressDialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_pay_pwd11);
        initView();
    }

    private void initView(){
        dialog = new ProgressDialogFragment();
        btn_sms = (Button) findViewById(R.id.btn_sms);
        btn_bd = (Button) findViewById(R.id.btn_bd);
        et_sms = (EditText) findViewById(R.id.et_sms);

//        helper = new CountDownButtonHelper(btn_sms, "发送验证码", 60, 1);
//        helper.start();

        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(getFragmentManager(), "1");
                httpGet(HTTP_PWD_GET);
            }
        });
        btn_bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(getFragmentManager(), "1");
                httpAuth(HTTP_PWD_AUTH);
            }
        });
    }

    public void httpGet(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", getIntent().getStringExtra("phone"));
        params.put("tag", "10");
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg ;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            dialog.dismiss();
                            helper = new CountDownButtonHelper(btn_sms, "发送验证码", 60, 1);
                            helper.start();

                            CustomToast.showToast(MorePayPwd11Activity.this, "已为您发送验证码", 1000);
                        } else {
                            dialog.dismiss();
                            CustomToast.showToast(MorePayPwd11Activity.this, msg, 1000);
                        }
                    }
                } catch (Exception e) {
                    Log.d("errp===", e + "");
                    dialog.dismiss();
                    CustomToast.showToast(MorePayPwd11Activity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errp===",error+"");
                dialog.dismiss();
                CustomToast.showToast(MorePayPwd11Activity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    public void httpAuth(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", getIntent().getStringExtra("phone"));
        params.put("tag", "10");
        params.put("smscode", et_sms.getText().toString().trim());
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg ;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MorePayPwd11Activity.this,MorePayPwd2Activity.class);
                            startActivityForResult(intent, STATE_LOGOUT);

                        } else {
                            dialog.dismiss();
                            CustomToast.showToast(MorePayPwd11Activity.this, msg, 1000);
                        }
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    CustomToast.showToast(MorePayPwd11Activity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                CustomToast.showToast(MorePayPwd11Activity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_pay_pwd11, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
