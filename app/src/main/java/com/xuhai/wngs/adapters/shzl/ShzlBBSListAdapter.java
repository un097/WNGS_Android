package com.xuhai.wngs.adapters.shzl;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.shzl.ShzlBBSListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

/**
 * Created by changliang on 14/11/19.
 */
public class ShzlBBSListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ShzlBBSListBean> mList;

    public ShzlBBSListAdapter(Context context, List<ShzlBBSListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_shzl_bbs, null);
            holder = new ViewHolder();

            holder.layout_image = (LinearLayout) convertView.findViewById(R.id.layout_image);
            holder.image1 = (ImageView) convertView.findViewById(R.id.image1);
            holder.image2 = (ImageView) convertView.findViewById(R.id.image2);
            holder.image3 = (ImageView) convertView.findViewById(R.id.image3);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.nickname = (TextView) convertView.findViewById(R.id.nikename);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.count = (TextView) convertView.findViewById(R.id.comments_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(mList.get(position).getTitle());
        if (mList.get(position).getNickname()==null||mList.get(position).getNickname().equals("")){
        holder.nickname.setText("火星网友");}
        else {
            holder.nickname.setText(mList.get(position).getNickname());
        }
        holder.time.setText(mList.get(position).getTime());
        holder.count.setText(mList.get(position).getComments_count() + "回复");

        if (mList.get(position).getImg1().equals("")) {
            holder.layout_image.setVisibility(View.GONE);
        } else {
            holder.layout_image.setVisibility(View.VISIBLE);

            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg1()).resize(mContext.getResources().getDimensionPixelSize(R.dimen.image_width), mContext.getResources().getDimensionPixelSize(R.dimen.image_height)).centerCrop().into(holder.image1);
            if (!mList.get(position).getImg2().equals("")) {
                holder.image2.setVisibility(View.VISIBLE);
                PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg2()).resize(mContext.getResources().getDimensionPixelSize(R.dimen.image_width), mContext.getResources().getDimensionPixelSize(R.dimen.image_height)).centerCrop().into(holder.image2);
            } else {
                holder.image2.setVisibility(View.GONE);
            }
            if (!mList.get(position).getImg3().equals("")) {
                holder.image3.setVisibility(View.VISIBLE);
                PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg3()).resize(mContext.getResources().getDimensionPixelSize(R.dimen.image_width), mContext.getResources().getDimensionPixelSize(R.dimen.image_height)).centerCrop().into(holder.image3);
            } else {
                holder.image3.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        LinearLayout layout_image;
        TextView title;
        TextView nickname;
        TextView time;
        TextView count;
        ImageView image1;
        ImageView image2;
        ImageView image3;
    }
}
