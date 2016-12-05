package com.xuhai.wngs.ui.more;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreBankListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
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

public class MoreCardTxActivity extends BaseActionBarAsUpActivity {

    private TextView tv_card,tv_name;
    private ImageView iv_bank;
    private EditText et_money;
    private Button btn_next;
    private TextView layout_off;
    private RelativeLayout layout_on;
    private List<MoreBankListBean> bankListBeanList;
    private LinearLayout layout_all;
    private ProgressDialogFragment dialog;
    private DialogWidget mDialogWidght;
    String cardnum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_card_tx);

        initView();
        httpGetcard(HTTP_BANK_LIST + "?uid=" + UID);
    }

    private void initView(){
        dialog = new ProgressDialogFragment();
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_card = (TextView) findViewById(R.id.tv_card);
        iv_bank = (ImageView) findViewById(R.id.iv_bank);
        et_money = (EditText) findViewById(R.id.et_money);
        btn_next = (Button) findViewById(R.id.btn_next);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialogWidght = new DialogWidget(MoreCardTxActivity.this,
                        PayPwdView.getInstance("提现 " + et_money.getText().toString().trim(),BANKUID, MoreCardTxActivity.this, new PayPwdView.OnPayListener() {
                            @Override
                            public void onCancelPay() {

                                mDialogWidght.dismiss();
                                mDialogWidght = null;
                            }

                            @Override
                            public void onSurePay(String s) {
                                postTixian(HTTP_YUE_TIXIAN, et_money.getText().toString().trim(), s);

                                mDialogWidght.dismiss();
                                mDialogWidght = null;
                            }
                        }).getView());
                mDialogWidght.show();
            }
        });

        layout_on = (RelativeLayout) findViewById(R.id.layout_on);
        layout_off = (TextView) findViewById(R.id.layout_off);
        layout_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreCardTxActivity.this, MoreCardListActivity.class);
                intent.putExtra("action", "choose");
                startActivityForResult(intent, STATE_LOGOUT);
            }
        });
        layout_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreCardTxActivity.this, MoreCardListActivity.class);
                intent.putExtra("action", "choose");
                startActivityForResult(intent, STATE_LOGOUT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STATE_LOGOUT & resultCode == RESULT_OK){
            layout_off.setVisibility(View.GONE);
            layout_on.setVisibility(View.VISIBLE);

            tv_name.setText(data.getStringExtra("bankname"));
            cardnum = data.getStringExtra("cardno");
            String showno = data.getStringExtra("cardno").substring(data.getStringExtra("cardno").length() - 4);
            tv_card.setText("***************" + showno);
            PicassoTrustAll.getInstance(MoreCardTxActivity.this).load(data.getStringExtra("bankphoto")).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(iv_bank);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_REFRESH:
                    if (bankListBeanList.size() > 0){
                        layout_off.setVisibility(View.GONE);
                        layout_on.setVisibility(View.VISIBLE);
                        layout_all.setVisibility(View.VISIBLE);

                        tv_name.setText(bankListBeanList.get(0).getBankname());
                        PicassoTrustAll.getInstance(MoreCardTxActivity.this).load(bankListBeanList.get(0).getBankphoto()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(iv_bank);
                        cardnum = bankListBeanList.get(0).getCardno();
                        String showno = bankListBeanList.get(0).getCardno().substring(bankListBeanList.get(0).getCardno().length() - 4);
                        tv_card.setText("***************" + showno);
                    }else {
                        layout_off.setVisibility(View.VISIBLE);
                        layout_on.setVisibility(View.GONE);
                    }
                    break;
            }
            return false;
        }
    });


    private void httpGetcard(String url) {
        bankListBeanList = new ArrayList<MoreBankListBean>();
        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
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
                                Gson gson = new Gson();
                                bankListBeanList = gson.fromJson(list.toString(), new TypeToken<List<MoreBankListBean>>() {
                                }.getType());
                            }
                            handler.sendEmptyMessage(LOAD_REFRESH);
                        } else {
                            CustomToast.showToast(MoreCardTxActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    Log.d("eeee,,", e + "");
                    CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("eeee,,", error + "");
                CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }

//    //效验
//    public void httpCheck(String url,String pwd) {
//
//        dialog.show(getFragmentManager(),"1");
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("uid", UID);
//        params.put("trad_passwd", pwd);
//        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                String recode, msg;
//                try {
//                    if (response.has("recode")) {
//                        recode = response.getString("recode");
//                        msg = response.getString("msg");
//                        if (recode.equals("0")) {
//
//                            dialog.dismiss();
//                            postChong(HTTP_YUE_CHONG, "0.01");
//
//                        } else {
//                            dialog.dismiss();
//                            CustomToast.showToast(MoreCardTxActivity.this, msg, 1000);
//                        }
//                    } else {
//                        dialog.dismiss();
//                        CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
//                    }
//                } catch (Exception e) {
//                    Log.d("eeee,,",e+"");
//                    dialog.dismiss();
//                    CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("eeee,,",error+"");
//                dialog.dismiss();
//                CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
//            }
//        });
//        queue.add(request);
//    }

    private void postTixian(String url, String amount,String pass) {
        dialog.show(getFragmentManager(),"1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("tranpass", pass);
        params.put("getcount", String.valueOf((int) (Double.parseDouble(amount) * 100)));
        params.put("uid", UID);
        params.put("cardno", cardnum);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");
                        if (recode.equals("0")) {
                            dialog.dismiss();
                            CustomToast.showToast(
                                    MoreCardTxActivity.this,
                                    "提现成功", 1000);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            dialog.dismiss();
                            CustomToast.showToast(getBaseContext(), msg, 1000);
                        }
                    }else {
                        dialog.dismiss();
                        CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                CustomToast.showToast(MoreCardTxActivity.this, R.string.http_fail, 1000);

            }
        });
        queue.add(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_card_tx, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
