package com.xuhai.wngs.ui.shzl;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.adapters.shzl.ShzlBldCARListAdapter;
import com.xuhai.wngs.beans.shzl.ShzlBldCPLBBean;
import com.xuhai.wngs.ui.more.LoginActivity;
import com.xuhai.wngs.ui.wyfw.WyfwZFCXActivity;

import java.util.ArrayList;
import java.util.List;

public class ShzlBldCARActivity extends BaseActionBarAsUpActivity {

    private String goods, goodsimg, sales, price, count, goodsid;
    public String storeid;
    private List <ShzlBldCPLBBean> carBeanlist;
    private ShzlBldCARListAdapter bldCARListAdapter;
    private ListView carlistview;
    private TextView tv_number,tv_jiage;
    private Context context;
    public int CAR_ITEM = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shzl_bld_car);

        initView();
        dbdate();


    }


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:

                    break;
                case LOAD_REFRESH:
                    bldCARListAdapter = new ShzlBldCARListAdapter(ShzlBldCARActivity.this, carBeanlist);

                    carlistview.setAdapter(bldCARListAdapter);

                    bldCARListAdapter.notifyDataSetChanged();

                    break;
                case LOAD_MORE:
                    bldCARListAdapter.notifyDataSetChanged();
                    break;
                case LOAD_FAIL:

                    break;
            }
            return false;
        }
    });

    private void initView() {
        carlistview = (ListView) findViewById(R.id.listView);
         tv_number = (TextView) findViewById(R.id.car_number);
         tv_jiage = (TextView) findViewById(R.id.car_jiage);
//        carlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Intent intent = new Intent(ShzlBldCARActivity.this, ShzlBldSPXQActivity.class);
////                intent.putExtra("goodsid", carBeanlist.get(position).getGoodsid());
////                intent.putExtra("goodsimg",carBeanlist.get(position).getGoodsimg());
////                intent.putExtra("price",carBeanlist.get(position).getPrice());
////                startActivityForResult(intent,CAR_ITEM);
//            }
//        });


        ImageView qujiesuan=(ImageView)findViewById(R.id.qujiesuan);
        qujiesuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_LOGIN) {
                    Intent intent = new Intent(ShzlBldCARActivity.this, ShzlBldQRDDActivity.class);
                    intent.putExtra("storeid",storeid);
                    intent.putExtra("allprice",tv_jiage.getText().toString().trim());

                    startActivityForResult(intent,19);
                }else {
                    Intent intent=new Intent(ShzlBldCARActivity.this,LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
            }
        });
    }

    public void listItemClick(int position) {
        Intent intent = new Intent(ShzlBldCARActivity.this, ShzlBldSPXQActivity.class);
        intent.putExtra("goodsid", carBeanlist.get(position).getGoodsid());
        intent.putExtra("goodsimg",carBeanlist.get(position).getGoodsimg());
        intent.putExtra("price",carBeanlist.get(position).getPrice());
        intent.putExtra("carOn","car");
        startActivityForResult(intent,CAR_ITEM);
    }

    private void dbdate(){
        carBeanlist = new ArrayList<ShzlBldCPLBBean>();
        Cursor cursor = database.rawQuery("select * from shopcart", null);
        while (cursor.moveToNext()) {
            ShzlBldCPLBBean bean = new ShzlBldCPLBBean();
            sales = cursor.getString(cursor.getColumnIndex("sales"));
            goodsid = cursor.getString(cursor.getColumnIndex("goodsid"));
            price = cursor.getString(cursor.getColumnIndex("price"));
            count = cursor.getString(cursor.getColumnIndex("count"));
            goods = cursor.getString(cursor.getColumnIndex("goods"));
            goodsimg = cursor.getString(cursor.getColumnIndex("goodsimg"));
            storeid = cursor.getString(cursor.getColumnIndex("storeid"));
            bean.setGoods(goods);
            bean.setStock(sales);
            bean.setPrice(price);
            bean.setCount(count);
            bean.setGoodsimg(goodsimg);
            bean.setGoodsid(goodsid);
            carBeanlist.add(bean);

        }
        handler.sendEmptyMessage(LOAD_REFRESH);

    }

    public void setbelow (int allnumber, double allprice) {

        tv_number.setText(String.valueOf(allnumber));
        tv_jiage.setText(String.valueOf(allprice));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CAR_ITEM){
            handler.sendEmptyMessage(LOAD_REFRESH);
        }else if (resultCode == LOGIN_SUCCESS){
            IS_LOGIN = spn.getBoolean(SPN_IS_LOGIN, false);
            Intent intent = new Intent(ShzlBldCARActivity.this, ShzlBldQRDDActivity.class);
            intent.putExtra("allprice",tv_jiage.getText().toString().trim());
            startActivityForResult(intent,19);
        }else if (requestCode == 19 && resultCode == 19){
            setResult(19);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shzl_bld_car, menu);
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
            setResult(CAR_ITEM);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
