package com.xuhai.wngs.ui.imgchoose;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.ImageUtils;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoPreviewActivity extends Activity {

    private String imgkey;
    private String imgtemp;

    private ImageView imageView;
    private ProgressBar progressBar;
//    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(false);
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dark_translucent));
        }

        imgkey = getIntent().getStringExtra("imgkey");
        imgtemp = getIntent().getStringExtra("img_temp_name");

        imageView = (ImageView) findViewById(R.id.image);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        PicassoTrustAll.getInstance(this).load(ImageUtils.saveFileBitmap("temp_preview_img.jpg", ImageUtils.compressImage(imgkey, 480, 800))).skipMemoryCache().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.GONE);
            }
        });

//        PicassoTrustAll.getInstance(this).load(new File(imgkey)).into(imageView);

//        mAttacher = new PhotoViewAttacher(imageView);

//        mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {

            ImageUtils.deleteTempImageFile(imgtemp);

            Intent intent = new Intent();
            intent.putExtra("imgkey", imgkey);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mAttacher.cleanup();
    }
}
