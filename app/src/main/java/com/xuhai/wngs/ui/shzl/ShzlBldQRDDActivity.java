package com.xuhai.wngs.ui.shzl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.pingplusplus.android.PaymentActivity;

import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;

import com.xuhai.wngs.beans.more.MoreSHDZListBean;
import com.xuhai.wngs.ui.more.MoreCardListActivity;
import com.xuhai.wngs.ui.more.MoreSHDZActivity;
import com.xuhai.wngs.ui.more.MoreTJSHDZActivity;

import com.xuhai.wngs.ui.more.MoreWdddItemActivity;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;

import cn.cccb.lib.cccbpay.DialogWidget;
import cn.cccb.lib.cccbpay.PayPwdView;
import me.drakeet.materialdialog.MaterialDialog;

public class ShzlBldQRDDActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    private static final String URL = HTTP_SERVER + "bankpay_order.php";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CHANNEL_UPMP = "upmp";
    private static final String CHANNEL_WECHAT = "wx";
    private static final String CHANNEL_CASH = "cash";
    private static final String CHANNEL_KA = "ka";
    private static final String CHANNEL_YUE = "yue";
    private static final String CHANNEL_ALIPAY = "alipay";
    private String cardno = "";
    private static String CHANNEL_WAY = CHANNEL_ALIPAY;
    private static String storeid;
    private TextView tv_name, tv_addr, tv_phone, tv_note, tv_time, tv_price,tv_yue;
    private ImageView iv_sure, hdfw_iv, zfb_iv,yue_iv,yhk_iv;
    private RelativeLayout rl_true, rl_false;
    private List<MoreSHDZListBean> shdzBeanList;
    private String name, address, phone, note;
    private RelativeLayout rl_note, hdfw_rl, zfb_rl,yue_rl,yhk_rl;
    private ProgressDialogFragment newFragment;
    private String goodsid, count;
    private JSONArray jsonArray;
    private String allprice;
    private JSONObject order;
    private String orderid;
    private DialogWidget mDialogWidght;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bld_qrdd);
        dbdate();
        initView();
        newFragment = new ProgressDialogFragment();
        httpRequest(HTTP_SHDZ_LIST + "?uid=" + UID,true);
        httpGetBan(HTTP_GET_BALANCE + "?uid=" + UID);
    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    if (shdzBeanList.size() == 0) {
                        rl_true.setVisibility(View.GONE);
                        rl_false.setVisibility(View.VISIBLE);
                    } else {
                        rl_true.setVisibility(View.VISIBLE);
                        rl_false.setVisibility(View.GONE);


                        tv_name.setText(shdzBeanList.get(0).getName());
                        tv_addr.setText(shdzBeanList.get(0).getAddress());
                        tv_phone.setText(shdzBeanList.get(0).getTel());
                    }
                    break;
                case LOAD_FAIL:
                    rl_true.setVisibility(View.GONE);
                    rl_false.setVisibility(View.VISIBLE);
                    break;
                case LOAD_MORE:

                    tv_yue.setText("¥ " + AESEncryptor.decrypt(spn.getString(SPN_BALANCE,"")));

                    break;


            }
            return false;
        }
    });

    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_addr = (TextView) findViewById(R.id.tv_addr);
        tv_phone = (TextView) findViewById(R.id.tv_tel);
        tv_note = (TextView) findViewById(R.id.tv_note);
        tv_yue = (TextView) findViewById(R.id.tv_yue);
        rl_note = (RelativeLayout) findViewById(R.id.rl_note);
        hdfw_iv = (ImageView) findViewById(R.id.hdfk_ok_iv);
        zfb_iv = (ImageView) findViewById(R.id.zfb_ok_iv);
