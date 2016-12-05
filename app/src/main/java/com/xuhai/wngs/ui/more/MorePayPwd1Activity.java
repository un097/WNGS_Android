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
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MorePayPwd1Activity extends BaseActionBarAsUpActivity {
    private EditText et_idnum,et_name,et_phone;
    private Button btn_next;
    private ProgressDialogFragment dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_pay_pwd1);

        if (IS_BANKPWD){
            setTitle("忘记交易密码");
        }else {
            setTitle("设置交易密码");
        }

        initView();
    }

    private void initView(){
        dialog = new ProgressDialogFragment();
        btn_next = (Button) findViewById(R.id.btn_next);
        et_name = (EditText) findViewById(R.id.et_name);
        et_idnum = (EditText) findViewById(R.id.et_idnum);
        et_phone = (EditText) findViewById(R.id.et_phone);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(getFragmentManager(), "1");
                if (IS_BANKPWD){
                    httpPost(HTTP_PWD_FGTAUTH);
                }else {
                    httpPost(HTTP_PWD_REALNAME);
                }
            }
        });
    }

    public void httpPost(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("idno", et_idnum.getText().toString().trim());
        params.put("realname", et_name.getText().toString().trim());
        params.put("tel", et_phone.getText().toString().trim());
        params.put("uid", UID);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            dialog.dismiss();
                            editor.putBoolean(SPN_IS_BANKCHECK,true);
                            editor.commit();
                            Intent intent = new Intent(MorePayPwd1Activity.this,MorePayPwd11Activity.class);
                            intent.putExtra("phone",et_phone.getText().toString().trim());
                            startActivityForResult(intent, STATE_LOGOUT);

                        } else {
                            dialog.dismiss();
                            CustomToast.showToast(MorePayPwd1Activity.this, msg, 1000);
                        }
                    } else {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    Log.d("error===",e+"");
                    dialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error===",error+"");
                dialog.dismiss();
            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_pay_pwd1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
