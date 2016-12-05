package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreSHDZListBean;
import com.xuhai.wngs.beans.more.MoreWDJFListBean;

import java.util.List;

/**
 * Created by WR on 2014/11/27.
 */
public class MoreWDJFListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreWDJFListBean> mList;

    public MoreWDJFListAdapter(Context context, List<MoreWDJFListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_wdjf, null);
            holder = new ViewHolder();
            holder.note = (TextView) convertView.findViewById(R.id.tv_note);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.points = (TextView) convertView.findViewById(R.id.tv_points);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.note.setText(mList.get(position).getNote());
        holder.date.setText(mList.get(position).getDate());
        holder.points.setText(mList.get(position).getPoints());
        if (mList.get(position).getTag().equals("1")){
            holder.points.setText("+"+mList.get(position).getPoints());
            holder.points.setTextColor(mContext.getResources().getColor(R.color.red));
        }else {
            holder.points.setText("-"+mList.get(position).getPoints());
            holder.points.setTextColor(mContext.getResources().getColor(R.color.green));}

        return convertView;
    }

    private class ViewHolder {
        TextView note;
        TextView date;
        TextView points;
    }
}
