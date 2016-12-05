package com.xuhai.wngs.ui.more;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.edmodo.cropper.CropImageView;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MoreInfoCropHeadActivity extends BaseActionBarAsUpActivity {

    CropImageView cropImageView;

    public String image_head;

    public Bitmap croppedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_crop_head);

        image_head = getIntent().getStringExtra("image_head");

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
//        cropImageView.setImageBitmap(BitmapFactory.decodeFile(image_head));
        cropImageView.setImageBitmap(ImageUtils.compressImage(image_head, screenWidth, screenWidth * 16 / 9));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_info_crop_head, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_complete) {

            Intent intent = new Intent();
            intent.putExtra("head_path", ImageUtils.saveFileBitmap("header.jpg", cropImageView.getCroppedImage()).getPath());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
