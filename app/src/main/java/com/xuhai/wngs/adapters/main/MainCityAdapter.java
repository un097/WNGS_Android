package com.xuhai.wngs.adapters.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xuhai.wngs.R;
import com.xuhai.wngs.ShequListActivity;
import com.xuhai.wngs.beans.main.MainCityBean;
import com.xuhai.wngs.beans.main.MainCityEngBean;

import java.util.List;

/**
 * Created by changliang on 14/11/5.
 */
public class MainCityAdapter extends BaseAdapter {


    private Context mContext;
    private List<MainCityBean> mList;


    public MainCityAdapter(Context context, List<MainCityBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_city, null);
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
