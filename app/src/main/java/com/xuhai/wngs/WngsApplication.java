package com.xuhai.wngs;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.android.http.RequestManager;
import com.android.volley.AuthFailureError;
import com.baidu.frontia.FrontiaApplication;

import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.views.DBManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 如果您的工程中实现了Application的继承类，那么，您需要将父类改为com.baidu.frontia.FrontiaApplication。
 * 如果您没有实现Application的继承类，那么，请在AndroidManifest.xml的Application标签中增加属性：
 * <application android:name="com.baidu.frontia.FrontiaApplication"
 * 。。。
 * Created by changliang on 14-9-29.
 */
public class WngsApplication extends FrontiaApplication {
    //对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList实现了基于动态数组的数据结构，要移动数据。LinkedList基于链表的数据结构,便于增加删除
    private List<Activity> activityList = new LinkedList<Activity>();
    private static WngsApplication _instance;

    public static String ANDROID_ID;
    private DBManager dbHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
//        initImageLoader(getApplicationContext());
        ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        RequestManager.getInstance().init(WngsApplication.this);

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        dbHelper.closeDatabase();
    }

    public static WngsApplication getInstance()
    {   if (null == _instance) {
        _instance = new WngsApplication();
    }
        return _instance;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 请求签名验证
     * @return
     */
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        if (WngsApplication.getInstance() != null) {
            StringBuilder label = new StringBuilder();
            label.append("WNGS");
            label.append(" ");
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(WngsApplication.getInstance().getPackageName(), 0);
                label.append(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
            }
            label.append(" ");
            label.append(System.getProperty("http.agent"));
            headers.put("User-Agent", label.toString());
            headers.put("Device-ID", WngsApplication.ANDROID_ID);
            headers.put("WNGS-Seal", EncryptionByMD5.getMD5((EncryptionByMD5.getMD5(("wngs" + ANDROID_ID).getBytes())).getBytes()));
        }
        return headers;
    }

    /**
     * WebView请求签名验证
     * @return
     */
    public Map<String, String> getWebViewHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        if (WngsApplication.getInstance() != null) {
            headers.put("Device-ID", WngsApplication.ANDROID_ID);
            headers.put("WNGS-Seal", EncryptionByMD5.getMD5((EncryptionByMD5.getMD5(("wngs" + ANDROID_ID).getBytes())).getBytes()));
        }
        return headers;
    }

    /**
     * WebView请求的User-Agent
     * @return
     */
    public String getWebViewUserAgent() {
        StringBuilder label = new StringBuilder();
        label.append("WNGS");
        label.append(" ");
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(WngsApplication.getInstance().getPackageName(), 0);
            label.append(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
        }
        label.append(" ");
        label.append(System.getProperty("http.agent"));
        return label.toString();
    }


    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }
//    public static void initImageLoader(Context context) {
//        // This configuration tuning is custom. You can tune every option, you may tune some of them,
//        // or you can create default configuration by
//        //  ImageLoaderConfiguration.createDefault(this);
//        // method.
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//                .threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
////                .writeDebugLogs() // Remove for release app
//                .build();
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config);
//    }
}
