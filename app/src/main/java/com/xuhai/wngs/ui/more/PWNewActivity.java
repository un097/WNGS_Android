package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PWNewActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "PWNewActivity";
    private EditText et_pwd_one, et_pwd_two;
    private ImageView confirmation;
    private ProgressDialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwnew);
        initView();
    }

    public void initView() {
        et_pwd_one = (EditText) findViewById(R.id.et_password_one);
        et_pwd_two = (EditText) findViewById(R.id.et_password_two);
        confirmation = (ImageView) findViewById(R.id.confirmation);
        confirmation.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pwnew, menu);
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
        switch (v.getId()) {
            case R.id.confirmation:
                if (et_pwd_one.getText().toString().trim().equals("") || et_pwd_two.getText().toString().trim().equals("")) {
                    CustomToast.showToast(getBaseContext(), "请输入密码", 1000);
                } else if (!et_pwd_one.getText().toString().trim().equals(et_pwd_two.getText().toString().trim())) {
                    CustomToast.showToast(getBaseContext(), "两次输入不一致", 1000);
                } else if (et_pwd_one.getText().toString().trim().length() < 6 || et_pwd_two.getText().toString().trim().length() < 6) {
                    CustomToast.showToast(getBaseContext(), "密码长度不可小于6位", 1000);
                } else {
                    httpPWDnew(HTTP_NEWPWD);
                }
                break;
        }
    }

    public void httpPWDnew(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", getIntent().getStringExtra("phone"));
        params.put("passwd", EncryptionByMD5.getMD5(EncryptionByMD5.getMD5(et_pwd_two.getText().toString().trim().getBytes()).getBytes()));
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
                            CustomToast.showToast(PWNewActivity.this, "修改密码成功", 1000);
                            Intent intent = new Intent(PWNewActivity.this, LoginActivity.class);
                            startActivity(intent);
                            PWNewActivity.this.finish();
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(PWNewActivity.this, msg, 1000);
                        }
                    }else {
                        CustomToast.showToast(PWNewActivity.this, R.string.http_fail, 1000);
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    CustomToast.showToast(PWNewActivity.this, R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(PWNewActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

}
