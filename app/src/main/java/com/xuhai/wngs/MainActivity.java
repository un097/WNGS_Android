package com.xuhai.wngs;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HTTPSTrustManager;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;
import com.xuhai.wngs.adapters.main.TabFragmentPagerAdapter;
import com.xuhai.wngs.beans.main.MainBean;
import com.xuhai.wngs.beans.main.MainCityBean;
import com.xuhai.wngs.beans.main.MainCityEngBean;
import com.xuhai.wngs.beans.main.MainInfoBean;
import com.xuhai.wngs.beans.main.MainListBean;
import com.xuhai.wngs.push.Utils;
import com.xuhai.wngs.ui.main.MainCitySelActivity;
import com.xuhai.wngs.ui.main.MainFragment;
import com.xuhai.wngs.ui.more.MoreFragment;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.utils.UpdateManager;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    public static final String TAG = "MainActivity";

//    private MainInfoBean mainInfoBean;

    public ViewPager viewPager;
    public TabFragmentPagerAdapter pagerAdapter;
    public TabPageIndicator indicator;
    private ProgressDialogFragment newFragment;

    private TextView titleView;
//    private List<List<MainBean>> allList;
//    private List<MainBean> wyfwList, shzlList, sjfwList;
    private List<MainListBean> mainListBeanList;

    String version_number;
    String updateforce;
    String download_url;
    String express,info,bbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    //    UpdateManager manager = new UpdateManager(MainActivity.this, "");
//        checkUpate("http://shequ.haixusoft.com/api/version.php?iora=a&uid=" + UID + "&sqid=" + SQID);

        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.openActivityDurationTrack(false);
//        NBSAppAgent.setLicenseKey("6c60b23d918d4c3c89b4ed229ff7528b").withLocationServiceEnabled(true).start(this);
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(MainActivity.this, "api_key"));
        setTags();

        if (getActionBar() != null) {
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowCustomEnabled(true);

            getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.actionbar_title_center_click, null), new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));
            titleView = (TextView) getActionBar().getCustomView().findViewById(R.id.title);
            titleView.setText(SQNAME);
            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dropdown, 0);
            titleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.padding_dropdown));

            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainCitySelActivity.class);
                    startActivityForResult(intent, 100);
                }
            });


        }

        newFragment = new ProgressDialogFragment();

        initView();

//        checkUpate(HTTP_VERSION + "?iora=a&uid=" + UID + "&sqid=" + SQID);
        httpRequest(HTTP_CHANGEPLOT + "?sqid=" + SQID);
    }

    private void setTags() {
        // Push: 设置tag调用方式
        List<String> tags = Utils.getTagsList(SQID);
        PushManager.setTags(getApplicationContext(), tags);

    }


    private void httpRequest(String url) {
        newFragment.show(getFragmentManager(), "1");
        mainListBeanList = new ArrayList<MainListBean>();
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
                                mainListBeanList = gson.fromJson(list.toString(), new TypeToken<List<MainListBean>>() {
                                }.getType());
                            }
                            handler.sendEmptyMessage(LOAD_SUCCESS);
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MainActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MainActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    Log.d("e===",e+"");
                    CustomToast.showToast(MainActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                Log.d("e===",error+"");
                CustomToast.showToast(MainActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }



    public void checkUpate(String url) {
        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                String recode = null, msg = null;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        if (response.has("msg")) {
                            msg = response.getString("msg");
                        }
                        if (recode.equals("0")) {

                            if (response.has("version")) {
                                version_number = response.getString("version");
                            }

                            if (response.has("upgrade")) {
                                updateforce = response.getString("upgrade");
                            }
                            if (response.has("url")) {
                                download_url = response.getString("url");
                            }
                            if (response.has("express")){
                                express = response.getString("express");
                                editor.putString(SPN_EXPRESS, AESEncryptor.encrypt(express));
                            }
                            if (response.has("info")){
                                info = response.getString("info");
                                editor.putString(SPN_INFO,AESEncryptor.encrypt(info));
                            }
                            if (response.has("bbs")){
                                bbs = response.getString("bbs");
                                editor.putString(SPN_BBS,AESEncryptor.encrypt(bbs));
                            }
                            editor.commit();
                            handler.sendEmptyMessage(5);

                        } else {
                            CustomToast.showToast(MainActivity.this, msg,1000);
                        }
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub


            }
        });
        queue.add(request);
    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    newFragment.dismiss();

                    initViewPager();
                    viewPager.setOffscreenPageLimit(pagerAdapter.getCount());
                    viewPager.setAdapter(pagerAdapter);
                    indicator.setVisibility(View.VISIBLE);
                    indicator.setViewPager(viewPager);
                    break;
                case 5:
                    UpdateManager manager = new UpdateManager(MainActivity.this, "",version_number,updateforce,download_url);
                    manager.update();
