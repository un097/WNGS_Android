package com.xuhai.wngs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushManager;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.push.Utils;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.ArrayList;
import java.util.List;


public class ShequActivity extends BaseActionBarAsUpActivity {
    private ImageView choose, img;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shequ);
        if (getActionBar() != null) {
            getActionBar().setTitle(R.string.title_activity_shequ);
        }
        choose = (ImageView) findViewById(R.id.choose);
        img = (ImageView) findViewById(R.id.img);
        title = (TextView) findViewById(R.id.title);
        title.setText(SQNAME);
        if (SQIMG == null || SQIMG.equals("")) {
            img.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(ShequActivity.this).load(SQIMG).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(img);
        }
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                List<String> tags = Utils.getTagsList(SQID);
//                PushManager.delTags(ShequActivity.this, tags);

                Intent intent = new Intent();
                intent.setClass(ShequActivity.this, ShequListActivity.class);
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 100) {
            setResult(RESULT_OK);
            finish();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shequ, menu);
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
}
