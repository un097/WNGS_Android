package com.xuhai.wngs.ui.more;

import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.more.MoreCardListAdapter;
import com.xuhai.wngs.adapters.more.MoreZdmxListAdapter;
import com.xuhai.wngs.beans.more.MoreBankListBean;
import com.xuhai.wngs.beans.more.MoreZdmxBean;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreZdmxActivity extends BaseActionBarAsUpActivity {
    private ListView listView;
    private MoreZdmxListAdapter zdmxListAdapter;
    private List<MoreZdmxBean> zdmxBeanList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_zdmx);
        initView();
        httpRequest(LOAD_REFRESH,HTTP_VACCOUNT + "?uid=" + UID );
    }

    private void initView(){
        listView = (ListView) findViewById(R.id.listView);

    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_REFRESH:
                    zdmxListAdapter = new MoreZdmxListAdapter(MoreZdmxActivity.this,zdmxBeanList);
                    listView.setAdapter(zdmxListAdapter);
                    break;
                case LOAD_MORE:
                    break;
                case LOAD_FAIL:

                    break;
            }
            return false;
        }
    });


    private void httpRequest(final int loadstate, String url) {
        zdmxBeanList = new ArrayList<MoreZdmxBean>();
        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("res===",response+"");
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                Gson gson = new Gson();
                                zdmxBeanList = gson.fromJson(list.toString(), new TypeToken<List<MoreZdmxBean>>() {
                                }.getType());
                            }
                            handler.sendEmptyMessage(loadstate);
                        } else {
                            CustomToast.showToast(MoreZdmxActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(MoreZdmxActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(MoreZdmxActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(MoreZdmxActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_zdmx, menu);
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