//                    allList.get(1).get(2).setInfo(info);
//                    allList.get(0).get(1).setExpress(express);
//                    allList.get(0).get(3).setBbs(bbs);

                    pagerAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });

     private void initView() {

        viewPager = (ViewPager) findViewById(R.id.pager);

        indicator = (TabPageIndicator) findViewById(R.id.indicator);

    }


    private void initViewPager() {
        pagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            if (i < pagerAdapter.getCount() - 1) {
                Fragment fragment = MainFragment.newInstance(mainListBeanList.get(i).getModdtl(),i);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("list", (Serializable) mainListBeanList.get(i).getModdtl());
//                bundle.putInt("index", i);
//                fragment.setArguments(bundle);
                pagerAdapter.getFragments().add(fragment);
            } else {
                Fragment fragment = MoreFragment.newInstance();
                pagerAdapter.getFragments().add(fragment);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case LOGIN_SUCCESS:
                IS_LOGIN = spn.getBoolean(SPN_IS_LOGIN,false);
                IS_AUTH = spn.getBoolean(SPN_AUTH,false);
                try {
                    POINTS_TOTAL = AESEncryptor.decrypt(spn.getString(SPN_POINTS_TOTLA,""));
                    INFO = AESEncryptor.decrypt(spn.getString(SPN_INFO,""));
                    EXPRESS = AESEncryptor.decrypt(spn.getString(SPN_EXPRESS,""));
                    BBS = AESEncryptor.decrypt(spn.getString(SPN_BBS,""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                allList.get(1).get(2).setInfo(INFO);
//                allList.get(0).get(1).setExpress(EXPRESS);
//                allList.get(0).get(3).setBbs(BBS);
                pagerAdapter.notifyDataSetChanged();
                break;
            case RESULT_OK:
                if (requestCode == 100) {
                    try {
                        SQID = AESEncryptor.decrypt(spn.getString(SPN_SQID, ""));
                        SQNAME = AESEncryptor.decrypt(spn.getString(SPN_SQNAME, ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    IS_AUTH = spn.getBoolean(SPN_AUTH,false);
                    titleView.setText(SQNAME);
                    setTags();
                    httpRequest(HTTP_CHANGEPLOT + "?sqid=" + SQID);
//                    pagerAdapter.notifyDataSetChanged();
                }
                break;
            case 10:
                editor.putString(SPN_INFO, "");
                editor.commit();
//                allList.get(1).get(2).setInfo("0");
                pagerAdapter.notifyDataSetChanged();
                break;
            case 11:
                editor.putString(SPN_EXPRESS, "");
                editor.commit();
//                allList.get(0).get(1).setExpress("0");
                pagerAdapter.notifyDataSetChanged();
                break;
            case WEIDU_BBS:
                editor.putString(SPN_BBS, "");
                editor.commit();
//                allList.get(0).get(3).setBbs("0");
                pagerAdapter.notifyDataSetChanged();
                break;
        }

    }

    private long exitTime = 0; // 退出计时

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {


            if ((System.currentTimeMillis() - exitTime) > 2000) {
                CustomToast.showToast(getApplicationContext(), "再按一次退出程序", 1000);
                exitTime = System.currentTimeMillis();
            } else {
                MobclickAgent.onKillProcess(this);
                editor.putString(SPN_EXPRESS, "");
                editor.putString(SPN_INFO, "");
                editor.putString(SPN_BBS, "");
                editor.commit();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
