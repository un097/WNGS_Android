package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoreXGSHDZ extends BaseActionBarAsUpActivity {
    private EditText et_shr, et_shdh, et_shdz;
    private ImageView setmoren;
    ProgressDialog progressDialog;
    private String sh_name, sh_addr, sh_tel, sh_isdefault;
    private ProgressDialogFragment newFragment;
    private boolean isSetmoren;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_xgshdz);
        initView();
    }

    private void initView() {
        newFragment = new ProgressDialogFragment();
        et_shr = (EditText) findViewById(R.id.et_shr);
        et_shdh = (EditText) findViewById(R.id.et_shdh);
        et_shdz = (EditText) findViewById(R.id.sh_dz);
        setmoren = (ImageView) findViewById(R.id.bt_swmrdz);
        sh_isdefault = getIntent().getStringExtra("c_sh_default");

        if (sh_isdefault.equals("0")) {
            setmoren.setBackgroundResource(R.drawable.set_up_on);
        } else {
            setmoren.setBackgroundResource(R.drawable.set_up_off);
        }
        et_shr.setText(getIntent().getStringExtra("c_sh_name"));
        et_shdh.setText(getIntent().getStringExtra("c_sh_tel"));
        et_shdz.setText(getIntent().getStringExtra("c_sh_addr"));


        setmoren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpsetmoren(HTTP_SET_MOREN);
            }
        });
    }

    private void httpsetmoren(String url) {
        newFragment.show(getFragmentManager(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", UID);
        params.put("addid", getIntent().getStringExtra("c_sh_addid"));
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
                            CustomToast.showToast(MoreXGSHDZ.this, "设置成功", 1000);
                            setmoren.setImageResource(R.drawable.set_up_off);
                            isSetmoren = true;
                        }else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreXGSHDZ.this,msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreXGSHDZ.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreXGSHDZ.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreXGSHDZ.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_xgshdz, menu);
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
            if (isSetmoren){
                setResult(RESULT_OK);
            }
            finish();
            return true;
        }
        if (id == R.id.yes) {
            httpPost(HTTP__XIUGAI_DIZHI);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
          if (isSetmoren){
              setResult(RESULT_OK);
          }
            finish();
                return true;

            }

        return super.dispatchKeyEvent(event);
    }
    private void httpPost(String url) {
        newFragment.show(getFragmentManager(), "1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", UID);
        params.put("addid", getIntent().getStringExtra("c_sh_addid"));
        params.put("name", et_shr.getText().toString().trim());
        params.put("tel", et_shdh.getText().toString().trim());
        params.put("address", et_shdz.getText().toString().trim());
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

                            CustomToast.showToast(MoreXGSHDZ.this, "修改成功", 1000);
                            setResult(RESULT_OK);
                            finish();
                        }else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreXGSHDZ.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreXGSHDZ.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(MoreXGSHDZ.this, R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreXGSHDZ.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }
}
