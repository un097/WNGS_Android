package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreJFDDListBean;
import com.xuhai.wngs.beans.more.MoreSHDZListBean;

import java.util.List;

/**
 * Created by WR on 2014/11/27.
 */
public class MoreJFDDListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreJFDDListBean> mList;

    public MoreJFDDListAdapter(Context context, List<MoreJFDDListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_jfdd, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.flag = (TextView) convertView.findViewById(R.id.flag);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.title.setText(mList.get(position).getTitle());
        holder.date.setText(mList.get(position).getDate());
        if (mList.get(position).getFlag().equals("0")){
            holder.flag.setText("未领取");
        }else if (mList.get(position).getFlag().equals("1")){
            holder.flag.setText("已领取");
        }



        return convertView;
    }

    private class ViewHolder {

        TextView title;
        TextView date;
        TextView flag;
    }
}
