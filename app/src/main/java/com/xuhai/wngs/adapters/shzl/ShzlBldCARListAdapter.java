package com.xuhai.wngs.adapters.shzl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.shzl.ShzlBldCPLBBean;
import com.xuhai.wngs.ui.shzl.ShzlBldCARActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.math.BigDecimal;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/12/4.
 */
public class ShzlBldCARListAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShzlBldCPLBBean> mList;
    private int[] itemnumber;
    private int allnumber = 0;
    private int nextnum;
    private double allprice = 0.00;
    private String scount, sgoodsid, sprice;

    public ShzlBldCARListAdapter(Context context, List<ShzlBldCPLBBean> list) {
        this.mContext = context;
        this.mList = list;
        itemnumber = new int[mList.size()];


        Cursor cursor = ((ShzlBldCARActivity) mContext).database.rawQuery("select * from shopcart", null);
        while (cursor.moveToNext()) {
            scount = cursor.getString(cursor.getColumnIndex("count"));
            sgoodsid = cursor.getString(cursor.getColumnIndex("goodsid"));
            sprice = cursor.getString(cursor.getColumnIndex("price"));
            for (int i = 0; i <= mList.size() - 1; i++) {
                if (mList.get(i).getGoodsid().equals(sgoodsid)) {
                    itemnumber[i] = Integer.parseInt(scount);
                }
            }
            allnumber = allnumber + Integer.parseInt(scount);
            BigDecimal b1 = new BigDecimal(Double.toString(allprice));
            BigDecimal b21 = new BigDecimal(sprice);
            BigDecimal b2 = new BigDecimal(Double.toString(b21.multiply(BigDecimal.valueOf(Integer.parseInt(scount))).doubleValue()));
            allprice = b1.add(b2).doubleValue();
        }
        ((ShzlBldCARActivity) mContext).setbelow(allnumber, allprice);


    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    int index = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        //  if (convertView == null) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_shzl_bld_cplb, null);
        holder = new ViewHolder();
        holder.title = (TextView) convertView.findViewById(R.id.cplp_title);
        holder.sales = (TextView) convertView.findViewById(R.id.cplp_sales);
        holder.number = (EditText) convertView.findViewById(R.id.cplb_number);
        holder.price = (TextView) convertView.findViewById(R.id.cplb_price);
        holder.image = (CircleImageView) convertView.findViewById(R.id.image);
        holder.jian = (ImageView) convertView.findViewById(R.id.cplb_jian);
        holder.iv_add = (ImageView) convertView.findViewById(R.id.cplb_add);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }

        if (itemnumber[position] <= 0) {
            holder.jian.setVisibility(View.GONE);
            holder.number.setVisibility(View.GONE);
        } else {
            holder.jian.setVisibility(View.VISIBLE);
            holder.number.setVisibility(View.VISIBLE);
        }
        holder.number.setText(String.valueOf(itemnumber[position]));

        holder.number.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    index = position;
                }
                return false;
            }
        });
        holder.sales.setText("库存："+mList.get(position).getStock()+"件");
        holder.number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                if (s.length() > 0) {
                    nextnum = Integer.parseInt(s.toString().trim());
                } else {
                    nextnum = 0;
                }
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (Integer.parseInt(s.toString()) > 0) {
                        if (Integer.parseInt(String.valueOf(s)) > Integer.valueOf(mList.get(position).getStock())) {
                            holder.number.setText(mList.get(position).getStock());
                            holder.iv_add.setImageResource(R.drawable.btn_bld_add_huise);
                       //     jisuana(Integer.valueOf(mList.get(position).getStock()), holder, position);

                        } else {
                            holder.iv_add.setImageResource(R.drawable.btn_bld_add);
                            jisuan(s, holder, position);
                        }

                    } else {
                        itemnumber[position] = 0;
                        holder.jian.setVisibility(View.GONE);
                        holder.number.setVisibility(View.GONE);

                        allnumber = allnumber - nextnum;
                        BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                        BigDecimal b2 = new BigDecimal(mList.get(position).getPrice());
                        BigDecimal b3 = new BigDecimal(nextnum);
                        allprice = b1.subtract(b2.multiply(b3)).doubleValue();

                        ((ShzlBldCARActivity) mContext).database.delete("shopcart", "goodsid=?", new String[]{mList.get(position).getGoodsid()});
                    }
                } else {
                    itemnumber[position] = 0;
                    allnumber = allnumber - nextnum;
                    BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                    BigDecimal b2 = new BigDecimal(mList.get(position).getPrice());
                    BigDecimal b3 = new BigDecimal(nextnum);
                    allprice = b1.subtract(b2.multiply(b3)).doubleValue();

                    ((ShzlBldCARActivity) mContext).database.delete("shopcart", "goodsid=?", new String[]{mList.get(position).getGoodsid()});
                }
                ((ShzlBldCARActivity) mContext).setbelow(allnumber, allprice);

            }
        });


        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mList.get(position).getStock()==null||mList.get(position).getStock().equals("0")){
//                    holder.iv_add.setImageResource(R.drawable.btn_bld_add_huise);
//                }else {
                    if (Integer.parseInt(String.valueOf(holder.number.getText())) >= Integer.valueOf(mList.get(position).getStock())) {
                        holder.iv_add.setImageResource(R.drawable.btn_bld_add_huise);
                    } else {
                        itemnumber[position]++;

                        holder.number.setText(String.valueOf(itemnumber[position]));
                        //              ((ShzlBldCARActivity) mContext).setbelow(allnumber,allprice);
                    }
                }
           // }
        });
        holder.jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemnumber[position]--;
                holder.number.setText(String.valueOf(itemnumber[position]));

            }
        });


        if (mList.get(position).getGoodsimg() == null || mList.get(position).getGoodsimg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getGoodsimg()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.image);
        }

        holder.title.setText(mList.get(position).getGoods());
        holder.price.setText(mContext.getResources().getString(R.string.yang) + " " + mList.get(position).getPrice());
        //  holder.sales.setText(mList.get(position).getSales());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ShzlBldCARActivity) mContext).listItemClick(position);
            }
        });
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                holder.number.setCursorVisible(false);
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {

                    imm.hideSoftInputFromWindow(((ShzlBldCARActivity) mContext).getCurrentFocus().getWindowToken(), 0);
                }

                return false;
            }
        });

        holder.number.clearFocus();
        if (index != -1 && index == position) {
            // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
            holder.number.requestFocus();

        }
        return convertView;
    }

    private void jisuan(Editable s, ViewHolder holder, int position) {
        itemnumber[position] = Integer.parseInt(s.toString());
        holder.jian.setVisibility(View.VISIBLE);
        holder.number.setVisibility(View.VISIBLE);
        Cursor cursor = ((ShzlBldCARActivity) mContext).database.rawQuery("select * from shopcart where goodsid=?", new String[]{mList.get(position).getGoodsid()});
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String number = cursor.getString(cursor.getColumnIndex("count"));
                allnumber = allnumber - Integer.parseInt(number);
                BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                BigDecimal b2 = new BigDecimal(mList.get(position).getPrice());
                BigDecimal b3 = new BigDecimal(number);
                allprice = b1.subtract(b2.multiply(b3)).doubleValue();

                allnumber = allnumber + Integer.parseInt(s.toString());
                BigDecimal b4 = new BigDecimal(Double.toString(allprice));
                BigDecimal b5 = new BigDecimal(mList.get(position).getPrice());
                BigDecimal b6 = new BigDecimal(s.toString());
                allprice = b4.add(b5.multiply(b6)).doubleValue();

            }
            ContentValues values = new ContentValues();
            values.put("count", s.toString().trim());//key为字段名，value为值
            ((ShzlBldCARActivity) mContext).database.update("shopcart", values, "goodsid=?", new String[]{mList.get(position).getGoodsid()});


        } else {
            ((ShzlBldCARActivity) mContext).database.execSQL("insert into shopcart(sqid,storeid,goodsid,goods,goodsimg,sales,price,count)  values('" + ((ShzlBldCARActivity) mContext).SQID + "','" + ((ShzlBldCARActivity) mContext).storeid + "','" + mList.get(position).getGoodsid() + "','" + mList.get(position).getGoods() + "','" + mList.get(position).getGoodsimg() + "','" + mList.get(position).getStock() + "','" + mList.get(position).getPrice() + "','" + itemnumber[position] + "')");
            allnumber = allnumber + Integer.parseInt(s.toString());
            BigDecimal b4 = new BigDecimal(Double.toString(allprice));
            BigDecimal b5 = new BigDecimal(mList.get(position).getPrice());
            BigDecimal b6 = new BigDecimal(s.toString());

            allprice = b4.add(b5.multiply(b6)).doubleValue();
        }
    }

    private void jisuana(Integer s, ViewHolder holder, int position) {
        itemnumber[position] = s;
        holder.jian.setVisibility(View.VISIBLE);
        holder.number.setVisibility(View.VISIBLE);
        Cursor cursor = ((ShzlBldCARActivity) mContext).database.rawQuery("select * from shopcart where goodsid=?", new String[]{mList.get(position).getGoodsid()});
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String number = cursor.getString(cursor.getColumnIndex("count"));
                allnumber = allnumber - Integer.parseInt(number);
                BigDecimal b1 = new BigDecimal(Double.toString(allprice));
                BigDecimal b2 = new BigDecimal(mList.get(position).getPrice());
                BigDecimal b3 = new BigDecimal(number);
                allprice = b1.subtract(b2.multiply(b3)).doubleValue();

                allnumber = allnumber + s;
                BigDecimal b4 = new BigDecimal(Double.toString(allprice));
                BigDecimal b5 = new BigDecimal(mList.get(position).getPrice());
                BigDecimal b6 = new BigDecimal(s);
                allprice = b4.add(b5.multiply(b6)).doubleValue();

            }
            ContentValues values = new ContentValues();
            values.put("count", s);//key为字段名，value为值
            ((ShzlBldCARActivity) mContext).database.update("shopcart", values, "goodsid=?", new String[]{mList.get(position).getGoodsid()});


        } else {
            ((ShzlBldCARActivity) mContext).database.execSQL("insert into shopcart(sqid,storeid,goodsid,goods,goodsimg,sales,price,count)  values('" + ((ShzlBldCARActivity) mContext).SQID + "','" + ((ShzlBldCARActivity) mContext).storeid + "','" + mList.get(position).getGoodsid() + "','" + mList.get(position).getGoods() + "','" + mList.get(position).getGoodsimg() + "','" + mList.get(position).getStock() + "','" + mList.get(position).getPrice() + "','" + itemnumber[position] + "')");
            allnumber = allnumber + s;
            BigDecimal b4 = new BigDecimal(Double.toString(allprice));
            BigDecimal b5 = new BigDecimal(mList.get(position).getPrice());
            BigDecimal b6 = new BigDecimal(s);

            allprice = b4.add(b5.multiply(b6)).doubleValue();
        }
    }

    private class ViewHolder {
        CircleImageView image;
        ImageView jian;
        ImageView iv_add;
        TextView sales;
        TextView title;
        EditText number;
        TextView price;
    }
}


