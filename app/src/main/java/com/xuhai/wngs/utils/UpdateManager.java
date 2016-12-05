package com.xuhai.wngs.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WngsApplication;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Administrator on 2014/10/20.
 */
public class UpdateManager {

    public final String APK_NAME = "wngs.apk";
    public final String DIR_DOWNLOAD = "/download";

    String version_number;
    String updateforce;
    String download_url;
    String express,info;
    Context mContext;
    RequestQueue queue;
    private ProgressBar mProgress;
    private TextView tv_progress;
    private String mSavePath;
    private int progress;
    /* 是否取消更新 */

    private Dialog mDownloadDialog;
    private boolean cancelUpdate = false;
    String className;
    String mupdatetan;

    public UpdateManager(Context context, String updatetan, String version_number,String updateforce,String download_url) {
        mContext = context;
        this.version_number = version_number;
        this.updateforce = updateforce;
        this.download_url = download_url;
        mupdatetan = updatetan;
        queue = Volley.newRequestQueue(mContext);

        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        className = info.topActivity.getClassName();
    }

    public void update(){

        if (getAppVersionName(mContext).compareTo(version_number) < 0) {
            showNoticeDialog();
        } else {
            if (mupdatetan.equals("dianji")) {
                CustomToast.showToast(mContext, "当前版本已为最新", 1000);
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 1:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    tv_progress.setText(progress + "%");
                    break;
                case 2:
                    // 安装文件
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };





    public boolean isUpdateforce() {
        if (updateforce.equals("1")) {
            return true;
        } else if (updateforce.equals("0")) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        // 构造对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("版本更新");
        builder.setMessage("有新的版本,是否更新？");
        // 更新
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
                showDownloadDialog();
            }

        });
        builder.setCancelable(false);
        if (isUpdateforce()) {
            // 稍后更新
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WngsApplication.getInstance().exit();
                    dialog.dismiss();
                    //  GjjApplication.getInstance().exit();
                }
            });
        } else {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false; // 默认返回 false
                }
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }


    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("下载文件");
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.activity_softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        tv_progress = (TextView) v.findViewById(R.id.tv_progress);
        builder.setView(v);
        builder.setCancelable(false);
        // 取消更新
        if (isUpdateforce()) {
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    WngsApplication.getInstance().exit();
                    // 设置取消状态
                    cancelUpdate = true;
                }
            });
        } else {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // 设置取消状态
                    cancelUpdate = true;

                    dialog.dismiss();
                }
            });
        }
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false; // 默认返回 false
                }
            }
        });

        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // 现在文件
        downloadApk();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     * @author coolszy
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    mSavePath = Environment.getExternalStorageDirectory() + DIR_DOWNLOAD;
                    URL url = new URL(download_url);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10 * 1000);// 设置超时时间
                    conn.setRequestMethod("GET");
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, APK_NAME);
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        handler.sendEmptyMessage(1);
                        if (numread <= 0) {
                            // 下载完成
                            handler.sendEmptyMessage(2);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }

    ;

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, APK_NAME);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        WngsApplication.getInstance().exit();
        //   GjjApplication.getInstance().exit();
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

}
