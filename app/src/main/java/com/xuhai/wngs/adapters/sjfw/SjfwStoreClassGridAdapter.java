package com.xuhai.wngs.adapters.sjfw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.sjfw.SjfwClassBean;
import com.xuhai.wngs.beans.sjfw.SjfwPublicListBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/26.
 */
public class SjfwStoreClassGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<SjfwClassBean> mList;

    private int choose = -1;
    public SjfwStoreClassGridAdapter(Context context,List<SjfwClassBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_store_class, null);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.view_choose = (View) convertView.findViewById(R.id.view_choose);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(mList.get(position).getClass1());

        if (position == choose){
            holder.view_choose.setVisibility(View.VISIBLE);
        }else {
            holder.view_choose.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void refreshChoo(int posi){
        this.choose = posi;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView tv_name;
        View view_choose;
    }
}
