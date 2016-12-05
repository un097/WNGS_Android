package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreWDDDListBean;
import com.xuhai.wngs.beans.more.MoreWdddItemBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/12/5.
 */
public class MoreWDDDItemListAdapter extends BaseAdapter{
    private Context mContext;
    private List<MoreWdddItemBean> mList;

    public MoreWDDDItemListAdapter(Context context, List<MoreWdddItemBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_wddd_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.count = (TextView) convertView.findViewById(R.id.geshu);
            holder.price = (TextView) convertView.findViewById(R.id.jiage);
            holder.image=(CircleImageView)convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.image);
        }
        holder.title.setText(mList.get(position).getGoods());
        holder.count.setText("X "+mList.get(position).getGoods_count());
        holder.price.setText(mContext.getResources().getString(R.string.yang) + " " + mList.get(position).getGoods_price());
        return convertView;
    }

    private class ViewHolder {
        CircleImageView image;
        TextView title;
        TextView count;
        TextView price;
    }
}

