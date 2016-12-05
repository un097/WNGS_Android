package com.xuhai.wngs.ui.more;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import cn.cccb.lib.cccbpay.PayPwdView;

public class MorePayPwd2Activity extends BaseActionBarAsUpActivity {
    private EditText et_pwd1,et_pwd2;
    private Button btn_sure;
    private ProgressDialogFragment dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_pay_pwd2);
        initView();
    }

    private void initView(){
        dialog = new ProgressDialogFragment();
        et_pwd1 = (EditText) findViewById(R.id.et_pwd1);
        et_pwd2 = (EditText) findViewById(R.id.et_pwd2);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_pwd1.getText().toString().equals("") | et_pwd2.getText().toString().equals("")){
                    CustomToast.showToast(MorePayPwd2Activity.this,"请输入密码",1000);
                }else if (et_pwd2.getText().length() < 6 | et_pwd2.getText().length() > 6) {
                    CustomToast.showToast(MorePayPwd2Activity.this, "请输入6位密码", 1000);
                }
                else if (!et_pwd1.getText().toString().equals(et_pwd2.getText().toString())){
                    CustomToast.showToast(MorePayPwd2Activity.this,"两次密码不一致",1000);
                }else {
                    dialog.show(getFragmentManager(),"1");

                    if (IS_BANKPWD){
                        httpSetpwd(HTTP_FORGER_PWD);
                    }else {
                        httpSetpwd(HTTP_PWD_SET);
                    }
                }
            }
        });
    }

    public void httpSetpwd(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", UID);
        params.put("trad_passwd", PayPwdView.getCccbPwd(et_pwd1.getText().toString(),BANKUID));
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

                            editor.putBoolean(SPN_IS_BANKPWD,true);
                            editor.commit();

                            setResult(RESULT_OK);
                            finish();
                            CustomToast.showToast(MorePayPwd2Activity.this, "设置成功", 1000);

                        } else {
                            dialog.dismiss();
                            CustomToast.showToast(MorePayPwd2Activity.this, msg, 1000);
                        }
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    CustomToast.showToast(MorePayPwd2Activity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                CustomToast.showToast(MorePayPwd2Activity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_pay_pwd2, menu);
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
