package com.xuhai.wngs;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HTTPSTrustManager;
import com.android.volley.toolbox.MultiPartStack;
import com.android.volley.toolbox.Volley;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.beans.shzl.ShdzBldNumBean;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.views.DBManager;

import java.math.BigDecimal;


public class BaseActivity extends FragmentActivity implements Constants {

    public boolean isLoadMore = false; //是否加载更多

    public int screenWidth, screenHeight; //屏幕宽高

    public SharedPreferences spn;
    public SharedPreferences.Editor editor;

    public String SQID, UID, SQNAME, NICK_NAME, USER_PHONE, USER_HEAD, SQIMG, USER_NOTE,POINTS_TOTAL,EXPRESS,INFO,BBS,BANKUID;
    public boolean IS_LOGIN, IS_AUTH,IS_FIRST_OPEN, IS_SELECT_SHEQU, IS_CHECKIN, IS_BANKCHECK,IS_BANKPWD;
    public int PAGE_COUNT;
    public int INDICATOR_HEIGHT;
    public String AUTH_NAME, AUTH_PHONE, AUTH_BUILDING, AUTH_UNIT, AUTH_ROOM;
    public String BALANCE;
    public RequestQueue queue;
    public SQLiteDatabase database;
    public String USERID,CHANNELID;
    public String BLD_STARTTIME,BLD_ENDTIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
//            getActionBar().setHomeButtonEnabled(false);
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//            getActionBar().setDisplayShowCustomEnabled(true);
//            getActionBar().setDisplayShowHomeEnabled(false);
//            getActionBar().setTitle(" ");
//            getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.actionbar_title_center_click, null), new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER));
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }

        //数据库
        database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;

        spn = getSharedPreferences(SPN_WNGS, MODE_PRIVATE);
        editor = spn.edit();

        IS_SELECT_SHEQU = spn.getBoolean(SPN_IS_SELECT_SHEQU, false);
        try {

            SQID = AESEncryptor.decrypt(spn.getString(SPN_SQID, ""));

            //推送
            USERID = AESEncryptor.decrypt(spn.getString(SPN_USERID, ""));
            CHANNELID = AESEncryptor.decrypt(spn.getString(SPN_CHANNELID,""));
            //获取认证信息
            AUTH_NAME = AESEncryptor.decrypt(spn.getString(SPN_AUTH_NAME, ""));
            AUTH_PHONE = AESEncryptor.decrypt(spn.getString(SPN_AUTH_PHONE, ""));
            AUTH_BUILDING = AESEncryptor.decrypt(spn.getString(SPN_AUTH_BUILDING, ""));
            AUTH_UNIT = AESEncryptor.decrypt(spn.getString(SPN_AUTH_UNIT, ""));
            AUTH_ROOM = AESEncryptor.decrypt(spn.getString(SPN_AUTH_ROOM, ""));
            POINTS_TOTAL = AESEncryptor.decrypt(spn.getString(SPN_POINTS_TOTLA, ""));
            BANKUID = AESEncryptor.decrypt(spn.getString(SPN_BANK_UID, ""));
            //便利店时间
            BLD_STARTTIME = AESEncryptor.decrypt(spn.getString(SPN_START_TIME,""));
            BLD_ENDTIME = AESEncryptor.decrypt(spn.getString(SPN_END_TIME,""));


            //获取用户信息
            UID = AESEncryptor.decrypt(spn.getString(SPN_UID, ""));
            USER_PHONE = AESEncryptor.decrypt(spn.getString(SPN_USER_PHONE, ""));
            USER_HEAD = AESEncryptor.decrypt(spn.getString(SPN_USER_HEAD, ""));
            NICK_NAME = AESEncryptor.decrypt(spn.getString(SPN_NICK_NAME, ""));
            USER_NOTE = AESEncryptor.decrypt(spn.getString(SPN_USER_NOTE, ""));

            SQIMG = AESEncryptor.decrypt(spn.getString(SPN_SQIMG, ""));
            BBS = AESEncryptor.decrypt(spn.getString(SPN_BBS, ""));
            INFO = AESEncryptor.decrypt(spn.getString(SPN_INFO, ""));
            EXPRESS = AESEncryptor.decrypt(spn.getString(SPN_EXPRESS, ""));
            BALANCE = AESEncryptor.decrypt(spn.getString(SPN_BALANCE,""));
            SQNAME = AESEncryptor.decrypt(spn.getString(SPN_SQNAME, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }


        //获取是否登录
        IS_LOGIN = spn.getBoolean(SPN_IS_LOGIN, false);
        IS_FIRST_OPEN=spn.getBoolean(SPN_IS_FIRST_OPEN, false);
        IS_BANKCHECK = spn.getBoolean(SPN_IS_BANKCHECK, false);
        IS_BANKPWD = spn.getBoolean(SPN_IS_BANKPWD,false);
        PAGE_COUNT = spn.getInt(SPN_PAGE_COUNT, 20);
//        STOREID = spn.getString(SPN_STOREID, "");

        IS_AUTH = spn.getBoolean(SPN_AUTH, false);
        IS_CHECKIN = spn.getBoolean(SPN_USER_CHECKIN, false);

        queue = Volley.newRequestQueue(this, new MultiPartStack());
        queue.start();
        HTTPSTrustManager.allowAllSSL();

    }

    //友盟
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onResume(this);       //统计时长
//    }
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPause(this);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);       //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        database.close();
        super.onDestroy();
    }

}
