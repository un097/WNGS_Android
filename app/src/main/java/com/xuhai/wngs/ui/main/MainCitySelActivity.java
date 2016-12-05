package com.xuhai.wngs.ui.main;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.ShequListActivity;
import com.xuhai.wngs.adapters.main.MainCityEngAdapter;
import com.xuhai.wngs.adapters.main.MainCityHotAdapter;
import com.xuhai.wngs.adapters.more.MoreCardListAdapter;
import com.xuhai.wngs.beans.main.MainCityBean;
import com.xuhai.wngs.beans.main.MainCityEngBean;
import com.xuhai.wngs.beans.more.MoreBankListBean;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.MyLetterListView;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCitySelActivity extends BaseActionBarAsUpActivity {

    private String URL = HTTP_SERVER + "province.php?id=1";
    private GridView gv_city;
    private ListView lv_city;

    private List<MainCityBean> hotBeanList;
    private List<MainCityEngBean> cityEngBeanList;

    private MainCityEngAdapter cityEngAdapter;
    private MainCityHotAdapter cityHotAdapter;

    private MyLetterListView letterListView;

    private Map<String,Integer> engMap;
//
//    class engBean{
//        String
//    }
    private ProgressDialogFragment newFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_city_sel);
        initView();
        httpRequest(LOAD_REFRESH,URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 & resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView(){
        newFragment = new ProgressDialogFragment();
        gv_city = (GridView) findViewById(R.id.gv_city);
        gv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainCitySelActivity.this, ShequListActivity.class);
                intent.putExtra("cityCode",hotBeanList.get(i).getCityCode());
                startActivityForResult(intent, 100);
            }
        });
        lv_city = (ListView) findViewById(R.id.lv_city);
        letterListView = (MyLetterListView) findViewById(R.id.letterView);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_REFRESH:
                    newFragment.dismiss();

                    cityHotAdapter = new MainCityHotAdapter(MainCitySelActivity.this,hotBeanList);
                    gv_city.setAdapter(cityHotAdapter);

                    Log.d("list=====-=-=",cityEngBeanList.size() + "");


                    cityEngAdapter = new MainCityEngAdapter(MainCitySelActivity.this,cityEngBeanList);
                    lv_city.setAdapter(cityEngAdapter);

                    engMap = new HashMap<String,Integer>();

                    for (int i = 0; i < cityEngBeanList.size(); i ++){
                        engMap.put(cityEngBeanList.get(i).getEng(),i);
                    }
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
        newFragment.show(getFragmentManager(), "1");
        hotBeanList = new ArrayList<MainCityBean>();
        cityEngBeanList = new ArrayList<MainCityEngBean>();
        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                Gson gson = new Gson();
                                cityEngBeanList = gson.fromJson(list.toString(), new TypeToken<List<MainCityEngBean>>() {
                                }.getType());
                            }
                            if (response.has("hotcitylist")){
                                JSONArray list = response.getJSONArray("hotcitylist");
                                Gson gson = new Gson();
                                hotBeanList = gson.fromJson(list.toString(), new TypeToken<List<MainCityBean>>() {
                                }.getType());
                            }
                            handler.sendEmptyMessage(loadstate);
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MainCitySelActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MainCitySelActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    Log.d("eeee,,", e + "");
                    newFragment.dismiss();
                    CustomToast.showToast(MainCitySelActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("eeee,,", error + "");
                newFragment.dismiss();
                CustomToast.showToast(MainCitySelActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    private class LetterListViewListener implements
            MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {

            if (engMap.get(s) != null) {
                int position = engMap.get(s);
                lv_city.setSelection(position);

                CustomToast.showToast(MainCitySelActivity.this, s, 1000);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_city_sel, menu);
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
