package com.xuhai.wngs.adapters.shzl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.shzl.ShzlBBSContentListBean;
import com.xuhai.wngs.beans.shzl.ShzlBBSListBean;
import com.xuhai.wngs.ui.shzl.ShzlBBSContentActivity;
import com.xuhai.wngs.ui.shzl.ShzlBBSHFActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/28.
 */
public class ShzlBBSContentListAdapter extends BaseAdapter {
    private int i;
    private Context mContext;
    private List<ShzlBBSContentListBean> mList;

    public ShzlBBSContentListAdapter(Context context, List<ShzlBBSContentListBean> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_shzl_bbs_content, null);
            holder = new ViewHolder();

            holder.head = (CircleImageView) convertView.findViewById(R.id.reply_head);
            holder.date = (TextView) convertView.findViewById(R.id.reply_date);
            holder.info = (TextView) convertView.findViewById(R.id.reply_info);
            holder.id = (TextView) convertView.findViewById(R.id.reply_id);
            holder.nickname = (TextView) convertView.findViewById(R.id.reply_nickname);
            holder.reply = (LinearLayout) convertView.findViewById(R.id.reply_reply);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mList.get(position).getReply_other_id().equals("") || mList.get(position).getReply_other_id() == null){
        if (mList.get(position).getReply_nickname().equals("") || mList.get(position).getReply_nickname() == null) {
            holder.nickname.setText("火星网友");
        } else {
            holder.nickname.setText(mList.get(position).getReply_nickname());
        }}else {
            if (mList.get(position).getReply_nickname().equals("") || mList.get(position).getReply_nickname() == null) {
                holder.nickname.setText("火星网友  回复  "+mList.get(position).getReply_other());
            } else {
                holder.nickname.setText(mList.get(position).getReply_nickname()+"  回复  "+mList.get(position).getReply_other());
            }
        }


        holder.date.setText(mList.get(position).getReply_time());
        holder.info.setText(mList.get(position).getReply_content().trim());
        holder.id.setText(position + 1 + "楼");

        if (mList.get(position).getReply_head() == null || mList.get(position).getReply_head().equals("")) {
            holder.head.setImageResource(R.drawable.head_qiuzhenxiang_public);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getReply_head()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.head);
        }
        return convertView;
    }

    private class ViewHolder {
        CircleImageView head;
        TextView nickname;
        TextView info;
        TextView date;
        TextView id;
        LinearLayout reply;
    }
}

