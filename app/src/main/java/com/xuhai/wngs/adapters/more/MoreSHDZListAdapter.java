package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreSHDZListBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/27.
 */
public class MoreSHDZListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreSHDZListBean> mList;

    public MoreSHDZListAdapter(Context context, List<MoreSHDZListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_shdz, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.tel = (TextView) convertView.findViewById(R.id.tel);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
//            holder.image.setImageResource(R.drawable.ic_launcher);
//        } else {
//            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(holder.image);
//        }
        if (mList.get(position).getIsdefault().equals("1")){
            holder.image.setImageResource(R.drawable.ic_more_shdz_img);
        }
        holder.name.setText(mList.get(position).getName());
        holder.tel.setText(mList.get(position).getTel());
        holder.address.setText(mList.get(position).getAddress());

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView tel;
        TextView address;
    }
}
