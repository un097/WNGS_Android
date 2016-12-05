package com.xuhai.wngs.ui.more;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pingplusplus.android.PaymentActivity;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.more.MoreWDDDItemListAdapter;
import com.xuhai.wngs.beans.more.MoreWdddItemBean;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ListViewForScrollView;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cccb.lib.cccbpay.DialogWidget;
import cn.cccb.lib.cccbpay.PayPwdView;
import me.drakeet.materialdialog.MaterialDialog;

public class MoreWdddItemActivity extends BaseActionBarAsUpActivity implements View.OnClickListener {
    private String orderid;
    private static final String URL = HTTP_SERVER + "pingpp_order.php";
    private static final String Success_url = HTTP_SERVER + "pay_done.php";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CHANNEL_UPMP = "upmp";
    private static final String CHANNEL_WECHAT = "wx";
    private static final String CHANNEL_ALIPAY = "alipay";
    public static final String TAG = "MoreWdddItemActivity";
    private int p = 1;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private List<MoreWdddItemBean> wdddItemBeanList;
    private ListViewForScrollView listView;
    private MoreWDDDItemListAdapter moreWDDDItemListAdapter;
    private String m_storename, m_flag, m_count, m_price, m_note, m_name, m_phone, m_addr, m_time,m_paymod;
    private ImageView qrsh, qrpj,jxfk,tk,qxdd;
    private RelativeLayout layout_all;
    private ProgressDialogFragment newFragment;
    private boolean buer=false;

    private DialogWidget mDialogWidght;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_wddd_item);
        orderid = getIntent().getStringExtra("orderid");
        initView();
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        httpRequest(HTTP_WDDD_XQ + "?uid=" + UID + "&orderid=" + orderid + "&p=" + String.valueOf(p) + "&count=" + String.valueOf(PAGE_COUNT));

    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setMessage();
                    moreWDDDItemListAdapter = new MoreWDDDItemListAdapter(MoreWdddItemActivity.this, wdddItemBeanList);
                    listView.setAdapter(moreWDDDItemListAdapter);
                    moreWDDDItemListAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });

    private void setMessage() {
        layout_all = (RelativeLayout) findViewById(R.id.layout_all);
        layout_all.setVisibility(View.VISIBLE);
        TextView textView = (TextView) this.findViewById(R.id.title);
        textView.setText(m_storename);
        TextView textView1 = (TextView) this.findViewById(R.id.tv_geshu);
        textView1.setText(m_count);
        TextView textView2 = (TextView) this.findViewById(R.id.tv_zhuangtai);
        qrsh = (ImageView) this.findViewById(R.id.qrsh);
        qrpj = (ImageView) this.findViewById(R.id.qrpj);
        jxfk = (ImageView) this.findViewById(R.id.jxfk);
        qxdd = (ImageView) this.findViewById(R.id.qxdd);
        tk = (ImageView) this.findViewById(R.id.tk);
        if (m_flag.equals("1")) {
            textView2.setText("已接收");
            qxdd.setVisibility(View.VISIBLE);
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
            tk.setVisibility(View.GONE);
            qxdd.setOnClickListener(MoreWdddItemActivity.this);
        } else if (m_flag.equals("2")) {
            textView2.setText("配送中");
            qrsh.setVisibility(View.VISIBLE);
            tk.setVisibility(View.GONE);
            qrsh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mDialogWidght = new DialogWidget(MoreWdddItemActivity.this,
                            PayPwdView.getInstance("确认收货", BANKUID, MoreWdddItemActivity.this, new PayPwdView.OnPayListener() {
                                @Override
                                public void onCancelPay() {

                                    mDialogWidght.dismiss();
                                    mDialogWidght = null;
                                }

                                @Override
                                public void onSurePay(String s) {
                                    httpQrsh(HTTP_WDDD_QRSH, s);
                                    mDialogWidght.dismiss();
                                    mDialogWidght = null;
                                }
                            }).getView());
                    mDialogWidght.show();

                }
            });
        } else if (m_flag.equals("3")) {
            textView2.setText("已完成");
            qrpj.setVisibility(View.VISIBLE);
            qrsh.setVisibility(View.GONE);
            tk.setVisibility(View.GONE);
            qrpj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Dianjill.isFastDoubleClick()) {
                        return;
                    }
                    Intent intent = new Intent();
                    intent.setClass(MoreWdddItemActivity.this, MoreWdddPj.class);
                    intent.putExtra("storename", m_storename);
                    intent.putExtra("orderid", orderid);
                    startActivity(intent);
                    finish();
                }
            });
        } else if (m_flag.equals("5")) {
            textView2.setText("已评价");
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
            tk.setVisibility(View.GONE);
        }else if (m_flag.equals("6")) {
            textView2.setText("未付款");
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
            tk.setVisibility(View.GONE);
            qxdd.setVisibility(View.VISIBLE);
            qxdd.setOnClickListener(MoreWdddItemActivity.this);
            jxfk.setVisibility(View.VISIBLE);
            jxfk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Dianjill.isFastDoubleClick()) {
                        return;
                    }
                    Intent intent = new Intent(MoreWdddItemActivity.this,MorePayAgainActivity.class);
                    intent.putExtra("orderid",orderid);
                    intent.putExtra("price",m_price);
                    startActivity(intent);
