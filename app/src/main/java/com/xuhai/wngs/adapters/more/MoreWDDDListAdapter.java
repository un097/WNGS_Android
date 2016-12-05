package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreJFDDListBean;
import com.xuhai.wngs.beans.more.MoreWDDDListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/27.
 */
public class MoreWDDDListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreWDDDListBean> mList;

    public MoreWDDDListAdapter(Context context, List<MoreWDDDListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_wddd, null);
            holder = new ViewHolder();
            holder.storename = (TextView) convertView.findViewById(R.id.storename);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.flag = (TextView) convertView.findViewById(R.id.flag);
            holder.image = (CircleImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.image);
        }
        holder.storename.setText(mList.get(position).getStorename());
        holder.price.setText(mContext.getResources().getString(R.string.yang) + " " + mList.get(position).getPrice());
        holder.flag.setTag(position);
        if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("1"))){
            holder.flag.setText("已接收");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("2"))){
            holder.flag.setText("配送中");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("3"))){
            holder.flag.setText("已完成");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("5"))){
            holder.flag.setText("已评论");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("6"))){
            holder.flag.setText("未付款");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("7"))){
            holder.flag.setText("支付确认中");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("8"))){
            holder.flag.setText("已支付");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("9"))){
            holder.flag.setText("退款中");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("10"))){
            holder.flag.setText("已退款");
        }else if ((mList.get((Integer) holder.flag.getTag()).getFlag().equals("11"))){
            holder.flag.setText("已取消");
        }else {
            holder.flag.setText("");
        }

        return convertView;
    }

    private class ViewHolder {
        CircleImageView image;
        TextView storename;
        TextView price;
        TextView flag;
    }
}
