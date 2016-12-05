package com.xuhai.wngs.adapters.shzl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.shzl.ShzlBldListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.ArrayList;

/**
 * Created by WR on 2014/12/2.
 */
public class BLDlistAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ShzlBldListBean> mlist;

    public BLDlistAdapter(Context context, ArrayList<ShzlBldListBean> blddlList) {
        this.context = context;
        mlist = blddlList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gv_wyfw_bld_list, null);
            holder = new ViewHolder();

            holder.image = (ImageView) convertView.findViewById(R.id.img);
            holder.text = (TextView) convertView.findViewById(R.id.text);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(mlist.get(position).getClasss());
        if (mlist.get(position).getClassimg() == null || mlist.get(position).getClassimg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(context)
                    .load(mlist.get(position).getClassimg())
                    .into(holder.image);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView text;

    }
}

