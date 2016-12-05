package com.xuhai.wngs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class GuideActivity extends BaseActionBarAsUpActivity {

    Intent intent1, intent2;
    public static final int GO_GUIDE = 1, GO_WELCOME = 2;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {

                case GO_WELCOME:
                    goWelcome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
            super.handleMessage(msg);
        }

        private void goGuide() {
            // TODO Auto-generated method stub
            intent1 = new Intent(GuideActivity.this, GuideVPActivity.class);
            startActivity(intent1);
            finish();
        }

        private void goWelcome() {
            // TODO Auto-generated method stub
            intent2 = new Intent(GuideActivity.this, WelcomeActivity.class);
            startActivity(intent2);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        IS_FIRST_OPEN = spn.getBoolean(SPN_IS_FIRST_OPEN, true);

        if (IS_FIRST_OPEN) {
            handler.sendEmptyMessage(GO_GUIDE);
        } else {
            handler.sendEmptyMessage(GO_WELCOME);
        }
    }


}

