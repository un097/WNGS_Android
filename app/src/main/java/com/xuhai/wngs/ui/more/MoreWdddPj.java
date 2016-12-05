package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MoreWdddPj extends BaseActionBarAsUpActivity {
    private static final String TAG ="MoreWdddPj";
    private TextView textView;
    private ImageView star1,star2,star3,star4,star5;
    private EditText editText;
    private String ed,star="0";
    ProgressDialogFragment newFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_wddd_pj);
        initView();
    }

    private void initView() {
        String storename=getIntent().getStringExtra("storename");
        textView=(TextView)this.findViewById(R.id.dd_pj_tv);
        textView.setText(storename);
        star1=(ImageView)this.findViewById(R.id.dd_pj_star1);
        star2=(ImageView)this.findViewById(R.id.dd_pj_star2);
        star3=(ImageView)this.findViewById(R.id.dd_pj_star3);
        star4=(ImageView)this.findViewById(R.id.dd_pj_star4);
        star5=(ImageView)this.findViewById(R.id.dd_pj_star5);
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.yellow_star);
                star2.setImageResource(R.drawable.grey_star);
                star3.setImageResource(R.drawable.grey_star);
                star4.setImageResource(R.drawable.grey_star);
                star5.setImageResource(R.drawable.grey_star);
                star="1";
            }
        });star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.yellow_star);
                star2.setImageResource(R.drawable.yellow_star);
                star3.setImageResource(R.drawable.grey_star);
                star4.setImageResource(R.drawable.grey_star);
                star5.setImageResource(R.drawable.grey_star);
                star="2";
            }
        });star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.yellow_star);
                star3.setImageResource(R.drawable.yellow_star);
                star2.setImageResource(R.drawable.yellow_star);
                star4.setImageResource(R.drawable.grey_star);
                star5.setImageResource(R.drawable.grey_star);
                star="3";
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.yellow_star);
                star4.setImageResource(R.drawable.yellow_star);
                star3.setImageResource(R.drawable.yellow_star);
                star2.setImageResource(R.drawable.yellow_star);
                star5.setImageResource(R.drawable.grey_star);
                star="4";
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setImageResource(R.drawable.yellow_star);
                star4.setImageResource(R.drawable.yellow_star);
                star3.setImageResource(R.drawable.yellow_star);
                star2.setImageResource(R.drawable.yellow_star);
                star5.setImageResource(R.drawable.yellow_star);
                star="5";
            }
        });
        editText=(EditText)this.findViewById(R.id.dd_pj_et);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ed=editText.getText().toString().trim();
            }
        });
        ImageView button=(ImageView)this.findViewById(R.id.dd_pj_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }
    private void register() {

        if (editText.getText().toString().trim().equals("")) {
            CustomToast.showToast(getBaseContext(), "请添加评论", 1000);
        }  else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            httpPost(HTTP_WDDD_PJ);

        }
    }
    private String et;
    private void httpPost(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        try {
            et = URLEncoder.encode(ed, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderid", getIntent().getStringExtra("orderid"));
        params.put("uid", UID);
        params.put("content", et);
        params.put("star", star);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            CustomToast.showToast(
                                    MoreWdddPj.this,
                                    "评论成功",1000);
                            Intent intent =new Intent(MoreWdddPj.this,MoreWDDDActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            CustomToast.showToast(getBaseContext(), msg, 1000);
                            newFragment.dismiss();
                        }
                    }else {
                        CustomToast.showToast(getBaseContext(), R.string.http_fail, 1000);
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    CustomToast.showToast(getBaseContext(), R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_wddd_pj, menu);
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
