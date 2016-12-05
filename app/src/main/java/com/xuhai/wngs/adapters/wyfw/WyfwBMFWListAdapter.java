package com.xuhai.wngs.adapters.wyfw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.wyfw.WyfwBMFWBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by changliang on 14/11/14.
 */
public class WyfwBMFWListAdapter extends BaseAdapter {

    private Context mContext;
    private List<WyfwBMFWBean> mList;

    public WyfwBMFWListAdapter(Context context, List<WyfwBMFWBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_wyfw_bmfw, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.info = (TextView) convertView.findViewById(R.id.info);
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

        holder.title.setText(mList.get(position).getTitle());
        holder.info.setText(mList.get(position).getInfo());

        return convertView;
    }

    private class ViewHolder {
        CircleImageView image;
        TextView info;
        TextView title;
    }
}
