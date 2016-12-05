package com.xuhai.wngs.ui.wyfw;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.http.LoadControler;
import com.android.http.RequestManager;
import com.android.http.RequestMap;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.ui.imgchoose.ImageChooseActivity;
import com.xuhai.wngs.ui.imgchoose.PhotoPreviewActivity;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.utils.ImageUtils;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WyfwTsbxPostActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {

    public final String TAG = "WyfwTsbxPostActivity";

    public final static String IMAGE_MAP = "image_map";
    private Map<String, String> imageMap = new HashMap<String, String>();
    private String[] imgParams = new String[]{"img1", "img2", "img3", "img4", "img5"};
    private List<String> imageList = new ArrayList<String>();
    private boolean isChooseOne = false, isChooseTwo = false, isChooseThree = false;

    public final int CHOOSE_IMAGE = 0, CLICK_IMAGE = 1;

    private EditText et_phone, et_content;
    private ImageView camera_one, camera_two, camera_three, confirmation, image;

    private Intent chooseIntent;

    private ProgressDialogFragment newFragment;

    private LoadControler mLoadControler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wyfw_tsbx_post);

        initView();
    }

    public void initView() {

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_phone.setText(USER_PHONE);
        et_content = (EditText) findViewById(R.id.et_content);
        camera_one = (ImageView) findViewById(R.id.camera_one);
        camera_one.setOnClickListener(this);
        camera_two = (ImageView) findViewById(R.id.camera_two);
        camera_two.setOnClickListener(this);
        camera_three = (ImageView) findViewById(R.id.camera_three);
        camera_three.setOnClickListener(this);
        confirmation = (ImageView) findViewById(R.id.confirmation);
        confirmation.setOnClickListener(this);

    }

    public void updateView() {
        if (imageList.size() <= 0) {
            camera_one.setVisibility(View.VISIBLE);
            camera_two.setVisibility(View.GONE);
            camera_three.setVisibility(View.GONE);

            camera_one.setImageResource(R.drawable.image_wyfw_tsbx_post_camera);

            isChooseOne = false;
            isChooseTwo = false;
            isChooseThree = false;

        } else if (imageList.size() > 0 && imageList.size() == 1) {
            camera_one.setVisibility(View.VISIBLE);
            camera_two.setVisibility(View.VISIBLE);
            camera_three.setVisibility(View.GONE);

            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img1.jpg", ImageUtils.compressImage(imageList.get(0), 100, 100))).skipMemoryCache().into(camera_one);
            camera_two.setImageResource(R.drawable.image_wyfw_tsbx_post_camera);

            isChooseOne = true;
            isChooseTwo = false;
            isChooseThree = false;
        } else if (imageList.size() == 2) {
            camera_one.setVisibility(View.VISIBLE);
            camera_two.setVisibility(View.VISIBLE);
            camera_three.setVisibility(View.VISIBLE);

            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img1.jpg", ImageUtils.compressImage(imageList.get(0), 100, 100))).skipMemoryCache().into(camera_one);
            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img2.jpg", ImageUtils.compressImage(imageList.get(1), 100, 100))).skipMemoryCache().into(camera_two);
            camera_three.setImageResource(R.drawable.image_wyfw_tsbx_post_camera);

            isChooseOne = true;
            isChooseTwo = true;
            isChooseThree = false;
        } else if (imageList.size() == 3) {
            camera_one.setVisibility(View.VISIBLE);
            camera_two.setVisibility(View.VISIBLE);
            camera_three.setVisibility(View.VISIBLE);

            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img1.jpg", ImageUtils.compressImage(imageList.get(0), 100, 100))).skipMemoryCache().into(camera_one);
            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img2.jpg", ImageUtils.compressImage(imageList.get(1), 100, 100))).skipMemoryCache().into(camera_two);
            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img3.jpg", ImageUtils.compressImage(imageList.get(2), 100, 100))).skipMemoryCache().into(camera_three);

            isChooseOne = true;
            isChooseTwo = true;
            isChooseThree = true;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wyfw_tsbx_post, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmation:
//                if (Dianjill.isFastDoubleClick()) {
//                    return;
//                }
                if (et_content.getText().toString().trim().equals("")) {
                    CustomToast.showToast(WyfwTsbxPostActivity.this, "请输入内容!", 1000);
                } else if (et_phone.getText().toString().trim().equals("")) {
                    CustomToast.showToast(WyfwTsbxPostActivity.this, "请输入电话号!", 1000);
                } else {
                    httpPost(HTTP_REPAIR);
                }
                break;
            case R.id.camera_one:
                if (isChooseOne) {
                    Intent intent = new Intent();
                    intent.setClass(WyfwTsbxPostActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("imgkey", imageList.get(0));
                    intent.putExtra("img_temp_name", "temp_img1.jpg");
                    startActivityForResult(intent, CLICK_IMAGE);
                } else {
                    chooseIntent = new Intent(WyfwTsbxPostActivity.this, ImageChooseActivity.class);
                    chooseIntent.putExtra("image_count", imageList.size());
                    startActivityForResult(chooseIntent, CHOOSE_IMAGE);
                }
                break;
            case R.id.camera_two:
                if (isChooseTwo) {
                    Intent intent = new Intent();
                    intent.setClass(WyfwTsbxPostActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("imgkey", imageList.get(1));
                    intent.putExtra("img_temp_name", "temp_img2.jpg");
                    startActivityForResult(intent, CLICK_IMAGE);
                } else {
                    chooseIntent = new Intent(WyfwTsbxPostActivity.this, ImageChooseActivity.class);
                    chooseIntent.putExtra("image_count", imageList.size());
                    startActivityForResult(chooseIntent, CHOOSE_IMAGE);
                }
                break;
            case R.id.camera_three:
                if (isChooseThree) {
                    Intent intent = new Intent();
                    intent.setClass(WyfwTsbxPostActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("imgkey", imageList.get(2));
                    intent.putExtra("img_temp_name", "temp_img3.jpg");
                    startActivityForResult(intent, CLICK_IMAGE);
                } else {
                    chooseIntent = new Intent(WyfwTsbxPostActivity.this, ImageChooseActivity.class);
                    chooseIntent.putExtra("image_count", imageList.size());
                    startActivityForResult(chooseIntent, CHOOSE_IMAGE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK) {

            imageMap.putAll((Map<String, String>) data.getSerializableExtra(IMAGE_MAP));

            imageList = new ArrayList<String>();
            Iterator iter = imageMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                imageList.add((String) entry.getValue());
            }
            updateView();
        } else if (requestCode == CLICK_IMAGE && resultCode == RESULT_OK) {
            imageMap.remove(data.getStringExtra("imgkey"));

            imageList = new ArrayList<String>();
            Iterator iter = imageMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                imageList.add((String) entry.getValue());
            }
            updateView();
        }
    }

    public void httpPost(String url) {

        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        RequestMap params = new RequestMap();
        params.put("sqid", SQID);
        params.put("uid", UID);
        params.put("info", et_content.getText().toString().trim());
        params.put("phone", et_phone.getText().toString().trim());
        for (int i = 0; i < imageList.size(); i++) {
            params.put(imgParams[i], ImageUtils.saveFileBitmap(imgParams[i] + ".jpg", ImageUtils.compressImage(imageList.get(i), 480, 800)));
        }
        mLoadControler = RequestManager.getInstance().post(url, params, requestListener, 0);
    }

    private RequestManager.RequestListener requestListener = new RequestManager.RequestListener() {
        @Override
        public void onRequest() {

        }

        @Override
        public void onSuccess(String response, String url, int actionId) {
            ImageUtils.deleteFileBitmap();
            try {
                JSONObject json = new JSONObject(response);
                String recode, msg;

                if (json.has("recode")) {
                    recode = json.getString("recode");
                    msg = json.getString("msg");

                    if (recode.equals("0")) {
                        CustomToast.showToast(WyfwTsbxPostActivity.this,"提交成功",1000);
                        setResult(RESULT_OK);
                        finish();
                        newFragment.dismiss();
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(WyfwTsbxPostActivity.this, msg, 1000);
                        confirmation.setOnClickListener(WyfwTsbxPostActivity.this);
                    }

                } else {
                    confirmation.setOnClickListener(WyfwTsbxPostActivity.this);
                }

            } catch (Exception e) {
                confirmation.setOnClickListener(WyfwTsbxPostActivity.this);
            }


        }

        @Override
        public void onError(String errorMsg, String url, int actionId) {
            newFragment.dismiss();
            confirmation.setOnClickListener(WyfwTsbxPostActivity.this);
        }
    };


    @Override
    protected void onDestroy() {
        ImageUtils.deleteFileBitmap();
        super.onDestroy();
    }
}
