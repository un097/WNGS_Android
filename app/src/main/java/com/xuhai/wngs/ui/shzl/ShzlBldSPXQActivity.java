package com.xuhai.wngs.ui.shzl;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.pager.ImagePagerAdapter;
import com.xuhai.wngs.adapters.pager.SpxqImagePagerAdapter;
import com.xuhai.wngs.beans.shzl.ShzlBldCPLBBean;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.MyViewpager;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class ShzlBldSPXQActivity extends BaseActionBarAsUpActivity {

    private static final String TAG = "ShzlBldSPXQActivity";
    private JSONArray imglist;
    private String msg, m_goods, m_content, m_price, m_imgs;
    private ImageView[] imageViews = null;
    private ImageView imageView = null;
    private AutoScrollViewPager advPager = null; //滚动图片
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
    private String goodsid, goodsimg, m_stock;
    private List<String> list = null;
    private ImageView gouwuche, iv_jian, iv_tejia;
    private TextView name, jieshao, tv_price, tv_allnumber, tv_allprice, tv_stock;
    ImageView iv_add;
    EditText et_itemnumber;
    private ViewGroup group;

    private int itemnumber = 0;
    private Boolean isResult;
    private ProgressDialogFragment newFragment;

    private String sgoodsid, scount, sprice, m_tag,storeid,sstoreid;
    private int allnumber = 0;
    private double allprice = 0.00;
    private int CAR_ITEM = 0;
    private int nextnum = 0;
    private RelativeLayout layout_all, rl_goods;
    private String isFromcar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bld_spxq);

        isFromcar = getIntent().getStringExtra("carOn");
        goodsid = getIntent().getStringExtra("goodsid");
        goodsimg = getIntent().getStringExtra("goodsimg");
        storeid = getIntent().getStringExtra("storeid");
        //   price = getIntent().getStringExtra("price");
        et_itemnumber = (EditText) findViewById(R.id.spxq_number);
        et_itemnumber.setInputType(InputType.TYPE_NULL);
        tv_allnumber = (TextView) findViewById(R.id.bar_number);
        tv_allprice = (TextView) findViewById(R.id.bar_jiage);
        iv_tejia = (ImageView) findViewById(R.id.spxq_tejia);
        tv_stock = (TextView) findViewById(R.id.spxq_stock);

        initImageView();

        dbdate();

        httpRequest(HTTP_BLD_SPXQ + "?sqid=" + SQID + "&goodsid=" + goodsid + "&storeid=" + storeid);


    }

    private void initImageView() {
        rl_goods = (RelativeLayout) findViewById(R.id.rl_goods);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rl_goods.getLayoutParams();
        params.height = screenWidth * 360 / 640;
        rl_goods.setLayoutParams(params);

        advPager = (AutoScrollViewPager) findViewById(R.id.adv_pager);
    }

    private void initView() {
        layout_all = (RelativeLayout) findViewById(R.id.layout_all);

        layout_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    et_itemnumber.setCursorVisible(false);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        });
        name = (TextView) findViewById(R.id.spxq_name);
        jieshao = (TextView) findViewById(R.id.tv2);
        tv_price = (TextView) findViewById(R.id.spxq_price);
        gouwuche = (ImageView) findViewById(R.id.gouwuche);
        iv_jian = (ImageView) findViewById(R.id.spxq_jian);
        iv_add = (ImageView) findViewById(R.id.spxq_add);


        if (itemnumber <= 0) {
            iv_jian.setVisibility(View.GONE);
            et_itemnumber.setVisibility(View.GONE);
        } else {
            iv_jian.setVisibility(View.VISIBLE);
            et_itemnumber.setVisibility(View.VISIBLE);
        }


        et_itemnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (isResult) {
                    nextnum = 0;
                } else {
                    if (s.length() > 0) {
                        nextnum = Integer.parseInt(s.toString().trim());
                    } else {
                        nextnum = 0;
                    }
                }
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (Integer.parseInt(s.toString()) > 0) {
                        if (Integer.parseInt(String.valueOf(s)) > Integer.valueOf(m_stock)) {
                            et_itemnumber.setText(m_stock);
                            iv_add.setImageResource(R.drawable.btn_bld_add_huise);
                            jisuana(Integer.valueOf(m_stock));

                        } else {
                            iv_add.setImageResource(R.drawable.btn_bld_add);
                            jisuan(s);
                        }

                    } else {
                        itemnumber = 0;
                        iv_jian.setVisibility(View.GONE);
                        et_itemnumber.setVisibility(View.GONE);

                        allnumber = allnumber - nextnum;
                        BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                        BigDecimal b2 = new BigDecimal(m_price);
                        BigDecimal b3 = new BigDecimal(nextnum);
                        allprice = b1.subtract(b2.multiply(b3)).doubleValue();

                        database.delete("shopcart", "goodsid=?", new String[]{goodsid});
                    }
                } else {
                    itemnumber = 0;
                    allnumber = allnumber - nextnum;
                    BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                    BigDecimal b2 = new BigDecimal(m_price);
                    BigDecimal b3 = new BigDecimal(nextnum);
                    allprice = b1.subtract(b2.multiply(b3)).doubleValue();
                    database.delete("shopcart", "goodsid=?", new String[]{goodsid});
                }

                tv_allnumber.setText(String.valueOf(allnumber));
                tv_allprice.setText(String.valueOf(allprice));
            }
        });


        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_stock==null||m_stock.equals("0")){
                    iv_add.setImageResource(R.drawable.btn_bld_add_huise);
                }else {
                    if (Integer.parseInt(String.valueOf(et_itemnumber.getText())) > Integer.valueOf(m_stock)) {
                        return;
                    } else {
                        itemnumber++;

                        et_itemnumber.setText(String.valueOf(itemnumber));
                    }
                }
            }
        });

        iv_jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemnumber--;
                et_itemnumber.setText(String.valueOf(itemnumber));
            }
        });


        gouwuche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromcar == null) {
                    if (tv_allnumber.getText().toString().equals("0")) {
                        CustomToast.showToast(ShzlBldSPXQActivity.this, "您还没有选择商品！！！", 1000);
                    } else {
                        Intent intent = new Intent(ShzlBldSPXQActivity.this, ShzlBldCARActivity.class);
                        intent.putExtra("storeid",storeid);
                        startActivityForResult(intent, CAR_ITEM);
                    }
                } else {
                    setResult(CAR_ITEM);
                    finish();
                }

            }
        });
    }

    private void jisuan(Editable s) {
        itemnumber = Integer.parseInt(s.toString());
        iv_jian.setVisibility(View.VISIBLE);
        et_itemnumber.setVisibility(View.VISIBLE);
        Cursor cursor = database.rawQuery("select * from shopcart where goodsid=?", new String[]{goodsid});
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String number = cursor.getString(cursor.getColumnIndex("count"));
                allnumber = allnumber - Integer.parseInt(number);
                BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                BigDecimal b2 = new BigDecimal(m_price);
                BigDecimal b3 = new BigDecimal(number);
                allprice = b1.subtract(b2.multiply(b3)).doubleValue();

                allnumber = allnumber + Integer.parseInt(s.toString());
                BigDecimal b4 = new BigDecimal(Double.toString(allprice));
                BigDecimal b5 = new BigDecimal(m_price);
                BigDecimal b6 = new BigDecimal(s.toString());
                allprice = b4.add(b5.multiply(b6)).doubleValue();

            }
            ContentValues values = new ContentValues();
            values.put("count", s.toString().trim());//key为字段名，value为值
            database.update("shopcart", values, "goodsid=?", new String[]{goodsid});


        } else {
            database.execSQL("insert into shopcart(sqid,storeid,goodsid,goods,goodsimg,sales,price,count)  values('" + SQID + "','" + storeid + "','" + goodsid + "','" + m_goods + "','" + goodsimg + "','" + m_stock + "','" + m_price + "','" + itemnumber + "')");
            allnumber = allnumber + Integer.parseInt(s.toString());
            BigDecimal b4 = new BigDecimal(Double.toString(allprice));
            BigDecimal b5 = new BigDecimal(m_price);
            BigDecimal b6 = new BigDecimal(s.toString());

            allprice = b4.add(b5.multiply(b6)).doubleValue();
        }
    }

    private void jisuana(Integer s) {
        itemnumber = s;
        iv_jian.setVisibility(View.VISIBLE);
        et_itemnumber.setVisibility(View.VISIBLE);
        Cursor cursor = database.rawQuery("select * from shopcart where goodsid=?", new String[]{goodsid});
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String number = cursor.getString(cursor.getColumnIndex("count"));
                allnumber = allnumber - Integer.parseInt(number);
                BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                BigDecimal b2 = new BigDecimal(m_price);
                BigDecimal b3 = new BigDecimal(number);
                allprice = b1.subtract(b2.multiply(b3)).doubleValue();

                allnumber = allnumber + Integer.parseInt(s.toString());
                BigDecimal b4 = new BigDecimal(Double.toString(allprice));
                BigDecimal b5 = new BigDecimal(m_price);
                BigDecimal b6 = new BigDecimal(s);
                allprice = b4.add(b5.multiply(b6)).doubleValue();

            }
            ContentValues values = new ContentValues();
            values.put("count", s);//key为字段名，value为值
            database.update("shopcart", values, "goodsid=?", new String[]{goodsid});


        } else {
            database.execSQL("insert into shopcart(sqid,storeid,goodsid,goods,goodsimg,sales,price,count)  values('" + SQID + "','" + storeid + "','" + goodsid + "','" + m_goods + "','" + goodsimg + "','" + m_stock + "','" + m_price + "','" + itemnumber + "')");
            allnumber = allnumber + s;
            BigDecimal b4 = new BigDecimal(Double.toString(allprice));
            BigDecimal b5 = new BigDecimal(m_price);
            BigDecimal b6 = new BigDecimal(s);

            allprice = b4.add(b5.multiply(b6)).doubleValue();
        }
    }


    private void httpRequest(String url) {
        newFragment = new ProgressDialogFragment();
        newFragment.show(getFragmentManager(), "1");
        list = new ArrayList<String>();
        JsonObjectHeadersRequest request = new JsonObjectHeadersRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            if (response.has("goods")) {
                                m_goods = response.getString("goods");
                            }
                            if (response.has("content")) {
                                m_content = response.getString("content");
                            }
                            if (response.has("price")) {
                                m_price = response.getString("price");
                            }
                            if (response.has("tag")) {
                                m_tag = response.getString("tag");
                            }if (response.has("stock")) {
                                m_stock = response.getString("stock");
                            }
                            try {
                                JSONObject json = new JSONObject(response.toString());
                                imglist = json.getJSONArray("imgs");
                                for (int i = 0; i < imglist.length(); i++) {//遍历JSONArray
                                    list.add(imglist.get(i).toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            newFragment.dismiss();
                            handler.sendEmptyMessage(0);

                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(ShzlBldSPXQActivity.this, msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                        CustomToast.showToast(ShzlBldSPXQActivity.this, R.string.http_fail, 1000);
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    CustomToast.showToast(ShzlBldSPXQActivity.this, R.string.http_fail, 1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
                CustomToast.showToast(ShzlBldSPXQActivity.this, R.string.http_fail, 1000);
            }
        });
        queue.add(request);
    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initView();
                    if (m_tag != null){
                        if (m_tag.equals("0")){
                            iv_tejia.setVisibility(View.GONE);
                        }else if (m_tag.equals("1")){
                            iv_tejia.setVisibility(View.VISIBLE);
                        }
                    }

                    tv_stock.setText("库存:" + m_stock + "件");
                    layout_all.setVisibility(View.VISIBLE);
                    name.setText(m_goods);
                    jieshao.setText(m_content);
                    tv_price.setText(getResources().getString(R.string.yang) + " " + m_price);

                    SpxqImagePagerAdapter adapter = new SpxqImagePagerAdapter(getSupportFragmentManager(), list);
                    advPager.setAdapter(adapter);
                    advPager.setInterval(3000);
                    advPager.startAutoScroll();

                    break;

                case 1:
                    et_itemnumber.setText(String.valueOf(itemnumber));
                    tv_allnumber.setText(String.valueOf(allnumber));
                    tv_allprice.setText(String.valueOf(allprice));
                    isResult = false;
                    break;

            }
            return false;
        }
    });

    private void dbdate() {

        Cursor cursor = database.rawQuery("select * from shopcart", null);
        while (cursor.moveToNext()) {
            scount = cursor.getString(cursor.getColumnIndex("count"));
            sgoodsid = cursor.getString(cursor.getColumnIndex("goodsid"));
            sprice = cursor.getString(cursor.getColumnIndex("price"));

            if (sgoodsid.equals(goodsid)) {
                itemnumber = Integer.parseInt(scount);
            }
            allnumber = allnumber + Integer.parseInt(scount);
            BigDecimal b1 = new BigDecimal(Double.toString(allprice));
            BigDecimal b21 = new BigDecimal(sprice);
            BigDecimal b2 = new BigDecimal(Double.toString(b21.multiply(BigDecimal.valueOf(Integer.parseInt(scount))).doubleValue()));
            allprice = b1.add(b2).doubleValue();

        }
        cursor.close();
        handler.sendEmptyMessage(1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CAR_ITEM) {
            allnumber = 0;
            allprice = 0.00;
            itemnumber = 0;
            isResult = true;
            dbdate();
        } else if (resultCode == 19) {
            setResult(19);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shzl_bld_spxq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:

                setResult(CAR_ITEM);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop auto scroll when onPause
        advPager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start auto scroll when onResume
        advPager.startAutoScroll();
    }

}
