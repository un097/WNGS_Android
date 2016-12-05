package com.xuhai.wngs.adapters.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.main.MainBean;
import com.xuhai.wngs.beans.main.MainListBean;
import com.xuhai.wngs.beans.main.MainModdtlBean;
import com.xuhai.wngs.utils.ClickEffect;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

/**
 * Created by changliang on 14-10-10.
 */
public class MainGridAdapter extends BaseAdapter {

    private List<MainModdtlBean> mList;
    private Context mContext;

    public MainGridAdapter(Context context, List<MainModdtlBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_grid_main, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.imageView);
            holder.read_iv = (ImageView) view.findViewById(R.id.read_iv);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (mList.get(i).getFucimg() != null & !mList.get(i).getFucimg().equals("")) {
            PicassoTrustAll.getInstance(mContext).load(mList.get(i).getFucimg()).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(holder.imageView);
        }
        holder.tv_name.setText(mList.get(i).getFucname());

        if (mList.get(i).getExpress() != null){
            if (mList.get(i).getExpress().equals("1")) {
                Bitmap bitMap = Bitmap.createBitmap(mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8), mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8), Bitmap.Config.ARGB_8888);
                bitMap = bitMap.copy(bitMap.getConfig(), true);
                // Construct a canvas with the specified bitmap to draw into
                Canvas canvas = new Canvas(bitMap);
                // Create a new paint with default settings.
                Paint paint = new Paint();
                // smooths out the edges of what is being drawn
                paint.setAntiAlias(true);
                // set color0xfff33342
                paint.setColor(0xffd5293f);
                // set style
                paint.setStyle(Paint.Style.FILL);
                // set stroke
                paint.setStrokeWidth(1.0f);
                // draw circle with radius 30
                canvas.drawCircle(mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, paint);
                // set on ImageView or any other view
                holder.read_iv.setImageBitmap(bitMap);
            }
        }

        if (mList.get(i).getInfo() != null){
            if (mList.get(i).getInfo().equals("1")) {
                Bitmap bitMap = Bitmap.createBitmap(mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8), mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8), Bitmap.Config.ARGB_8888);
                bitMap = bitMap.copy(bitMap.getConfig(), true);
                // Construct a canvas with the specified bitmap to draw into
                Canvas canvas = new Canvas(bitMap);
                // Create a new paint with default settings.
                Paint paint = new Paint();
                // smooths out the edges of what is being drawn
                paint.setAntiAlias(true);
                // set color0xfff33342
                paint.setColor(0xffd5293f);
                // set style
                paint.setStyle(Paint.Style.FILL);
                // set stroke
                paint.setStrokeWidth(1.0f);
                // draw circle with radius 30
                canvas.drawCircle(mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, paint);
                // set on ImageView or any other view
                holder.read_iv.setImageBitmap(bitMap);
            }
        }
        if (mList.get(i).getBbs() != null){
            if (mList.get(i).getBbs().equals("1")) {
                Bitmap bitMap = Bitmap.createBitmap(mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8), mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8), Bitmap.Config.ARGB_8888);
                bitMap = bitMap.copy(bitMap.getConfig(), true);
                // Construct a canvas with the specified bitmap to draw into
                Canvas canvas = new Canvas(bitMap);
                // Create a new paint with default settings.
                Paint paint = new Paint();
                // smooths out the edges of what is being drawn
                paint.setAntiAlias(true);
                // set color0xfff33342
                paint.setColor(0xffd5293f);
                // set style
                paint.setStyle(Paint.Style.FILL);
                // set stroke
                paint.setStrokeWidth(1.0f);
                // draw circle with radius 30
                canvas.drawCircle(mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, mContext.getResources().getDimensionPixelSize(R.dimen.base_dimen_8) / 2, paint);
                // set on ImageView or any other view
                holder.read_iv.setImageBitmap(bitMap);
            }
        }
        return view;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView tv_name;
        private ImageView read_iv;
    }
}
