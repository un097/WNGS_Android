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
import com.xuhai.wngs.beans.wyfw.WyfwBMFWBean;
import com.xuhai.wngs.beans.wyfw.WyfwZXGGBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by changliang on 14/11/14.
 */
public class WyfwZXGGListAdapter extends BaseAdapter {

    private Context mContext;
    private List<WyfwZXGGBean> mList;

    public WyfwZXGGListAdapter(Context context, List<WyfwZXGGBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_wyfw_zxgg, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.image = (ImageView) convertView.findViewById(R.id.red);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
//            holder.image.setImageResource(R.drawable.ic_wyfw_zxgg_message);
//        } else {
//            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_wyfw_zxgg_message).error(R.drawable.ic_wyfw_zxgg_message).into(holder.image);
//        }

        holder.title.setText(mList.get(position).getTitle());
        holder.time.setText(mList.get(position).getTime());
        if (mList.get(position).getRead() != null) {

            if (mList.get(position).getRead().equals("0")) {
                holder.image.setVisibility(View.VISIBLE);
            } else if (mList.get(position).getRead().equals("1")) {
                holder.image.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private class ViewHolder {

        TextView time;
        TextView title;
        ImageView image;
    }
}
