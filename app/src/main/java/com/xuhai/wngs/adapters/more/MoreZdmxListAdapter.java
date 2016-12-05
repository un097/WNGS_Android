package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreZdmxBean;

import java.util.List;

/**
 * Created by WR on 2014/11/27.
 */
public class MoreZdmxListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreZdmxBean> mList;

    public MoreZdmxListAdapter(Context context,List<MoreZdmxBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_zd, null);
            holder = new ViewHolder();
            //布局
            holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_desc.setText(mList.get(position).getDesc());
        holder.tv_time.setText(mList.get(position).getTrandate());
        if (mList.get(position).getProcesstype().equals("0")){
            holder.tv_type.setText("处理中");
        }else if (mList.get(position).getProcesstype().equals("1")){
            holder.tv_type.setText("交易成功");
        }else if (mList.get(position).getProcesstype().equals("2")){
            holder.tv_type.setText("交易失败");
        }
        if (mList.get(position).getTranstype().equals("C")) {
            holder.tv_price.setText("+ " + mList.get(position).getAmount());
        }else {
            holder.tv_price.setText("- " + mList.get(position).getAmount());

        }
        return convertView;
    }

    private class ViewHolder {

        TextView tv_desc,tv_price,tv_time,tv_type;
    }
}
