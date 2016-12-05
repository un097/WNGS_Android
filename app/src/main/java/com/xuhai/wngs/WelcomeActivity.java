package com.xuhai.wngs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.xuhai.wngs.ui.main.MainCitySelActivity;

import java.util.Timer;
import java.util.TimerTask;


public class WelcomeActivity extends BaseActionBarAsUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (SQID.equals("")) {
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
//				// TODO Auto-generated method stub
                    Intent intent1 = new Intent(WelcomeActivity.this, MainCitySelActivity.class);
                    WelcomeActivity.this.startActivity(intent1);
                    WelcomeActivity.this.finish();
                }
            }, 500);
        } else {

            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
//				// TODO Auto-generated method stub


                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }

            }, 500);
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 如果是返回键,直接返回到桌面
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }


}


