package com.xuhai.wngs.ui.more;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.cocosw.bottomsheet.BottomSheet;
import com.pingplusplus.android.PaymentActivity;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoreYueActivity extends BaseActionBarAsUpActivity {
    private Button btn_cz,btn_tx;
    private static final int REQUEST_CODE_PAYMENT = 14;
    private static final String CHANNEL_ALIPAY = "2";
    private static final String CHANNEL_UPMP = "4";
    private static final String CHANNEL_WECHAT = "3";
    private static final String CHANNEL_CARD = "1";

    private String CHANNEL_WAY = "";
    private TextView tv_yue;
    private boolean issetok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_yue);
        initView();
        httpRequest(HTTP_GET_BALANCE + "?uid=" + UID);
    }

    private void initView(){
        btn_cz = (Button) findViewById(R.id.btn_cz);
        tv_yue = (TextView) findViewById(R.id.tv_yue);
        tv_yue.setText(BALANCE);
        btn_cz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MoreYueActivity.this, MoreCardCzActivity.class);
                startActivityForResult(intent, LOAD_REFRESH);

//                new BottomSheet.Builder(MoreYueActivity.this).sheet(R.menu.menu_yue_sheet).listener(new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case R.id.yhk:
//
//                                break;
//                            case R.id.alipay:
//                                CHANNEL_WAY = CHANNEL_ALIPAY;
//                                postJson(HTTP_YUE_CHONG, "0.01");
//                                break;
//                            case R.id.wx:
//                                CHANNEL_WAY = CHANNEL_WECHAT;
//                                postJson(HTTP_YUE_CHONG, "0.01");
//                                break;
//                            case R.id.yl:
//                                CHANNEL_WAY = CHANNEL_UPMP;
//                                postJson(HTTP_YUE_CHONG, "0.01");
//                                break;
//
//                        }
//                    }
//                }).show();


            }
        });
        btn_tx = (Button) findViewById(R.id.btn_tx);
        btn_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreYueActivity.this, MoreCardTxActivity.class);
                startActivityForResult(intent, LOAD_REFRESH);
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    try {
                        tv_yue.setText(AESEncryptor.decrypt(spn.getString(SPN_BALANCE,"")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
            return false;
        }
    });

    private void httpRequest(String url) {

        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            editor.putString(SPN_BALANCE, AESEncryptor.encrypt(response.getString("balance")));
                            editor.commit();
                            handler.sendEmptyMessage(LOAD_SUCCESS);
                        } else {
                            CustomToast.showToast(MoreYueActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(MoreYueActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(MoreYueActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(MoreYueActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    private void postJson(String url, String amount) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("bachannel", CHANNEL_WAY);
        params.put("bacount", String.valueOf((int) (Double.parseDouble(amount) * 100)));
        params.put("uid", UID);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.has("metadata")){
                    try {
                        JSONObject jsonObject = response.getJSONObject("metadata");
                        if (jsonObject.has("orderid")){
//                            orderid = jsonObject.getString("orderid");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent();
                String packageName = getPackageName();
                ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                intent.setComponent(componentName);

                intent.putExtra(PaymentActivity.EXTRA_CHARGE, response.toString());
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(MoreYueActivity.this, R.string.http_fail, 1000);

            }
        });
        queue.add(request);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")) {
                    issetok = true;
                    CustomToast.showToast(this, "支付成功", 1000);
                } else if (result.equals("fail")) {
                    CustomToast.showToast(this, "支付失败", 1000);
                } else if (result.equals("cancel")) {

                    CustomToast.showToast(this, "用户取消操作", 1000);
                } else if (result.equals("invalid")) {
                    CustomToast.showToast(this, "请安装插件", 1000);
                }

                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */

            }
        }else if (requestCode == LOAD_REFRESH){
            if (resultCode == RESULT_OK){
                issetok = true;
                httpRequest(HTTP_GET_BALANCE + "?uid=" + UID);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_yue, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.zdmx) {
            Intent intent = new Intent(MoreYueActivity.this,MoreZdmxActivity.class);
            startActivity(intent);
            return true;
        }else if (id == android.R.id.home){
            if (issetok) {
                setResult(LOGIN_SUCCESS);
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
