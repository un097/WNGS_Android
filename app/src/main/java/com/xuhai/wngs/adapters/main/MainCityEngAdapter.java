package com.xuhai.wngs.adapters.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.ShequListActivity;
import com.xuhai.wngs.beans.main.MainCityEngBean;
import com.xuhai.wngs.beans.main.ShequListBean;
import com.xuhai.wngs.ui.main.MainCitySelActivity;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by changliang on 14/11/5.
 */
public class MainCityEngAdapter extends BaseAdapter {


    private Context mContext;
    private List<MainCityEngBean> mList;

    private MainCityAdapter cityAdapter;
    public MainCityEngAdapter(Context context, List<MainCityEngBean> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_cityeng, null);
            holder = new ViewHolder();
            holder.tv_eng = (TextView) convertView.findViewById(R.id.tv_eng);
            holder.lv_city = (ListView) convertView.findViewById(R.id.lv_city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_eng.setText(mList.get(position).getEng());


        cityAdapter = new MainCityAdapter(mContext,mList.get(position).getCitylist());
        holder.lv_city.setAdapter(cityAdapter);
        holder.lv_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, ShequListActivity.class);
                intent.putExtra("cityCode",mList.get(position).getCitylist().get(i).getCityCode());
                ((MainCitySelActivity)mContext).startActivityForResult(intent, 100);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView tv_eng;
        ListView lv_city;
    }
}
