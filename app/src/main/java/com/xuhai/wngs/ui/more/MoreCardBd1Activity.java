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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoreCardBd1Activity extends BaseActionBarAsUpActivity {
    private Button btn_next;
    private EditText et_card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_card_bd1);
        initView();
    }
    private void initView(){
        btn_next = (Button) findViewById(R.id.btn_next);
        et_card = (EditText) findViewById(R.id.et_card);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_card.getText().toString().equals("")) {
                    CustomToast.showToast(MoreCardBd1Activity.this, "请输入卡号", 1000);
                } else {
                    httpPost(HTTP_BANK_CHECK);
                }
            }
        });
    }

    public void httpPost(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("bankcard_num", et_card.getText().toString().trim());
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
                            Intent intent = new Intent(MoreCardBd1Activity.this, MoreCardBd2Activity.class);
                            intent.putExtra("cardno",et_card.getText().toString().trim());
                            startActivityForResult(intent,STATE_LOGOUT);

                        } else {
                            CustomToast.showToast(MoreCardBd1Activity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(MoreCardBd1Activity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    Log.d("eeee,,",e+"");
                    CustomToast.showToast(MoreCardBd1Activity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("eeee,,",error+"");
                CustomToast.showToast(MoreCardBd1Activity.this, R.string.http_fail, 1000);
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
        getMenuInflater().inflate(R.menu.menu_more_card_bd1, menu);
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
