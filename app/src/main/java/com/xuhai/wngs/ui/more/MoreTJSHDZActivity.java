package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class MoreTJSHDZActivity extends BaseActionBarAsUpActivity {
    private EditText et_shr, et_shdh, et_shdz;
    private RelativeLayout swmr;
    private String uid;
    private String sh_name, sh_addr, sh_tel;
    private ProgressDialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_xgshdz);

        swmr = (RelativeLayout) findViewById(R.id.swmr_rl);
        swmr.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_tjshdz, menu);
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
        if (id == R.id.yes) {
            viewJiance();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewJiance() {
        et_shr = (EditText) findViewById(R.id.et_shr);
        et_shdh = (EditText) findViewById(R.id.et_shdh);
        et_shdz = (EditText) findViewById(R.id.sh_dz);
        if (et_shdh.getText().toString().trim().equals("")) {
            CustomToast.showToast(MoreTJSHDZActivity.this, "电话不能为空！", 1000);
        } else if (et_shr.getText().toString().trim().equals("")) {
            CustomToast.showToast(MoreTJSHDZActivity.this, "收货人不能为空！", 1000);
        } else if (et_shdz.getText().toString().trim().equals("")) {
            CustomToast.showToast(MoreTJSHDZActivity.this, "收货地址不能为空！", 1000);
        }else {



            httpAdd(HTTP_ADD_SHDZ);
        }
    }

    private void httpAdd(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(),"1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", UID);
        params.put("name", et_shr.getText().toString().trim());
        params.put("tel",   et_shdh.getText().toString().trim());
        params.put("address", et_shdz.getText().toString().trim());
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreTJSHDZActivity.this, "添加成功", 1000);
                            Intent intent = new Intent();
                            intent.putExtra("c_sh_name", et_shr.getText().toString().trim());
                            intent.putExtra("c_sh_tel",   et_shdh.getText().toString().trim());
                            intent.putExtra("c_sh_addr",et_shdz.getText().toString().trim());
                            setResult(RESULT_OK,intent);
                            finish();
                        }else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreTJSHDZActivity.this,msg,1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreTJSHDZActivity.this,R.string.http_fail,1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreTJSHDZActivity.this,R.string.http_fail,1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreTJSHDZActivity.this,R.string.http_fail,1000);
            }
        });
        queue.add(request);
    }

}
