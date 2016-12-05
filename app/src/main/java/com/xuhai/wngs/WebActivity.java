package com.xuhai.wngs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
import com.squareup.okhttp.internal.Util;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.drakeet.materialdialog.MaterialDialog;


public class WebActivity extends BaseActionBarAsUpActivity {

    private WebView webView;
    private ProgressDialogFragment newFragment;
    public String url = "";
    String tel;
    ValueCallback<Uri> mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        if (getActionBar() != null) {
            getActionBar().setTitle(getIntent().getStringExtra("title"));
        }

        url = getIntent().getStringExtra("url");
        dialog();
        initView();
    }

    private void dialog() {
//        FragmentManager fragmentManager = getFragmentManager();
        newFragment = new ProgressDialogFragment();
//        new ProgressDialogFragment();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        // 指定一个过渡动画
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        // 作为全屏显示,使用“content”作为fragment容器的基本视图,这始终是Activity的基本视图
//        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    private void initView() {
        webView = (WebView) this.findViewById(R.id.webView);

        webView.freeMemory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);// 让浏览器来存储页面元素的DOM模型，从而使JavaScript可以执行操作了。
        webView.getSettings().setLoadsImagesAutomatically(true);//自动加载图片

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);//自动适应屏幕

        webView.requestFocus();
        webView.getSettings().setUserAgentString(WngsApplication.getInstance().getWebViewUserAgent());
        webView.loadUrl(url, WngsApplication.getInstance().getWebViewHeaders());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
//                    newFragment.dismissAllowingStateLoss();
newFragment.dismiss();
                } else {
                    // 加载中
//                    if (newFragment == null) {
//                        dialog();
//                    }
                    if (!newFragment.isVisible())
                    newFragment.show(getFragmentManager(),"1");
                }
                super.onProgressChanged(view, newProgress);
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                this.openFileChooser(uploadMsg, acceptType, null);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                this.openFileChooser(uploadMsg, null, null);
            }

// For Android > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
//文件选择
                showMenu4InputFile("image/*");

            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                if (url != null && "htt".equals(url.substring(0, 3))) {
                    webView.loadUrl(url);

                } else if (url != null && "tel".equals(url.substring(0, 3))) {
                    tel = url;
                    call();
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
//                super.onReceivedSslError(view, handler, error);

            }
        });
    }

    /**
     * 选择文件为客户端的Input File功能
     */
    private void chooseFile4InputFile(String acceptType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE); //能够返回一个Uri结果
        if (acceptType.equals("")) {
            acceptType = "*/*"; //选择的文件类型，例如：image/*表示图片
        }
        intent.setType(acceptType);
//        startActivityForResult(Intent.createChooser(intent, "File Chooser"), 4);
        startActivityForResult(intent, 4);
    }

    /**
     * 拍照为客户端的Input File功能
     */
    private void takePicture4InputFile() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 3);
    }

    private void showMenu4InputFile(final String acceptType) {
        String[] menus = {"拍照", "相册"};
        Dialog dialog = new AlertDialog.Builder(this).setItems(menus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takePicture4InputFile();
                        break;
                    case 1:
                        chooseFile4InputFile(acceptType);
                        break;
                    default:
                        break;
                }
            }
        }).create();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
        });
        dialog.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 3 && this.mUploadMessage != null) {
//选择文件时的拍照功能
            Uri uri = null;
            if (resultCode == Activity.RESULT_OK && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream fos = null;
                try {
                    File file = Environment.getExternalStorageDirectory();
                    file = new File(file, "51zhangdan/temp");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(file, "filechoosertmp.png");
                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    fos = null;
                    uri = Uri.fromFile(file);
                } catch (Exception e) {
                    Log.e("web", String.format("处理TakePicture图片数据出错.%s", e.getMessage()));
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            Log.e("web", String.format("关闭TakePicture图片数据出错.%s", e.getMessage()));
                        }
                    }
                }
            }
            //这里进行回调，WebView才能获取到你选择了哪个文件
            this.mUploadMessage.onReceiveValue(uri);
            this.mUploadMessage = null;
        } else if (requestCode == 4) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    protected void call() {

        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("提示");
        mMaterialDialog.setMessage("确认拨打?");
        mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setNegativeButton("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.show();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        webView.setWebChromeClient(null);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (webView.canGoBack()) {
            if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}