//        upmp_iv = (ImageView) findViewById(R.id.upmp_ok_iv);
//        wx_iv = (ImageView) findViewById(R.id.wechat_ok_iv);
        yue_iv = (ImageView) findViewById(R.id.yue_ok_iv);
        yhk_iv = (ImageView) findViewById(R.id.yhk_ok_iv);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_price = (TextView) findViewById(R.id.tv_allprice);
        allprice = getIntent().getStringExtra("allprice");
        tv_price.setText("应付总额：" + getResources().getString(R.string.yang) + " " + allprice);
        tv_time.setText("备注：送货时间" + BLD_STARTTIME + "~" + BLD_ENDTIME + "，产品以实物为准，图片仅供参考");
        rl_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShzlBldQRDDActivity.this, ShzlBldQRDDBzActivity.class);

                startActivityForResult(intent, 21);
            }
        });
        rl_true = (RelativeLayout) findViewById(R.id.rl_true);
        rl_true.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShzlBldQRDDActivity.this, MoreSHDZActivity.class);
                intent.putExtra("bldon", "bld");
                startActivityForResult(intent, 11);
            }
        });

        rl_false = (RelativeLayout) findViewById(R.id.rl_false);
        rl_false.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShzlBldQRDDActivity.this, MoreTJSHDZActivity.class);

                startActivityForResult(intent, 11);
            }
        });
        hdfw_rl = (RelativeLayout) findViewById(R.id.hdfk_rl);
        hdfw_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CHANNEL_WAY = CHANNEL_CASH;
                hdfw_iv.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
                zfb_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
//                upmp_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
//                wx_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                yue_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                yhk_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
            }
        });
        zfb_rl = (RelativeLayout) findViewById(R.id.zfb_rl);
        zfb_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CHANNEL_WAY = CHANNEL_ALIPAY;
                hdfw_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                zfb_iv.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
//                upmp_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
//                wx_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                yue_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                yhk_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
            }
        });
        yue_rl = (RelativeLayout) findViewById(R.id.yue_rl);
        yue_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CHANNEL_WAY = CHANNEL_YUE;
                hdfw_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                zfb_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
//                upmp_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
//                wx_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                yue_iv.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
                yhk_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
            }
        });
        yhk_rl = (RelativeLayout) findViewById(R.id.yhk_rl);
        yhk_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CHANNEL_WAY = CHANNEL_KA;
                hdfw_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                zfb_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
//                upmp_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
//                wx_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                yue_iv.setImageResource(R.drawable.ic_shdz_qrdd_choose);
                yhk_iv.setImageResource(R.drawable.ic_shdz_qrddchoose_on);
            }
        });

        iv_sure = (ImageView) findViewById(R.id.qrdd_sure);
        iv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shdzBeanList.size() == 0) {
                    CustomToast.showToast(ShzlBldQRDDActivity.this, "添加您的收货地址！", 1000);
                    Intent intent = new Intent(ShzlBldQRDDActivity.this, MoreTJSHDZActivity.class);
                    startActivityForResult(intent, 11);
                } else {

                    if (CHANNEL_WAY.equals(CHANNEL_KA)){
                        Intent intent = new Intent(ShzlBldQRDDActivity.this,MoreCardListActivity.class);
                        intent.putExtra("action","choose");
                        startActivityForResult(intent, STATE_LOGOUT);
                    }else if (CHANNEL_WAY.equals(CHANNEL_YUE)){

                        mDialogWidght = new DialogWidget(ShzlBldQRDDActivity.this,
                                PayPwdView.getInstance("消费金额 " + allprice, BANKUID, ShzlBldQRDDActivity.this, new PayPwdView.OnPayListener() {
                                    @Override
                                    public void onCancelPay() {

                                        mDialogWidght.dismiss();
                                        mDialogWidght = null;
                                    }

                                    @Override
                                    public void onSurePay(String s) {
                                        postJson(URL, allprice, s);

                                        mDialogWidght.dismiss();
                                        mDialogWidght = null;
                                    }
                                }).getView());
                        mDialogWidght.show();

                    }else {
                        postJson(URL, allprice, "");
                    }

                }

            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")) {
                        payDone(HTTP_PAY_DONE, orderid);
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

                database.delete("shopcart", null, null);
                database.close();

                if (CHANNEL_WAY == CHANNEL_ALIPAY) {
                    Intent intent = new Intent(ShzlBldQRDDActivity.this, MoreWdddItemActivity.class);
                    intent.putExtra("orderid", orderid);
                    intent.putExtra("isFromitem", true);
                    startActivityForResult(intent, 19);
                }
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceld
                 * "invalid" - payment plugin not installed
                 */


