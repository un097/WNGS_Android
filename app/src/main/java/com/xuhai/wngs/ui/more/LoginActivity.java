package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {

    public static final String TAG = "LoginActivity";

    private EditText et_username, et_password;
    private TextView tv_forget_pw;
    private ImageView btn_login, btn_register;
    private ProgressDialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

    }

    private void initView() {
        newFragment = new ProgressDialogFragment();
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_forget_pw = (TextView) findViewById(R.id.tv_forgt_pw);
        btn_login = (ImageView) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        btn_register = (ImageView) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
        tv_forget_pw.setOnClickListener(this);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    LoginActivity.this.setResult(LOGIN_SUCCESS);
                    finish();
                    break;
            }
            return false;
        }
    });

    public void httpLogin(String url) {
        newFragment.show(getFragmentManager(), "1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("sqid", SQID);
        params.put("phone", et_username.getText().toString().trim());
        params.put("passwd", EncryptionByMD5.getMD5(EncryptionByMD5.getMD5(et_password.getText().toString().trim().getBytes()).getBytes()));
        params.put("bpuserid", USERID);
        params.put("bpchannelid", CHANNELID);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            editor.putString(SPN_USER_PHONE, AESEncryptor.encrypt(et_username.getText().toString().trim()));
                            editor.putString(SPN_USER_PWD, EncryptionByMD5.getMD5(EncryptionByMD5.getMD5(et_password.getText().toString().trim().getBytes()).getBytes()));
                            editor.putBoolean(SPN_IS_LOGIN, true);

                            if (response.has("uid")) {
                                editor.putString(SPN_UID, AESEncryptor.encrypt(response.getString("uid")));
                            }
                            if (response.has("head")) {
                                editor.putString(SPN_USER_HEAD, AESEncryptor.encrypt(response.getString("head")));
                            }
                            if (response.has("nickname")) {
                                editor.putString(SPN_NICK_NAME, AESEncryptor.encrypt(response.getString("nickname")));
                            }
                            if (response.has("note")) {
                                editor.putString(SPN_USER_NOTE, AESEncryptor.encrypt(response.getString("note")));
                            }if (response.has("express")) {
                                editor.putString(SPN_EXPRESS, AESEncryptor.encrypt(response.getString("express")));
                            }if (response.has("info")) {
                                editor.putString(SPN_INFO, AESEncryptor.encrypt(response.getString("info")));
                            }if (response.has("bbs")) {
                                editor.putString(SPN_BBS, AESEncryptor.encrypt(response.getString("bbs")));
                            }if (response.has("balance")) {
                                editor.putString(SPN_BALANCE, AESEncryptor.encrypt(response.getString("balance")));
                            }if (response.has("ischeck")){
                                if (response.getString("ischeck").equals("0")){
                                    editor.putBoolean(SPN_IS_BANKCHECK,true);
                                }
                            }
                            if (response.has("ispasswd")){
                                if (response.getString("ispasswd").equals("0")){
                                    editor.putBoolean(SPN_IS_BANKPWD,true);
                                }
                            }

                            if (response.has("checkin")) {
                                if (response.getString("checkin").equals("0")) {
                                    editor.putBoolean(SPN_USER_CHECKIN, false);
                                } else if (response.get("checkin").equals("1")) {
                                    editor.putBoolean(SPN_USER_CHECKIN, true);
                                }
                            }
                            if (response.has("auth")) {
                                if (response.getString("auth").equals("0")) {
                                    editor.putBoolean(SPN_AUTH, false);
                                } else if (response.getString("auth").equals("1")) {
                                    editor.putBoolean(SPN_AUTH, true);
                                }
                            }
                            if (response.has("auth_name")) {
                                editor.putString(SPN_AUTH_NAME, AESEncryptor.encrypt(response.getString("auth_name")));
                            }
                            if (response.has("auth_phone")) {
                                editor.putString(SPN_AUTH_PHONE, AESEncryptor.encrypt(response.getString("auth_phone")));
                            }
                            if (response.has("auth_building")) {
                                editor.putString(SPN_AUTH_BUILDING, AESEncryptor.encrypt(response.getString("auth_building")));
                            }
                            if (response.has("auth_unit")) {
                                editor.putString(SPN_AUTH_UNIT, AESEncryptor.encrypt(response.getString("auth_unit")));
                            }
                            if (response.has("auth_room")) {
                                editor.putString(SPN_AUTH_ROOM, AESEncryptor.encrypt(response.getString("auth_room")));
                            }
                            if (response.has("points_total")) {
                                editor.putString(SPN_POINTS_TOTLA, AESEncryptor.encrypt(response.getString("points_total")));
                            }

                            editor.commit();
                            handler.sendEmptyMessage(LOAD_SUCCESS);
                            newFragment.dismiss();
                        }else {
                            CustomToast.showToast(LoginActivity.this, msg, 1000);
                            newFragment.dismiss();
                        }
                    } else {
                        CustomToast.showToast(LoginActivity.this,R.string.http_fail,1000);
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    CustomToast.showToast(LoginActivity.this,R.string.http_fail,1000);
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(LoginActivity.this,R.string.http_fail,1000);
                newFragment.dismiss();
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
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
            case R.id.btn_login:
                loGin();
                break;
            case R.id.btn_register:
                Intent intent = new Intent(LoginActivity.this, RegisterYanzhengActivity.class);
                intent.putExtra("tag", "0");
                startActivity(intent);
                break;
            case R.id.tv_forgt_pw:
                intent = new Intent(LoginActivity.this, RegisterYanzhengActivity.class);
                intent.putExtra("tag", "1");
                startActivity(intent);
                break;
        }
    }

    public void loGin() {

        if (et_username.getText().toString().trim().equals("")) {
            CustomToast.showToast(getBaseContext(), "请输入手机号", 1000);
        } else if (et_password.getText().toString().trim().equals("")) {
            CustomToast.showToast(getBaseContext(), "请输入密码", 1000);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_username.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(et_password.getWindowToken(), 0);
            httpLogin(HTTP_LOGIN);
        }

    }
}
