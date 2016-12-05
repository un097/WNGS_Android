package com.xuhai.wngs.ui.more;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.pingplusplus.android.PaymentActivity;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.views.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.cccb.lib.cccbpay.DialogWidget;
import cn.cccb.lib.cccbpay.PayPwdView;
import me.drakeet.materialdialog.MaterialDialog;

public class MorePayAgainActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    private static final String URL = HTTP_SERVER + "bankpay_order.php";
    private static final int REQUEST_CODE_PAYMENT = 1;

    private EditText editpwd;
    private MaterialDialog mMaterialDialog;
    private RelativeLayout yue_rl,hdfk_rl,zfb_rl,yhk_rl;
    private ImageView iv_yue_ok,iv_hdfk_ok,iv_zfb_ok,iv_yhk_ok;
    private TextView tv_yue,tv_price;
    private ImageView btn_sure;
    private String order_id,cardno = "",price;

    private static final String CHANNEL_CASH = "cash";
    private static final String CHANNEL_KA = "ka";
    private static final String CHANNEL_YUE = "yue";
    private static final String CHANNEL_ALIPAY = "alipay";

    private static String CHANNEL_WAY = CHANNEL_YUE;

    private DialogWidget mDialogWidght;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_pay_again);
        order_id = getIntent().getStringExtra("orderid");
        price = getIntent().getStringExtra("price");
        initView();
        initDialog();

        httpRequest(HTTP_GET_BALANCE + "?uid=" + UID);
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
                            CustomToast.showToast(MorePayAgainActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(MorePayAgainActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(MorePayAgainActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(MorePayAgainActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    private void initView(){
        yue_rl = (RelativeLayout) findViewById(R.id.yue_rl);
        hdfk_rl = (RelativeLayout) findViewById(R.id.hdfk_rl);
        zfb_rl = (RelativeLayout) findViewById(R.id.zfb_rl);
        yhk_rl = (RelativeLayout) findViewById(R.id.yhk_rl);
        iv_yue_ok = (ImageView) findViewById(R.id.iv_yue_ok);
        iv_hdfk_ok = (ImageView) findViewById(R.id.iv_hdfk_ok);
        iv_zfb_ok = (ImageView) findViewById(R.id.iv_zfb_ok);
        iv_yhk_ok = (ImageView) findViewById(R.id.iv_yhk_ok);
        tv_yue = (TextView) findViewById(R.id.tv_yue);
        tv_price = (TextView) findViewById(R.id.tv_price);
        btn_sure = (ImageView) findViewById(R.id.btn_sure);

        tv_price.setText(price);
        yue_rl.setOnClickListener(this);
        hdfk_rl.setOnClickListener(this);
        zfb_rl.setOnClickListener(this);
        yhk_rl.setOnClickListener(this);
        btn_sure.setOnClickListener(this);

    }

    private void postJson(String url,String pwd) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("orderid", order_id);
        params.put("channel", CHANNEL_WAY);
        params.put("cardno", cardno);
        params.put("tranpass", pwd);

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

                if (CHANNEL_WAY.equals(CHANNEL_ALIPAY)) {
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                    intent.setComponent(componentName);
                    intent.putExtra(PaymentActivity.EXTRA_CHARGE, response.toString());
                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                }

                if (response.has("recode")){
                    try {
                        if (response.getString("recode").equals("0")){
                            setResult(RESULT_OK);
                            finish();
                            CustomToast.showToast(MorePayAgainActivity.this, "支付完成", 1000);
                        }else {
                            CustomToast.showToast(MorePayAgainActivity.this,response.getString("msg"),1000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(MorePayAgainActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_pay_again, menu);
        return true;
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDialog(){
        editpwd = new EditText(MorePayAgainActivity.this);
        editpwd.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mMaterialDialog = new MaterialDialog(MorePayAgainActivity.this);
        mMaterialDialog.setTitle("交易密码");
        mMaterialDialog.setContentView(editpwd);
        mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postJson(URL, editpwd.getText().toString().trim());
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.setNegativeButton("CANCEL", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")) {
//                    payDone(HTTP_PAY_DONE, orderid);
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
        }


        if (requestCode == STATE_LOGOUT & resultCode == RESULT_OK){
            cardno = data.getStringExtra("cardno");
            mDialogWidght = new DialogWidget(MorePayAgainActivity.this,
                    PayPwdView.getInstance("消费金额 " + price, BANKUID, MorePayAgainActivity.this, new PayPwdView.OnPayListener() {
                        @Override
                        public void onCancelPay() {

                            mDialogWidght.dismiss();
                            mDialogWidght = null;
                        }

                        @Override
                        public void onSurePay(String s) {
                            postJson(URL,s);

                            mDialogWidght.dismiss();
                            mDialogWidght = null;
                        }
                    }).getView());
            mDialogWidght.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.yue_rl:
                CHANNEL_WAY = CHANNEL_YUE;
                iv_yue_ok.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
                iv_hdfk_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_zfb_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_yhk_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                break;
            case R.id.hdfk_rl:
                CHANNEL_WAY = CHANNEL_CASH;
                iv_yue_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_hdfk_ok.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
                iv_zfb_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_yhk_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                break;
            case R.id.zfb_rl:
                CHANNEL_WAY = CHANNEL_ALIPAY;
                iv_yue_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_hdfk_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_zfb_ok.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
                iv_yhk_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                break;
            case R.id.yhk_rl:
                CHANNEL_WAY = CHANNEL_KA;
                iv_yue_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_hdfk_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_zfb_ok.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                iv_yhk_ok.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
                break;
            case R.id.btn_sure:
                if (CHANNEL_WAY.equals(CHANNEL_YUE)){
                    mDialogWidght = new DialogWidget(MorePayAgainActivity.this,
                            PayPwdView.getInstance("消费金额 " + price, BANKUID, MorePayAgainActivity.this, new PayPwdView.OnPayListener() {
                                @Override
                                public void onCancelPay() {

                                    mDialogWidght.dismiss();
                                    mDialogWidght = null;
                                }

                                @Override
                                public void onSurePay(String s) {
                                    postJson(URL,s);

                                    mDialogWidght.dismiss();
                                    mDialogWidght = null;
                                }
                            }).getView());
                    mDialogWidght.show();

                }else if (CHANNEL_WAY.equals(CHANNEL_KA)){
                    Intent intent = new Intent(MorePayAgainActivity.this,MoreCardListActivity.class);
                    intent.putExtra("action","choose");
                    startActivityForResult(intent, STATE_LOGOUT);
                }else {
                    postJson(URL,"");
                }
                break;
        }
    }
}
