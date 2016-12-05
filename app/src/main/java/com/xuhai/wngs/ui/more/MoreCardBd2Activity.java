package com.xuhai.wngs.ui.more;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.utils.IdCardVerify;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class MoreCardBd2Activity extends BaseActionBarAsUpActivity {
    private EditText et_name,et_idnum,et_phone;
    private TextView tv_yhxx;
    private Button btn_next;
    private ProgressDialogFragment dialog;
    private CheckBox check_yhxx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_card_bd2);
        initView();
    }

    private void initView(){
        dialog = new ProgressDialogFragment();
        btn_next = (Button) findViewById(R.id.btn_next);
        et_name = (EditText) findViewById(R.id.et_name);
        et_idnum = (EditText) findViewById(R.id.et_idnum);
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_yhxx = (TextView) findViewById(R.id.tv_yhxx);
        check_yhxx = (CheckBox) findViewById(R.id.check_yhxx);

        if (IS_BANKCHECK){
            et_name.setText("已认证");
            et_name.setEnabled(false);
            et_idnum.setText("已认证");
            et_idnum.setEnabled(false);
        }
        tv_yhxx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreCardBd2Activity.this, WebActivity.class);
                intent.putExtra("url",HTTP_BANK_AGREEMENT);
                intent.putExtra("title","用户协议");
                startActivity(intent);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                IdCardVerify idCardVerify = new IdCardVerify();
                try {
                    if (!IS_BANKCHECK){
                        if (!new IdCardVerify().IDCardValidate(et_idnum.getText().toString().trim()).equals("")){
                            CustomToast.showToast(MoreCardBd2Activity.this,"请输入正确的身份证号",2000);
                        }
                    }
                    if (!check_yhxx.isChecked()) {
                        CustomToast.showToast(MoreCardBd2Activity.this,"请先同意用户协议",2000);
                    }else {
                        dialog.show(getFragmentManager(), "1");
                        httpPost(HTTP_BANKINFO_CHECK);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
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
        params.put("cardno", getIntent().getStringExtra("cardno"));
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
                            Intent intent = new Intent(MoreCardBd2Activity.this, MoreCardBd3Activity.class);
                            intent.putExtra("cardno",getIntent().getStringExtra("cardno"));
                            intent.putExtra("phone",et_phone.getText().toString().trim());
                            startActivityForResult(intent, STATE_LOGOUT);

                        } else {
                            dialog.dismiss();
                            CustomToast.showToast(MoreCardBd2Activity.this, msg, 1000);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STATE_LOGOUT & resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_card_bd2, menu);
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
