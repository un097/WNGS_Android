package com.xuhai.wngs.ui.imgchoose;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.BaseActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.ui.imgchoose.adapter.ImageGridAdapter;
import com.xuhai.wngs.views.CustomToast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ImageChooseActivity extends BaseActionBarAsUpActivity implements AdapterView.OnItemClickListener, View.OnClickListener, PhotoBucketWindow.OnBucketSelectedListener {

    public final String TAG = "ImageChooseActivity";
    public final static String IMAGE_MAP = "image_map";

    public final int TAKE_PHOTO = 0, SELECT_PHOTO = 1;

    public Map<String, String> photoMap;

    private int imageCount = 0;

    private List<ImageItem> dataList = new ArrayList<ImageItem>();
    private GridView gridView;
    private ImageGridAdapter adapter;
    private AlbumHelper helper;
    private PhotoBucketWindow bucketWindow;

    private LinearLayout anchor;
    private Button btn_bucket;

    String fileName;// 文件名，路径
    String dirPath;//
    String takePath = "";
    public static Bitmap def;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_choose);

        BitmapBucket.max = BitmapBucket.MAX_COUNT - getIntent().getIntExtra("image_count", 0);

        def = BitmapFactory.decodeResource(getResources(),
                R.drawable.img_default_photo);

        initView();

        handler.sendEmptyMessage(1);
    }

    private void initView() {
        helper = AlbumHelper.getHelper();
        helper.init(this);

        anchor = (LinearLayout) findViewById(R.id.layout_footer);
        btn_bucket = (Button) findViewById(R.id.btn_bucket);
        btn_bucket.setOnClickListener(this);

        gridView = (GridView) findViewById(R.id.image_grid);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setOnItemClickListener(this);

        bucketWindow = new PhotoBucketWindow(this, anchor,
                helper.getImageBucketList(true), handler, screenWidth,
                (int) (screenHeight * 0.618));
        bucketWindow.setSelectedListener(this);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    CustomToast.showToast(ImageChooseActivity.this, "最多选择" + BitmapBucket.max + "张图片", 1000);
                    return true;
                case 1:
                    dataList.clear();
                    dataList.addAll(helper.getAllImageList(true));
                    adapter = new ImageGridAdapter(ImageChooseActivity.this, dataList, handler);
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
//                    gridView.setOnItemClickListener(ImageChooseActivity.this);
                    adapter.setTextCallBack(new ImageGridAdapter.TextCallBack() {

                        @Override
                        public void onListen(int count) {
//                          btn.setText("完成(" + count + "/" + BitmapBucket.max +
                            imageCount = count;
                            getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
                        }
                    });
                    break;
            }
            return false;
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_choose, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem completeItem = menu.findItem(R.id.action_complete);
        completeItem.setTitle("完成(" + imageCount + "/" + BitmapBucket.max + ")");
        return super.onPrepareOptionsMenu(menu);
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
        } else if (id == R.id.action_complete) {
            Intent intent = new Intent();
            intent.putExtra(IMAGE_MAP, (Serializable) adapter.map);
            setResult(RESULT_OK, intent);
            finish();
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBucketSelected(List<ImageItem> imageList) {
        dataList.clear();
        dataList.addAll(imageList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bucket:
                bucketWindow.show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        if (position == 0) {
//            Log.d(TAG, "gridview item === " + position);
//        }
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {

            photoMap.put(takePath, takePath);

            Intent intent = new Intent();
            intent.putExtra(IMAGE_MAP, (Serializable) photoMap);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void takePhoto() {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            photoMap = new HashMap<String, String>();

            fileName = getFileName();
            System.out.println(Environment.getExternalStorageDirectory()
                    .toString());
            System.out.println(Environment.getExternalStorageDirectory()
                    .getAbsolutePath());
            dirPath = Environment.getExternalStorageDirectory().getPath()
                    + "/temp";
            File tempFile = new File(dirPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            File saveFile = new File(tempFile, fileName + ".jpg");
            takePath = saveFile.getPath();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
            startActivityForResult(intent, TAKE_PHOTO);
        } else {
            Toast.makeText(ImageChooseActivity.this, "未检测到SD卡，拍照不可用!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName() {
        StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        long millis = calendar.getTimeInMillis();
        String[] dictionaries = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9"};
        sb.append("dzc");
        sb.append(millis);
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            sb.append(dictionaries[random.nextInt(dictionaries.length - 1)]);
        }
        return sb.toString();
    }

    ;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        BitmapBucket.pathList.clear();
//        dataList.clear();
//        dataList.addAll(helper.getAllImageList(true));
//        adapter = new ImageGridAdapter(this, dataList, handler);
//        gridView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        gridView.setOnItemClickListener(this);
//        adapter.setTextCallBack(new ImageGridAdapter.TextCallBack() {
//
//            @Override
//            public void onListen(int count) {
////                btn.setText("完成(" + count + "/" + BitmapBucket.max + ")");
//                getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
//            }
//        });
    }
}
