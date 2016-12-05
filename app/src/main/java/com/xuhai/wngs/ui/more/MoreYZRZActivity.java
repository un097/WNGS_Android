package com.xuhai.wngs.ui.more;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoreYZRZActivity extends BaseActionBarAsUpActivity  {
    private ProgressDialogFragment newFragment;
    private EditText et_name, et_tel, et_build, et_unit, et_room;
    private ImageView iv_save;
    private String name, phone, build, unit, room;
    private TextView tv_fankui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_yzrz);

        initView();

    }




    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_tel = (EditText) findViewById(R.id.et_tel);
        et_build = (EditText) findViewById(R.id.et_build);
        et_unit = (EditText) findViewById(R.id.et_unit);
        et_room = (EditText) findViewById(R.id.et_room);
        iv_save = (ImageView) findViewById(R.id.iv_save);
        tv_fankui = (TextView) findViewById(R.id.tv_fankui);

        tv_fankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreYZRZActivity.this, MoreRZFKAcitivity.class);
                startActivity(intent);
            }
        });
        if (IS_AUTH) {
            et_name.setText(AUTH_NAME);
            et_tel.setText(AUTH_PHONE);
            et_build.setText(AUTH_BUILDING);
            et_unit.setText(AUTH_UNIT);
            et_room.setText(AUTH_ROOM);
        }


        iv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Dianjill.isFastDoubleClick()) {
                    return;
                }
                httpPraise(HTTP_YZRZ);
            }
        });
    }

    public void httpPraise(String url) {
        name = et_name.getText().toString().trim();
        phone = et_tel.getText().toString().trim();
        build = et_build.getText().toString().trim();
        unit = et_unit.getText().toString().trim();
        room = et_room.getText().toString().trim();

        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("sqid", SQID);
        params.put("uid", UID);
        params.put("name", name);
        params.put("phone", phone);
        params.put("building", build);
        params.put("unit", unit);
        params.put("room", room);
        params.put("feedback", "0");
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
                            editor.putString(SPN_AUTH_NAME, name);
                            editor.putString(SPN_AUTH_PHONE, phone);
                            editor.putString(SPN_AUTH_BUILDING, build);
                            editor.putString(SPN_AUTH_UNIT, unit);
                            editor.putString(SPN_AUTH_ROOM, room);
                            CustomToast.showToast(MoreYZRZActivity.this, "认证成功", 1000);
//                            Intent intent=new Intent(MoreYZRZActivity.this, MainActivity.class);
//                            startActivity(intent);
                            setResult(LOGIN_SUCCESS);
                            finish();

                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreYZRZActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(MoreYZRZActivity.this, R.string.http_fail, 1000);
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreYZRZActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreYZRZActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_yzrz, menu);
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