//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                CustomToast.showToast(this, "用户取消操作", 1000);
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                CustomToast.showToast(this, "请安装支付宝插件.", 1000);
            }
        } else if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                httpRequest(HTTP_SHDZ_LIST + "?uid=" + UID,false);

                name = data.getStringExtra("c_sh_name");
                phone = data.getStringExtra("c_sh_tel");
                address = data.getStringExtra("c_sh_addr");

                rl_true.setVisibility(View.VISIBLE);
                rl_false.setVisibility(View.GONE);

                tv_name.setText(name);
                tv_addr.setText(address);
                tv_phone.setText(phone);
            }else if (resultCode == RESULT_CANCELED){
                httpRequest(HTTP_SHDZ_LIST + "?uid=" + UID,false);
            }

        } else if (requestCode == 21) {
            if (resultCode == RESULT_OK) {
                note = data.getStringExtra("message");
                tv_note.setText(note);
            }
        } else if (requestCode == 19 && resultCode == 19){
            setResult(19);
            finish();
        }else if (requestCode == STATE_LOGOUT & resultCode == RESULT_OK){
            cardno = data.getStringExtra("cardno");
            mDialogWidght = new DialogWidget(ShzlBldQRDDActivity.this,
                    PayPwdView.getInstance("消费金额 " + allprice, BANKUID, ShzlBldQRDDActivity.this, new PayPwdView.OnPayListener() {
                        @Override
                        public void onCancelPay() {

                            mDialogWidght.dismiss();
                            mDialogWidght = null;
                        }

                        @Override
                        public void onSurePay(String s) {
                            postJson(URL, allprice, s);

                            mDialogWidght.dismiss();
                            mDialogWidght = null;
                        }
                    }).getView());
            mDialogWidght.show();
        }
    }

    private void postJson(String url, String amount,String pwd) {
//
        Map<String, String> params = new HashMap<String, String>();
        params.put("cardno", cardno);
        params.put("tranpass", pwd);
        params.put("sqid", SQID);
        params.put("channel", CHANNEL_WAY);
        params.put("amount", String.valueOf((int) (Double.parseDouble(amount) * 100)));
        params.put("uid", UID);
        params.put("storeid",storeid);
        params.put("phone", tv_phone.getText().toString().trim());
        params.put("address", tv_addr.getText().toString().trim());
        params.put("name", tv_name.getText().toString().trim());
        params.put("note", tv_note.getText().toString().trim());
        params.put("orders", String.valueOf(jsonArray));

        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("response=====",response+"");
                if (response.has("metadata")){
                    try {
                        JSONObject jsonObject = response.getJSONObject("metadata");
                        if (jsonObject.has("orderid")){
                            orderid = jsonObject.getString("orderid");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (CHANNEL_WAY == CHANNEL_ALIPAY) {
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
                            setResult(19);
                            finish();

                            database.delete("shopcart", null, null);
                            database.close();

                            CustomToast.showToast(ShzlBldQRDDActivity.this,"支付完成",2000);
                        }else {
                            CustomToast.showToast(ShzlBldQRDDActivity.this,response.getString("msg"),2000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("err===",error+"");
                CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 2000);

            }
        });
        queue.add(request);
    }

    private void payDone(String url,String orderno) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderid", orderno);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
//                            CustomToast.showToast(ShzlBldQRDDActivity.this, "ok", 1000);
//                            Log.d("paydone","okokok");

                        } else {
                            CustomToast.showToast(ShzlBldQRDDActivity.this, msg, 1000);
                          //  Log.d("paydone","nonono");
                        }
                    } else {
                        CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);

                    }
                } catch (Exception e) {
                    CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);


            }
        });
        queue.add(request);
    }

    private void dbdate() {
        jsonArray = new JSONArray();
        Cursor cursor = database.rawQuery("select * from shopcart", null);
        while (cursor.moveToNext()) {
            goodsid = cursor.getString(cursor.getColumnIndex("goodsid"));
            count = cursor.getString(cursor.getColumnIndex("count"));
            storeid = cursor.getString(cursor.getColumnIndex("storeid"));
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("goodsid", goodsid);
                jsonObject.put("count", count);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    private void httpGetBan(String url) {

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
                            handler.sendEmptyMessage(LOAD_MORE);
                        } else {
                            CustomToast.showToast(ShzlBldQRDDActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

//    private void httpPost(String url) {
//        newFragment.show(getFragmentManager(), "1");
//        iv_sure.setOnClickListener(null);
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("sqid", SQID);
//        params.put("uid", UID);
//        params.put("storeid", STOREID);
//        params.put("phone", tv_phone.getText().toString().trim());
//        params.put("address", tv_addr.getText().toString().trim());
//        params.put("name", tv_name.getText().toString().trim());
//        params.put("note", tv_note.getText().toString().trim());
//        params.put("orders", String.valueOf(jsonArray));
//        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(1, url, params, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                String recode, msg;
//                try {
//                    if (response.has("recode")) {
//                        recode = response.getString("recode");
//                        msg = response.getString("msg");
//
//                        if (recode.equals("0")) {
//                            CustomToast.showToast(ShzlBldQRDDActivity.this, "下单成功", 1000);
//                            database.delete("shopcart", null, null);
//                            database.close();
//                            setResult(19);
//                            finish();
//                        } else {
//                            Toast.makeText(ShzlBldQRDDActivity.this, msg, Toast.LENGTH_SHORT).show();
//                            iv_sure.setOnClickListener(ShzlBldQRDDActivity.this);
//                            newFragment.dismiss();
//                        }
//                    } else {
//                        CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
//                        newFragment.dismiss();
//                        iv_sure.setOnClickListener(ShzlBldQRDDActivity.this);
//                    }
//                } catch (Exception e) {
//                    CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
//                    newFragment.dismiss();
//                    iv_sure.setOnClickListener(ShzlBldQRDDActivity.this);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
//                newFragment.dismiss();
//                iv_sure.setOnClickListener(ShzlBldQRDDActivity.this);
//            }
//        });
//        queue.add(request);
//    }


    //收货地址

    private void httpRequest(String url, final Boolean isFirstin) {
        newFragment.show(getFragmentManager(), "1");
        shdzBeanList = new ArrayList<MoreSHDZListBean>();

        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                for (int i = 0; i < list.length(); i++) {
                                    JSONObject json = list.getJSONObject(i);
                                    MoreSHDZListBean bean = new MoreSHDZListBean();
                                    if (json.has("addid")) {
                                        bean.setAddid(json.getString("addid"));
                                    }
                                    if (json.has("name")) {
                                        bean.setName(json.getString("name"));

                                    }
                                    if (json.has("tel")) {
                                        bean.setTel(json.getString("tel"));
                                    }
                                    if (json.has("address")) {
                                        bean.setAddress(json.getString("address"));

                                    }
                                    if (json.has("default")) {
                                        bean.setIsdefault(json.getString("default"));
                                    }
                                    shdzBeanList.add(bean);
                                }
                            }
                            if (isFirstin) {
                                handler.sendEmptyMessage(LOAD_SUCCESS);
                            }
                            newFragment.dismiss();
                        } else {

                            handler.sendEmptyMessage(LOAD_FAIL);

                            //   CustomToast.showToast(ShzlBldQRDDActivity.this, "添加您的收货地址！", 1000);
                            newFragment.dismiss();
                        }
                    } else {
                        CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
                    newFragment.dismiss();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(ShzlBldQRDDActivity.this, R.string.http_fail, 1000);
                newFragment.dismiss();
            }
        });
        queue.add(request);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shzl_bld_qrdd, menu);
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

    @Override
    public void onClick(View v) {

    }
}
