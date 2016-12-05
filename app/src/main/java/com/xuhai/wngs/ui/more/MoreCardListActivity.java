package com.xuhai.wngs.ui.more;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.more.MoreCardListAdapter;
import com.xuhai.wngs.adapters.more.MoreJFDDListAdapter;
import com.xuhai.wngs.beans.more.MoreBankListBean;
import com.xuhai.wngs.beans.more.MoreJFDDListBean;
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

public class MoreCardListActivity extends BaseActionBarAsUpActivity {
    private ListView lv_card;
    private MoreCardListAdapter cardListAdapter;
    private TextView tv_add;
    private ProgressDialogFragment newFragment;
    private List<MoreBankListBean> bankListBeanList;
    DialogWidget mDialogWidght;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_card_list);
        initView();
        newFragment = new ProgressDialogFragment();
        httpRequest(LOAD_REFRESH,HTTP_BANK_LIST + "?uid=" + UID);
    }

    private void initView(){
        lv_card = (ListView) findViewById(R.id.lv_card);
        tv_add = (TextView) findViewById(R.id.tv_add);

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreCardListActivity.this, MoreCardBd1Activity.class);
                startActivityForResult(intent, STATE_LOGOUT);
            }
        });
        lv_card.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                if (getIntent().getStringExtra("action").equals("choose")){
                    Intent intent = new Intent();
                    intent.putExtra("cardno",bankListBeanList.get(position).getCardno());
                    intent.putExtra("bankname",bankListBeanList.get(position).getBankname());
                    intent.putExtra("bankphoto",bankListBeanList.get(position).getBankphoto());
                    setResult(RESULT_OK,intent);
                    finish();
                }else {
                    new BottomSheet.Builder(MoreCardListActivity.this).sheet(R.menu.menu_card_sheet).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.jiechu:
                                    mDialogWidght = new DialogWidget(MoreCardListActivity.this,
                                            PayPwdView.getInstance("解除绑定", BANKUID, MoreCardListActivity.this, new PayPwdView.OnPayListener() {
                                                @Override
                                                public void onCancelPay() {

                                                    mDialogWidght.dismiss();
                                                    mDialogWidght = null;
                                                }

                                                @Override
                                                public void onSurePay(String s) {
                                                    httpJiebang(HTTP_BANK_REMOVE, bankListBeanList.get(position).getCardno(), s);
                                                    mDialogWidght.dismiss();
                                                    mDialogWidght = null;
                                                }
                                            }).getView());
                                    mDialogWidght.show();

                                    break;
                            }
                        }
                    }).show();
                }
            }
        });
    }


    public void httpJiebang(String url,String cardno,String pwd) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("cardno", cardno);
        params.put("uid", UID);
        params.put("tranpass", pwd);
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");
                        if (recode.equals("0")) {
                            httpRequest(LOAD_REFRESH,HTTP_BANK_LIST + "?uid=" + UID);
                        } else {
                            CustomToast.showToast(MoreCardListActivity.this, msg, 1000);
                        }
                    } else {
                        CustomToast.showToast(MoreCardListActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    Log.d("eeee,,",e+"");
                    CustomToast.showToast(MoreCardListActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("eeee,,",error+"");
                CustomToast.showToast(MoreCardListActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_REFRESH:

                    newFragment.dismiss();
                    cardListAdapter = new MoreCardListAdapter(MoreCardListActivity.this,bankListBeanList);
                    lv_card.setAdapter(cardListAdapter);
                    break;
                case LOAD_MORE:
                    break;
                case LOAD_FAIL:

                    break;
            }
            return false;
        }
    });


    private void httpRequest(final int loadstate, String url) {
        newFragment.show(getFragmentManager(),"1");
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
                                handler.sendEmptyMessage(loadstate);
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(MoreCardListActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(MoreCardListActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(MoreCardListActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(MoreCardListActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STATE_LOGOUT & resultCode == RESULT_OK){

            Log.d("jfkdddddddddd","here");
            httpRequest(LOAD_REFRESH, HTTP_BANK_LIST + "?uid=" + UID);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_card_list, menu);
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
}
