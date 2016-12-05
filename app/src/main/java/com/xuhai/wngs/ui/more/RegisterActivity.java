package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

public class RegisterActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "RegisterActivity";
    private EditText et_username, et_password_one, et_password_two;
    private ImageView confirmation;
    private ProgressDialogFragment newFragment;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone = getIntent().getStringExtra("phone");

        et_username = (EditText) findViewById(R.id.et_username);
        et_password_one = (EditText) findViewById(R.id.et_password_one);
        et_password_two = (EditText) findViewById(R.id.et_password_two);
        confirmation = (ImageView) findViewById(R.id.confirmation);
        et_username.setText(getIntent().getStringExtra("phone"));
        et_username.setOnClickListener(this);
        confirmation.setOnClickListener(this);

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    //Thread thread = new Thread(runnable);
                    // thread.start();
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
                    finish();
                    break;
                case LOAD_MORE:
                    RegisterActivity.this.setResult(LOGIN_SUCCESS);
                    finish();
                    break;
            }
            return false;
        }
    });


    private void httpRegister(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");
        Map<String, String> params = new HashMap<String, String>();


        params.put("phone", phone);
        params.put("passwd", EncryptionByMD5.getMD5(EncryptionByMD5.getMD5(et_password_one.getText().toString().trim().getBytes()).getBytes()));
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

                            CustomToast.showToast(RegisterActivity.this, "注册成功", 1000);
                            handler.sendEmptyMessage(LOAD_SUCCESS);
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(RegisterActivity.this, msg, 1000);
                        }
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(RegisterActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(RegisterActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_username:
                CustomToast.showToast(getBaseContext(), "只能用此手机号注册", 1000);
                break;
            case R.id.confirmation:
                if (Dianjill.isFastDoubleClick()) {
                    return;
                }
                reGister();
                break;
        }
    }

    public void reGister() {
        if (et_password_one.getText().toString().trim().equals("")) {
            CustomToast.showToast(getBaseContext(), "请输入密码", 1000);
        } else if (et_password_two.getText().toString().trim().equals("")) {
            CustomToast.showToast(getBaseContext(), "请输入密码", 1000);
        } else if (et_password_one.getText().toString().trim().length() < 6) {
            CustomToast.showToast(getBaseContext(), "密码长度不可小于6位", 1000);
        } else if (et_password_two.getText().toString().trim().length() < 6) {
            CustomToast.showToast(getBaseContext(), "密码长度不可小于6位", 1000);
        } else if (!et_password_one.getText().toString().trim().equals(et_password_two.getText().toString().trim())) {
            CustomToast.showToast(getBaseContext(), "两次输入密码不一致", 1000);
        } else {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_password_one.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(et_password_two.getWindowToken(), 0);

            httpRegister(HTTP_SIGNUP);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
