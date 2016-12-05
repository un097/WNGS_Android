package com.xuhai.wngs.adapters.shzl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.shzl.ShzlBBSListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

/**
 * Created by changliang on 14/12/23.
 */
public class ShzlBBSRecyclerAdapter extends RecyclerView.Adapter<ShzlBBSRecyclerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout_image;
        TextView title;
        ImageView image1;
        ImageView image2;
        ImageView image3;
        TextView nickname;
        TextView time;
        TextView comments_count;

        public ViewHolder(View view) {
            super(view);
        }
    }

    private Context mContext;
    private List<ShzlBBSListBean> mList;

    public ShzlBBSRecyclerAdapter(Context context, List<ShzlBBSListBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_shzl_bbs, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.layout_image = (LinearLayout) view.findViewById(R.id.layout_image);
        holder.title = (TextView) view.findViewById(R.id.title);
        holder.image1 = (ImageView) view.findViewById(R.id.image1);
        holder.image2 = (ImageView) view.findViewById(R.id.image2);
        holder.image3 = (ImageView) view.findViewById(R.id.image3);
        holder.nickname = (TextView) view.findViewById(R.id.nikename);
        holder.time = (TextView) view.findViewById(R.id.time);
        holder.comments_count = (TextView) view.findViewById(R.id.comments_count);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mList.get(position).getTitle());
        if (mList.get(position).getNickname() == null || mList.get(position).getNickname().equals("")) {
            holder.nickname.setText("火星网友");
        } else {
            holder.nickname.setText(mList.get(position).getNickname());
        }
        holder.time.setText(mList.get(position).getTime());
        holder.comments_count.setText(mList.get(position).getComments_count() + "回复");

        if (mList.get(position).getImg1().equals("")) {
            holder.layout_image.setVisibility(View.GONE);
        } else {
            holder.layout_image.setVisibility(View.VISIBLE);

            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg1()).resize(mContext.getResources().getDimensionPixelSize(R.dimen.image_width), mContext.getResources().getDimensionPixelSize(R.dimen.image_height)).centerCrop().into(holder.image1);
            if (!mList.get(position).getImg2().equals("")) {
                PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg2()).resize(mContext.getResources().getDimensionPixelSize(R.dimen.image_width), mContext.getResources().getDimensionPixelSize(R.dimen.image_height)).centerCrop().into(holder.image2);
            } else {
                holder.image2.setVisibility(View.GONE);
            }
            if (!mList.get(position).getImg3().equals("")) {
                PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg3()).resize(mContext.getResources().getDimensionPixelSize(R.dimen.image_width), mContext.getResources().getDimensionPixelSize(R.dimen.image_height)).centerCrop().into(holder.image3);
            } else {
                holder.image3.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
