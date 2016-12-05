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
import com.xuhai.wngs.beans.more.MoreWDKDListBean;

import java.util.List;

/**
 * Created by WR on 2014/12/10.
 */
public class MoreWDKDListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreWDKDListBean> mList;

    public MoreWDKDListAdapter(Context context, List<MoreWDKDListBean> list) {
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
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getRead() ==null){

        }else {
            if (mList.get(position).getRead().equals("0")){
                holder.image.setImageResource(R.drawable.round);
            }else if (mList.get(position).getRead().equals("1")){
                holder.image.setImageResource(R.drawable.selector_half_transp);
            }
        }
        holder.title.setText(mList.get(position).getTitle());
        holder.date.setText(mList.get(position).getDate());
        if (mList.get(position).getOut().equals("0")){
            holder.flag.setText("未领取");
            holder.flag.setTextColor(R.color.orange_dark);
        }else if (mList.get(position).getOut().equals("1")){
            holder.flag.setText("已领取");
            holder.flag.setTextColor(R.color.green_dark);
        }else if (mList.get(position).getOut().equals("3")){
            holder.flag.setText("保留");
            holder.flag.setTextColor(R.color.color_text_gray);
        }



        return convertView;
    }

    private class ViewHolder {

        TextView title;
        TextView date;
        TextView flag;
        ImageView image;
    }
}
