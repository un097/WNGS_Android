package com.xuhai.wngs.ui.more;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterYanzhengActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    public static final String TAG = "RegisterYanzhengActivity";
    private EditText et_username, et_yanzhengma;
    private ImageView confirmation, getyanzheng;
    private BroadcastReceiver smsReceiver;
    private IntentFilter filter2;
    private String message;
    int count = 60;
    private TimerTask timerTask;
    private Timer timer;
    private TextView time;
    private String tag;
    private ProgressDialogFragment newFragment;
    private TextView tv_yhxx;
    private CheckBox check_yhxx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_yanzheng);

        tag = getIntent().getStringExtra("tag");

        initView();
        //自动填写验证马
        writeYanzheng();
    }

    public void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_yanzhengma = (EditText) findViewById(R.id.et_yanzhengma);
        confirmation = (ImageView) findViewById(R.id.confirmation);
        getyanzheng = (ImageView) findViewById(R.id.getyanzheng);
        time = (TextView) findViewById(R.id.time);
        tv_yhxx = (TextView) findViewById(R.id.tv_yhxx);
        check_yhxx = (CheckBox) findViewById(R.id.check_yhxx);
        getyanzheng.setOnClickListener(this);
        confirmation.setOnClickListener(this);
        tv_yhxx.setOnClickListener(this);
        check_yhxx.setOnClickListener(this);
        newFragment = new ProgressDialogFragment();
    }

    protected void writeYanzheng() {
        filter2 = new IntentFilter();
        filter2.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter2.setPriority(Integer.MAX_VALUE);
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object[] objs = (Object[]) intent.getExtras().get("pdus");
                for (Object obj : objs) {
                    byte[] pdu = (byte[]) obj;
                    SmsMessage sms = SmsMessage.createFromPdu(pdu);
                    // 短信的内容
                    message = sms.getMessageBody();
                    // 短息的手机号。。+86开头？
                    String from = sms.getOriginatingAddress();
                    // Time time = new Time();
                    // time.set(sms.getTimestampMillis());
                    // String time2 = time.format3339(true);
                    // Log.d("logo", from + "   " + message + "  " + time2);
                    // strContent = from + "   " + message;
                    // handler.sendEmptyMessage(1);
                    if (tag.equals("0")) {
                        String yanzhengma = message.substring(19, 23);
                        if (isNumeric(yanzhengma)) {
                            et_yanzhengma.setText(yanzhengma);
                        } else {

                        }
                    } else if (tag.equals("1")) {
                        String yanzhengma = message.substring(12, 16);
                        if (isNumeric(yanzhengma)) {
                            et_yanzhengma.setText(yanzhengma);
                        } else {

                        }
                    }


                }

            }
        };
        registerReceiver(smsReceiver, filter2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_yanzheng, menu);
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    time.setVisibility(View.VISIBLE);
                    time.setText(count + "秒重新获取");
                    getyanzheng.setVisibility(View.GONE);
                    break;
                case 1:
                    getyanzheng.setVisibility(View.VISIBLE);
                    time.setVisibility(View.GONE);
                    timer.cancel();
                    count = 60;
                    break;
                case 3:
                    timer = new Timer();
                    timerTask = new MyTimerTask();
                    timer.schedule(timerTask, 0, 1000);
                    break;
            }

            return false;
        }
    });

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (count > 0) mHandler.sendEmptyMessage(0);
            else mHandler.sendEmptyMessage(1);
            count--;
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getyanzheng:
                if (Dianjill.isFastDoubleClick()) {
                    return;
                }
                if (et_username.getText().toString().trim().equals("")) {
                    CustomToast.showToast(RegisterYanzhengActivity.this, "请输入手机号码", 2000);
                } else if (et_username.getText().toString().length() < 11) {
                    CustomToast.showToast(RegisterYanzhengActivity.this, "请输入11位电话号", 2000);
                } else {
                    newFragment.show(getFragmentManager(), "1");
                    httpGet(HTTP_GET_YANZHENG);
                }
                break;
            case R.id.confirmation:
                if (Dianjill.isFastDoubleClick()) {
                    return;
                }
                if (et_username.getText().toString().trim().equals("")) {
                    CustomToast.showToast(RegisterYanzhengActivity.this, "请输入手机号码", 2000);
                } else if (et_yanzhengma.getText().toString().trim().equals("")) {
                    CustomToast.showToast(RegisterYanzhengActivity.this, "请输入验证码", 2000);
                } else if (!check_yhxx.isChecked()){
                    CustomToast.showToast(RegisterYanzhengActivity.this, "请先同意用户协议", 2000);
                }else {
                    newFragment.show(getFragmentManager(), "1");
                    httpYanzheng(HTTP_YANZHENG);
                }
                break;
            case R.id.tv_yhxx:
                Intent intent = new Intent(RegisterYanzhengActivity.this, WebActivity.class);
                intent.putExtra("url",HTTP_REGISTER_AGREEMENT);
                intent.putExtra("title","用户协议");
                startActivity(intent);
                break;
        }
    }

    public void httpGet(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", et_username.getText().toString().trim());
        params.put("tag", tag);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg ;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {


                            CustomToast.showToast(RegisterYanzhengActivity.this, "已为您发送验证码", 1000);
                            mHandler.sendEmptyMessage(3);
                            newFragment.dismiss();
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(RegisterYanzhengActivity.this, msg, 1000);
                        }
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(RegisterYanzhengActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(RegisterYanzhengActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    public void httpYanzheng(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", et_username.getText().toString().trim());
        params.put("smscode", et_yanzhengma.getText().toString().trim());
        params.put("tag", tag);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            CustomToast.showToast(
                                    RegisterYanzhengActivity.this,
                                    "验证成功", 1000);
                            if (tag.equals("0")) {
                                newFragment.dismiss();
                                Intent intent = new Intent();
                                intent.setClass(RegisterYanzhengActivity.this, RegisterActivity.class);
                                intent.putExtra("phone", et_username.getText().toString().trim());
                                startActivity(intent);
                                RegisterYanzhengActivity.this.finish();
                            } else {
                                newFragment.dismiss();
                                Intent intent = new Intent();
                                intent.setClass(RegisterYanzhengActivity.this, PWNewActivity.class);
                                intent.putExtra("phone", et_username.getText().toString().trim());
                                startActivity(intent);
                                RegisterYanzhengActivity.this.finish();
                            }
                        } else {
                            CustomToast.showToast(
                                    RegisterYanzhengActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(RegisterYanzhengActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(RegisterYanzhengActivity.this, R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(RegisterYanzhengActivity.this, R.string.http_fail, 1000);
                newFragment.dismiss();
            }
        });
        queue.add(request);
    }
}
