package com.xuhai.wngs.adapters.sjfw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.sjfw.SjfwSplistBean;
import com.xuhai.wngs.ui.sjfw.SjfwStoreActivity;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

/**
 * Created by WR on 2014/11/26.
 */
public class SjfwStoreGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<SjfwSplistBean> mlist;

    public SjfwStoreGridAdapter(Context context,List<SjfwSplistBean>  list) {
        this.mContext = context;
        this.mlist = list;
    }

    @Override
    public int getCount() {
        return mlist.size();
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_sjfw_store, null);
            holder = new ViewHolder();
            holder.tv_goods = (TextView) convertView.findViewById(R.id.tv_goods);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
//            holder.iv_bag = (ImageView) convertView.findViewById(R.id.iv_bag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_goods.setText(mlist.get(position).getGoods());
        holder.tv_price.setText("¥ " + mlist.get(position).getPrice());


        if (mlist.get(position).getGoodsimg() != null & !mlist.get(position).getGoodsimg().equals("")) {
            PicassoTrustAll.getInstance(mContext)
                    .load(mlist.get(position).getGoodsimg())
                    .resize(((SjfwStoreActivity) mContext).screenWidth / 2, ((SjfwStoreActivity) mContext).screenWidth / 2)
                    .centerCrop()
                    .into(holder.iv_img);
        }

        return convertView;
    }


    private class ViewHolder {
        TextView tv_goods,tv_price;
        ImageView iv_img,iv_bag;

    }
}
