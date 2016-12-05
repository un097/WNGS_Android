package com.xuhai.wngs.adapters.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.main.MainCityBean;

import java.util.List;

/**
 * Created by changliang on 14/11/5.
 */
public class MainCityHotAdapter extends BaseAdapter {


    private Context mContext;
    private List<MainCityBean> mList;

    public MainCityHotAdapter(Context context, List<MainCityBean> list) {
        this.mContext = context;
        this.mList = list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_cityhot, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tv_name.setText(mList.get(position).getCityText());

        return convertView;
    }

    private class ViewHolder {
        TextView tv_name;
    }
}
