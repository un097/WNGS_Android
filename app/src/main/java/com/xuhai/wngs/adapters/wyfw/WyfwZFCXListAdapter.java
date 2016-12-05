package com.xuhai.wngs.adapters.wyfw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.wyfw.WyfwZFCXListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/27.
 */
public class WyfwZFCXListAdapter extends BaseAdapter {
    private Context mContext;
    private List<WyfwZFCXListBean> mList;

    public WyfwZFCXListAdapter(Context context, List<WyfwZFCXListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_wyfw_zfcx, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.balance = (TextView) convertView.findViewById(R.id.balance);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.image);
        }

        holder.title.setText(mList.get(position).getTitle());
        holder.info.setText("收费标准："+mList.get(position).getInfo());
        holder.date.setText(mList.get(position).getDate());
        holder.balance.setText(mContext.getResources().getString(R.string.yang)+mList.get(position).getBalance());
        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView info;
        TextView title;
        TextView balance;
        TextView date;
    }
}


