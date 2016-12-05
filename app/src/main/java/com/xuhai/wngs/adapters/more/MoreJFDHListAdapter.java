package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreJFDDListBean;
import com.xuhai.wngs.beans.more.MoreJFDHListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/27.
 */
public class MoreJFDHListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreJFDHListBean> mList;

    public MoreJFDHListAdapter(Context context, List<MoreJFDHListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_jfdh, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.info = (TextView) convertView.findViewById(R.id.tv_info);
            holder.points = (TextView) convertView.findViewById(R.id.tv_points);
            holder.image = (CircleImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.title.setText(mList.get(position).getTitle());
        holder.info.setText(mList.get(position).getInfo());
        holder.points.setText(mList.get(position).getPoints()+"积分");
        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_launcher);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(holder.image);
        }


        return convertView;
    }

    private class ViewHolder {
        CircleImageView image;
        TextView title;
        TextView info;
        TextView points;
    }
}
