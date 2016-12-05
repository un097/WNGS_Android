package com.xuhai.wngs.ui.shzl;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;

public class ShzlBldQRDDBzActivity extends BaseActionBarAsUpActivity {
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bld_qrddbz);
        message=(EditText)findViewById(R.id.message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shzl_bld_qrddbz, menu);
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
                Intent intent=new Intent();
                intent.putExtra("message",message.getText().toString().trim());
                setResult(RESULT_OK,intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
