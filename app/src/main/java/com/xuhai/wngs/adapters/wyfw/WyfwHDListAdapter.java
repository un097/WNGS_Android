package com.xuhai.wngs.adapters.wyfw;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.wyfw.WyfwHDBean;
import com.xuhai.wngs.beans.wyfw.WyfwZXGGBean;
import com.xuhai.wngs.ui.wyfw.WyfwHDActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

/**
 * Created by changliang on 14/11/14.
 */
public class WyfwHDListAdapter extends BaseAdapter {

    private Context mContext;
    private List<WyfwHDBean> mList;

    public WyfwHDListAdapter(Context context, List<WyfwHDBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_wyfw_hd, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {

        } else {
            int[] img_coordinates = new int[2];
//            holder.image.getLocationOnScreen(img_coordinates);
//            Log.d("image", "x === " + img_coordinates[0]);
//            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_huisewoniu).resize(640, 320).centerCrop().into(holder.image);
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_huisewoniu).resize(((WyfwHDActivity) mContext).screenWidth, ((WyfwHDActivity) mContext).screenWidth * 320 / 640).centerCrop().into(holder.image);
        }

        holder.title.setText(mList.get(position).getTitle());
        holder.time.setText(mList.get(position).getDate());
        holder.text.setTag(position);

        if (mList.get(position).getExpired() != null) {
            if (mList.get(position).getExpired().equals("1")) {
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setBackgroundColor(R.color.black);
                holder.text.setAlpha(127);
                holder.text.setText("活动已经过期了");
            } else {
                holder.text.setVisibility(View.INVISIBLE);
            }
        }



        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView time;
        TextView title,text;
    }
}