//                    if (m_paymod.equals("alipay")) {
//                        postJson(URL);
//                    }
                }
            });
        }else if (m_flag.equals("7")) {
            textView2.setText("支付确认中");
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
            tk.setVisibility(View.GONE);
        } else if (m_flag.equals("8")) {
            textView2.setText("已支付");
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
            tk.setVisibility(View.VISIBLE);
            tk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Dianjill.isFastDoubleClick()) {
                        return;
                    }
                    httpTk(HTTP_PAY_TK);
                }
            });
        } else if (m_flag.equals("9")) {
            textView2.setText("退款中");
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
        } else if (m_flag.equals("10")) {
            textView2.setText("已退款");
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
        }else if (m_flag.equals("11")) {
            textView2.setText("已取消");
            qrpj.setVisibility(View.GONE);
            qrsh.setVisibility(View.GONE);
        }

        TextView textView3 = (TextView) this.findViewById(R.id.tv_jiage);
        textView3.setText(getResources().getString(R.string.yang) + " " + m_price);
        TextView textView4 = (TextView) this.findViewById(R.id.beizhu);
        textView4.setText(m_note);
        TextView tv_shr = (TextView) findViewById(R.id.tv_shr);
        tv_shr.setText(m_name);
        TextView tv_shdz = (TextView) findViewById(R.id.tv_shdz);
        tv_shdz.setText(m_addr);
        TextView tv_tel = (TextView) findViewById(R.id.tv_tel);
        tv_tel.setText(m_phone);
        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setText(m_time);
    }

    private void httpTk(String url) {
        newFragment.show(getFragmentManager(), "1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderid", orderid);
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
                                    MoreWdddItemActivity.this,
                                    "退款成功", 1000);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            CustomToast.showToast(getBaseContext(), msg, 1000);
                            newFragment.dismiss();
                        }
                    }else {
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
            }
        });
        queue.add(request);
    }

    private void postJson(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderid", orderid);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

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
                CustomToast.showToast(MoreWdddItemActivity.this, R.string.http_fail, 1000);

            }
        });
        queue.add(request);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //支付页面返回处理
        if (requestCode == REQUEST_CODE_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")) {
                    buer=true;
                    CustomToast.showToast(this, "支付成功", 1000);
                    postSuccess(Success_url);
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


//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                CustomToast.showToast(this, "用户取消操作", 1000);
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                CustomToast.showToast(this, "请安装支付宝插件.", 1000);
            }
        }
    }

    private void postSuccess(String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderid", orderid);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CustomToast.showToast(MoreWdddItemActivity.this, R.string.http_fail, 1000);

            }
        });
        queue.add(request);
    }

    private void httpQrsh(String url,String pwd) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", UID);
        params.put("trad_passwd", pwd);
        params.put("orderid", orderid);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(MoreWdddItemActivity.this);
//                            builder.setTitle("提示");
//                            builder.setPositiveButton("确定", null);
//                            builder.setIcon(android.R.drawable.ic_dialog_info);
//                            builder.setMessage("确认收货成功!");
//                            builder.show();
                            qrpj.setVisibility(View.VISIBLE);
                            qrsh.setVisibility(View.GONE);
                            CustomToast.showToast(
                                    MoreWdddItemActivity.this,
                                    "确认收货成功", 1000);
                            buer=true;
                        } else {
                            qrpj.setVisibility(View.GONE);
                            qrsh.setVisibility(View.VISIBLE);
                            CustomToast.showToast(getBaseContext(), msg, 1000);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(request);
    }

    private void initView() {
        listView = (ListViewForScrollView) findViewById(R.id.listView);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

    }

    private void httpRequest(String url) {
        wdddItemBeanList = new ArrayList<MoreWdddItemBean>();

        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            if (response.has("storename")) {
                                m_storename = response.getString("storename");
                            }
                            if (response.has("flag")) {
                                m_flag = response.getString("flag");
                            }
                            if (response.has("count")) {
                                m_count = response.getString("count");
                            }
                            if (response.has("price")) {
                                m_price = response.getString("price");
                            }
                            if (response.has("note")) {
                                m_note = response.getString("note");
                            }
                            if (response.has("name")) {
                                m_name = response.getString("name");

                            }
                            if (response.has("phone")) {
                                m_phone = response.getString("phone");
                            }
                            if (response.has("addr")) {
                                m_addr = response.getString("addr");
                            }
                            if (response.has("time")) {
                                m_time = response.getString("time");
                            }
                            if (response.has("paymod")){
                                m_paymod = response.getString("paymod");
                            }
                            if (response.has("list")) {
                                JSONArray list = response.getJSONArray("list");
                                Gson gson = new Gson();
                                wdddItemBeanList = gson.fromJson(list.toString(), new TypeToken<List<MoreWdddItemBean>>() {
                                }.getType());

                            }
                            newFragment.dismiss();
                            handler.sendEmptyMessage(0);

                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreWdddItemActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreWdddItemActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreWdddItemActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreWdddItemActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_wddd_item, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (buer){
                if (getIntent().getBooleanExtra("isFromitem",false)){
                    setResult(19);
                }else {
                    setResult(RESULT_OK);
                }
                finish();
            }else {
                if (getIntent().getBooleanExtra("isFromitem",false)){
                    setResult(19);
                    finish();
                }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if (buer){
                if (getIntent().getBooleanExtra("isFromitem",false)){
                    setResult(19);
                }else {
                    setResult(RESULT_OK);
                }
                finish();
            }else {
                if (getIntent().getBooleanExtra("isFromitem",false)){
                    setResult(19);
                    finish();
                }else {
                    finish();
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qxdd:
                if (Dianjill.isFastDoubleClick()) {
                    return;
                }
                httpQxdd(HTTP_QXDD);
                break;
        }
    }

    private void httpQxdd(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");

        Map<String, String> params = new HashMap<String, String>();
        params.put("orderid", orderid);
        params.put("uid", UID);
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
                                    MoreWdddItemActivity.this,
                                    "取消订单成功", 1000);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            CustomToast.showToast(getBaseContext(), msg, 1000);
                            newFragment.dismiss();
                        }
                    }else {
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                           newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
            }
        });
        queue.add(request);
    }
}
