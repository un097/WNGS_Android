package com.xuhai.wngs.ui.shzl;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShzlBbsPostActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {

    public final String TAG = "ShzlBbsPostActivity";

    public final static String IMAGE_MAP = "image_map";
    private Map<String, String> imageMap = new HashMap<String, String>();
    private String[] imgParams = new String[]{"img1", "img2", "img3", "img4", "img5"};
    private List<String> imageList = new ArrayList<String>();
    private boolean isChooseOne = false, isChooseTwo = false, isChooseThree = false;

    public final int CHOOSE_IMAGE = 0, CLICK_IMAGE = 1;

    EditText et_title, et_content;
    ImageView ok;
    ImageView iv1, iv2, iv3;

    private Intent chooseIntent;

    private LoadControler mLoadControler = null;

    private ProgressDialogFragment newFragment;
    private String bid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bbs_post);
        if (getActionBar()!=null){
            getActionBar().setTitle(getIntent().getStringExtra("title"));
        }
   bid=getIntent().getStringExtra("bid");
        initView();
    }

    private void initView() {
        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv1.setOnClickListener(this);
        iv2 = (ImageView) findViewById(R.id.iv2);
        iv2.setOnClickListener(this);
        iv3 = (ImageView) findViewById(R.id.iv3);
        iv3.setOnClickListener(this);
        ok = (ImageView) findViewById(R.id.ok_fatie);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().equals("")) {
                    CustomToast.showToast(ShzlBbsPostActivity.this, "请输入内容", 1000);
                } else if (et_title.getText().toString().equals("")) {
                    CustomToast.showToast(ShzlBbsPostActivity.this, "请输入标题", 1000);
                } else {
                    httpPost(HTTP_BBS_POST);
                }
            }
        });
    }

    public void updateView() {
        if (imageList.size() <= 0) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.GONE);
            iv3.setVisibility(View.GONE);

            iv1.setImageResource(R.drawable.image_wyfw_tsbx_post_camera);

            isChooseOne = false;
            isChooseTwo = false;
            isChooseThree = false;

        } else if (imageList.size() > 0 && imageList.size() == 1) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.GONE);

            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img1.jpg", ImageUtils.compressImage(imageList.get(0), 100, 100))).skipMemoryCache().into(iv1);
            iv2.setImageResource(R.drawable.image_wyfw_tsbx_post_camera);

            isChooseOne = true;
            isChooseTwo = false;
            isChooseThree = false;
        } else if (imageList.size() == 2) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);

            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img1.jpg", ImageUtils.compressImage(imageList.get(0), 100, 100))).skipMemoryCache().into(iv1);
            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img2.jpg", ImageUtils.compressImage(imageList.get(1), 100, 100))).skipMemoryCache().into(iv2);
            iv3.setImageResource(R.drawable.image_wyfw_tsbx_post_camera);

            isChooseOne = true;
            isChooseTwo = true;
            isChooseThree = false;
        } else if (imageList.size() == 3) {
            iv1.setVisibility(View.VISIBLE);
            iv2.setVisibility(View.VISIBLE);
            iv3.setVisibility(View.VISIBLE);

            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img1.jpg", ImageUtils.compressImage(imageList.get(0), 100, 100))).skipMemoryCache().into(iv1);
            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img2.jpg", ImageUtils.compressImage(imageList.get(1), 100, 100))).skipMemoryCache().into(iv2);
            PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_img3.jpg", ImageUtils.compressImage(imageList.get(2), 100, 100))).skipMemoryCache().into(iv3);

            isChooseOne = true;
            isChooseTwo = true;
            isChooseThree = true;
        }
    }

    public void httpPost(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        RequestMap params = new RequestMap();
        params.put("sqid", SQID);
        params.put("uid", UID);
        params.put("bid", bid);
        params.put("title", et_title.getText().toString().trim());
        params.put("content", et_content.getText().toString().trim());
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
                        CustomToast.showToast(ShzlBbsPostActivity.this,"提交成功",1000);
                        setResult(RESULT_OK);
                        finish();
                        newFragment.dismiss();
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(ShzlBbsPostActivity.this, msg, 1000);
                        ok.setOnClickListener(ShzlBbsPostActivity.this);
                    }

                } else {
                    ok.setOnClickListener(ShzlBbsPostActivity.this);
                }

            } catch (Exception e) {
                ok.setOnClickListener(ShzlBbsPostActivity.this);
            }


        }

        @Override
        public void onError(String errorMsg, String url, int actionId) {
            newFragment.dismiss();
            ok.setOnClickListener(ShzlBbsPostActivity.this);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shzl_bbs_post, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv1:
                if (isChooseOne) {
                    Intent intent = new Intent();
                    intent.setClass(ShzlBbsPostActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("imgkey", imageList.get(0));
                    intent.putExtra("img_temp_name", "temp_img1.jpg");
                    startActivityForResult(intent, CLICK_IMAGE);
                } else {
                    chooseIntent = new Intent(ShzlBbsPostActivity.this, ImageChooseActivity.class);
                    chooseIntent.putExtra("image_count", imageList.size());
                    startActivityForResult(chooseIntent, CHOOSE_IMAGE);
                }
                break;
            case R.id.iv2:
                if (isChooseTwo) {
                    Intent intent = new Intent();
                    intent.setClass(ShzlBbsPostActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("imgkey", imageList.get(1));
                    intent.putExtra("img_temp_name", "temp_img2.jpg");
                    startActivityForResult(intent, CLICK_IMAGE);
                } else {
                    chooseIntent = new Intent(ShzlBbsPostActivity.this, ImageChooseActivity.class);
                    chooseIntent.putExtra("image_count", imageList.size());
                    startActivityForResult(chooseIntent, CHOOSE_IMAGE);
                }
                break;
            case R.id.iv3:
                if (isChooseThree) {
                    Intent intent = new Intent();
                    intent.setClass(ShzlBbsPostActivity.this, PhotoPreviewActivity.class);
                    intent.putExtra("imgkey", imageList.get(2));
                    intent.putExtra("img_temp_name", "temp_img3.jpg");
                    startActivityForResult(intent, CLICK_IMAGE);
                } else {
                    chooseIntent = new Intent(ShzlBbsPostActivity.this, ImageChooseActivity.class);
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

    @Override
    protected void onDestroy() {
        ImageUtils.deleteFileBitmap();
        super.onDestroy();
    }
}
