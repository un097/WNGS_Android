package com.xuhai.wngs.ui.more;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.http.LoadControler;
import com.android.http.RequestManager;
import com.android.http.RequestMap;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.ui.imgchoose.ImageChooseActivity;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.utils.ImageUtils;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class MyinfoActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {

    public final String TAG = "MyinfoActivity";

    public final int CHOOSE_IMAGE =  0, CROP_HEAD = 1;
    public final static String IMAGE_MAP = "image_map";

    private String image_head;

    private ProgressDialogFragment newFragment;

    private TextView nickname, phone, message;
    private ImageView tuichu;
    CircleImageView img;
    private Bitmap headBitmap;
    private String headPath;
    private boolean isHead = false; //是否修改头像
    private LinearLayout nickname_ll, message_ll,anquan_ll,upate_ll;

    private LoadControler mLoadControler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        if (getActionBar() != null) {
            getActionBar().setTitle("个人信息");
        }
        nickname = (TextView) findViewById(R.id.nickname);
        phone = (TextView) findViewById(R.id.phone);
        message = (TextView) findViewById(R.id.message);
        img = (CircleImageView) findViewById(R.id.img);
        img.setOnClickListener(this);
        nickname_ll = (LinearLayout) findViewById(R.id.nickname_ll);
        message_ll = (LinearLayout) findViewById(R.id.message_ll);
        anquan_ll = (LinearLayout) findViewById(R.id.anquan_ll);
        upate_ll = (LinearLayout) findViewById(R.id.upate_ll);

        if (IS_BANKPWD){
            upate_ll.setVerticalGravity(View.GONE);
        }

        tuichu = (ImageView) findViewById(R.id.tuichu);
        nickname.setText(NICK_NAME);
        phone.setText(USER_PHONE);
        message.setText(USER_NOTE);
        if (USER_HEAD == null || USER_HEAD.equals("")) {
            img.setImageResource(R.drawable.ic_more_camera);
        } else {
            PicassoTrustAll.getInstance(MyinfoActivity.this).load(USER_HEAD).placeholder(R.drawable.ic_more_camera).error(R.drawable.ic_more_camera).into(img);
        }
        nickname_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyinfoActivity.this, ModifyNameActivity.class);
                intent.putExtra("nickname", nickname.getText().toString().trim());
                startActivityForResult(intent, 5);
            }
        });
        message_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyinfoActivity.this, ModifyMessageActivity.class);
                intent.putExtra("message", message.getText().toString().trim());
                startActivityForResult(intent, 4);
            }
        });
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Dianjill.isFastDoubleClick()) {
                    return;
                }
                zhuXiao();
            }
        });
        anquan_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IS_BANKPWD){
                    Intent intent = new Intent(MyinfoActivity.this, MorePayPwd1Activity.class);
                    startActivity(intent);
                }else {
                    if (IS_BANKCHECK){
                        Intent intent = new Intent(MyinfoActivity.this, MorePayPwd2Activity.class);
                        startActivity(intent);
//                    }
                    }else {
                        Intent intent = new Intent(MyinfoActivity.this, MorePayPwd1Activity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        upate_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyinfoActivity.this, MorePayPwdUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void zhuXiao() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("提示");
        mMaterialDialog.setMessage("是否注销?");
        mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putString(SPN_USER_PHONE, "");
                editor.putString(SPN_UID, "");
                editor.putString(SPN_USER_HEAD, "");
                editor.putString(SPN_NICK_NAME, "");
                editor.putString(SPN_USER_NOTE, "");
                editor.putString(SPN_AUTH_NAME, "");
                editor.putString(SPN_AUTH_PHONE, "");
                editor.putString(SPN_AUTH_BUILDING, "");
                editor.putString(SPN_AUTH_UNIT, "");
                editor.putString(SPN_AUTH_ROOM, "");
                editor.putBoolean(SPN_IS_LOGIN, false);
                editor.putBoolean(SPN_USER_CHECKIN, false);
                editor.putBoolean(SPN_AUTH, false);
                editor.putString(SPN_POINTS_TOTLA, "");
                editor.putString(SPN_EXPRESS, "");
                editor.putString(SPN_INFO, "");
                editor.putString(SPN_BBS, "");
                editor.putBoolean(SPN_IS_BANKCHECK,false);
                editor.putBoolean(SPN_IS_BANKPWD,false);
                editor.putString(SPN_BALANCE,"");
                editor.putString(SPN_USER_PASSWD,"");
                editor.commit();

                database.delete("shopcart", null, null);
                database.close();

                MyinfoActivity.this.setResult(LOGIN_SUCCESS);
                finish();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_myinfo, menu);
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
                break;
            case R.id.yes:
                httpPost(HTTP_XGXX);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void httpPost(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        RequestMap params = new RequestMap();

        params.put("nickname", nickname.getText().toString().trim());
        params.put("uid", UID);
        if (isHead) {
            params.put("head", ImageUtils.saveFileBitmap("head.jpg", ImageUtils.compressImage(headPath, 200, 200)));
        }
        params.put("note", message.getText().toString().trim());
        mLoadControler = RequestManager.getInstance().post(url, params, requestListener, 0);

    }

    private RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onSuccess(String response, String url, int actionId) {
            ImageUtils.deleteFileBitmap();
            String recode, msg = null;
            try {
                JSONObject json = new JSONObject(response);
                if (json.has("recode")) {
                    recode = json.getString("recode");
                    msg = json.getString("msg");

                    if (recode.equals("0")) {

                        if (isHead && json.has("head") ) {
                            editor.putString(SPN_USER_HEAD, AESEncryptor.encrypt(json.getString("head")));
                            editor.commit();
                        }
                        if (json.has("nickname")) {
                            editor.putString(SPN_NICK_NAME, AESEncryptor.encrypt(json.getString("nickname")));
                            editor.commit();
                        }
                        if (json.has("note")) {
                            editor.putString(SPN_USER_NOTE, AESEncryptor.encrypt(json.getString("note")));
                            editor.commit();
                        }

                        newFragment.dismiss();
                        CustomToast.showToast(MyinfoActivity.this, "修改成功", 1000);

                        setResult(LOGIN_SUCCESS);
                        finish();
                    } else {
                        CustomToast.showToast(MyinfoActivity.this, msg, 1000);
                    }
                } else {
                    CustomToast.showToast(MyinfoActivity.this, R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            } catch (Exception e) {
                newFragment.dismiss();
                CustomToast.showToast(MyinfoActivity.this, R.string.http_fail, 1000);
            }
        }

        @Override
        public void onError(String errorMsg, String url, int actionId) {
            newFragment.dismiss();
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img:
                Intent intent = new Intent();
                intent.setClass(MyinfoActivity.this, ImageChooseActivity.class);
                intent.putExtra("image_count", 2);
                startActivityForResult(intent, CHOOSE_IMAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == RESULT_OK) {
            if (data.getStringExtra("message") == null || data.getStringExtra("message").equals("")) {
            } else {
                message.setText(data.getStringExtra("message"));
            }
        } else if (requestCode == 5 && resultCode == RESULT_OK) {
            if (data.getStringExtra("nickname") == null || data.getStringExtra("nickname").equals("")) {
            } else {
                nickname.setText(data.getStringExtra("nickname"));
            }
        } else if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {
            Map<String, String> imageMap = new HashMap<String, String>();
            imageMap.putAll((Map<String, String>) data.getSerializableExtra(IMAGE_MAP));

            Iterator iter = imageMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                image_head = entry.getValue().toString();
            }

            startCropHeadActivity(image_head);
        } else if (requestCode == CROP_HEAD && resultCode == RESULT_OK) {

            isHead = true;
            headPath = data.getStringExtra("head_path");
            img.setImageBitmap(ImageUtils.compressImage(headPath, 200, 200));
        }
    }

    public void startCropHeadActivity(String uri) {
        Intent intent = new Intent();
        intent.setClass(MyinfoActivity.this, MoreInfoCropHeadActivity.class);
        intent.putExtra("image_head", uri);
        startActivityForResult(intent, CROP_HEAD);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
