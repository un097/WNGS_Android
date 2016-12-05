package com.xuhai.wngs.ui.more;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.BaseActivity;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.utils.DataCleanManager;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.utils.UpdateManager;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ListViewForScrollView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import me.drakeet.materialdialog.MaterialDialog;

public class MoreGYWMActivity extends BaseActionBarAsUpActivity {
    private ListView listView;
    private    List<HashMap<String, String>> data;
    private MaterialDialog mMaterialDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_gywm);

        initView();
        listView = (ListView) findViewById(R.id.listView);

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,data,R.layout.item_list_more_gywm,new String[]{"title"},new int[]{R.id.text_tv});

        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MoreGYWMActivity.this, WebActivity.class);
                        intent.putExtra("url", Constants.HTTP_FEEDBACK + "?sqid=" + SQID + "&uid=" + UID + "&iora=a");
                        intent.putExtra("title", "意见反馈");
                        startActivity(intent);
                        break;
                    case 1:
                        if (Dianjill.isFastDoubleClick()) {
                            return;
                        }

                        checkUpate("http://shequ.haixusoft.com/api/version.php?iora=a");
//                        UpdateManager manager = new UpdateManager(MoreGYWMActivity.this, "dianji");
//                        manager.checkUpate("http://shequ.haixusoft.com/api/version.php?iora=a");
                        break;
                    case 2:

                         mMaterialDialog = new MaterialDialog(MoreGYWMActivity.this)
                                .setTitle("提示")
                                .setMessage("确定清除缓存？")
                                .setPositiveButton("是", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                        DataCleanManager.clearAllCache(MoreGYWMActivity.this);
                                    }
                                })
                                .setNegativeButton("否", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                });

                        mMaterialDialog.show();

                        break;
                    case 3:
                        intent = new Intent(MoreGYWMActivity.this, WebActivity.class);
                        intent.putExtra("title", "关于我们");
                        intent.putExtra("url", Constants.HTTP_ABOUT);
                        startActivity(intent);
                        break;
                    case 4:
                        if (Dianjill.isFastDoubleClick()) {
                            return;
                        }
                        showShare();
                        break;
                }
            }
        });
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

                            UpdateManager manager = new UpdateManager(MoreGYWMActivity.this, "dianji",response.getString("version"),response.getString("upgrade"), response.getString("url"));
                            manager.update();


                        } else {
                            CustomToast.showToast(MoreGYWMActivity.this, msg, 1000);
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

    private void initView(){
        String[] words = new String[]{"意见反馈","版本更新","清除缓存","关于我们","分享给朋友"};

        data=new ArrayList<HashMap<String,String>>();
        for (int i = 0; i<words.length;i++){
            HashMap<String, String> hashMap=new HashMap<String, String>();
            hashMap.put("title",words[i]);
            data.add(hashMap);
        }
    }

    private void showShare() {
        ShareSDK.initSDK(MoreGYWMActivity.this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://shequ.haixusoft.com/shareapp.php");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("蜗牛公社下载地址  http://shequ.haixusoft.com/shareapp.php");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("android.resource://com.xuhai.wngs/" + R.drawable.ic_launcher);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://shequ.haixusoft.com/shareapp.php");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://shequ.haixusoft.com/shareapp.php");

        oks.setImageUrl("http://shequ.haixusoft.com/css/images/logo.png");

        // 启动分享GUI
        oks.show(MoreGYWMActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_gywm, menu);
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
