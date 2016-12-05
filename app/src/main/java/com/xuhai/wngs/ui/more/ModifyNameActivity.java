package com.xuhai.wngs.ui.more;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.views.CustomToast;

public class ModifyNameActivity extends BaseActionBarAsUpActivity {

    private EditText nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_name);
        nickname = (EditText) findViewById(R.id.nickname);
        nickname.setText(getIntent().getStringExtra("nickname"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_name, menu);
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
                if (nickname.getText().toString().trim() == null || nickname.getText().toString().trim().equals("")) {
                    CustomToast.showToast(ModifyNameActivity.this, "昵称不能为空", 1000);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("nickname", nickname.getText().toString().trim());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
