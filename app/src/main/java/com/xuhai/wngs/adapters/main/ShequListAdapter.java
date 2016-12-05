package com.xuhai.wngs.adapters.main;

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
import com.xuhai.wngs.beans.main.ShequListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by changliang on 14/11/5.
 */
public class ShequListAdapter extends BaseAdapter {

    private int mLastPosition = -1;

    private Context mContext;
    private List<ShequListBean> mList;

    public ShequListAdapter (Context context, List<ShequListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_shequ, null);
            holder = new ViewHolder();
            holder.image = (CircleImageView) convertView.findViewById(R.id.image);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        // animate the item
//        TranslateAnimation animation = null;
//        if (position > mLastPosition) {
//            animation = new TranslateAnimation(
//                    Animation.RELATIVE_TO_SELF,
//                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                    Animation.RELATIVE_TO_SELF, 1.0f,
//                    Animation.RELATIVE_TO_SELF, 0.0f);
//
//            animation.setDuration(600);
//            convertView.startAnimation(animation);
//            mLastPosition = position;
//        }

        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.image);
        }
        holder.title.setText(mList.get(position).getShequ());
        holder.description.setText(mList.get(position).getAddr());

        if (mList.get(position).getDistance() == null || mList.get(position).getDistance().equals("")) {
            holder.distance.setText("");
        } else {
            if (mList.get(position).getDistance().length()!=0 ) {
                if (mList.get(position).getDistance().length() <= 3) {
                    holder.distance.setText(mList.get(position).getDistance() + "m");
                } else {
                    if (mList.get(position).getDistance()!=null){
                        DecimalFormat df = new DecimalFormat("0.0");
                        double d = Double.valueOf(mList.get(position).getDistance()) / 1000;
                        String db = df.format(d);
                        holder.distance.setText(db + "km");}
                }
            } else {

            }
        }

        return convertView;
    }

    private class ViewHolder {
        CircleImageView image;
        TextView distance;
        TextView title;
        TextView description;
    }
}
