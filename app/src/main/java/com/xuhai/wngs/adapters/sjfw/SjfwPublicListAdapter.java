package com.xuhai.wngs.adapters.sjfw;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.sjfw.SjfwPublicListBean;
import com.xuhai.wngs.beans.wyfw.WyfwBMFWBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/26.
 */
public class SjfwPublicListAdapter extends BaseAdapter {
    private Context mContext;
    private List<SjfwPublicListBean> mList;

    public SjfwPublicListAdapter(Context context, List<SjfwPublicListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_sjfw_all, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.image = (CircleImageView) convertView.findViewById(R.id.image);
            holder.rbar=(RatingBar)convertView.findViewById(R.id.rbar);
            holder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
            holder.iv_ding = (ImageView) convertView.findViewById(R.id.iv_ding);
            holder.iv_tuan = (ImageView) convertView.findViewById(R.id.iv_tuan);
            holder.iv_hui = (ImageView) convertView.findViewById(R.id.iv_hui);
            holder.iv_wai = (ImageView) convertView.findViewById(R.id.iv_wai);
            holder.iv_olpay = (ImageView) convertView.findViewById(R.id.iv_olpay);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //holder.rbar.setRating(Float.parseFloat(mList.get(position).getStar()));
        if (mList.get(position).getStar() == null || mList.get(position).getStar().equals("")){
            holder.rbar.setRating(0);
        }else {
            holder.rbar.setRating(Float.parseFloat(mList.get(position).getStar()));
        }
        if (mList.get(position).getImg() == null || mList.get(position).getImg().equals("")) {
            holder.image.setImageResource(R.drawable.ic_huisewoniu);
        } else {
            PicassoTrustAll.getInstance(mContext).load(mList.get(position).getImg()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.image);
        }

        if (mList.get(position).getDing().equals("0")){
            holder.iv_ding.setVisibility(View.GONE);
        }else {
            holder.iv_ding.setVisibility(View.VISIBLE);
        }
        if (mList.get(position).getTuan().equals("0")){
            holder.iv_tuan.setVisibility(View.GONE);
        }else {
            holder.iv_tuan.setVisibility(View.VISIBLE);
        }
        if (mList.get(position).getWai().equals("0")){
            holder.iv_wai.setVisibility(View.GONE);
        }else {
            holder.iv_wai.setVisibility(View.VISIBLE);
        }
        if (mList.get(position).getHui().equals("0")){
            holder.iv_hui.setVisibility(View.GONE);
        }else {
            holder.iv_hui.setVisibility(View.VISIBLE);
        }

        if (mList.get(position).getIssell().equals("1")){
            holder.iv_olpay.setVisibility(View.VISIBLE);
        }else {
            holder.iv_olpay.setVisibility(View.GONE);
        }
        holder.title.setText(mList.get(position).getTitle());
        holder.tv_tel.setText("电话： " + mList.get(position).getTel());
        holder.info.setText("地址： " + mList.get(position).getAddr());

        return convertView;
    }

    private class ViewHolder {
        CircleImageView image;
        TextView info;
        TextView title;
        RatingBar rbar;
        ImageView iv_ding;
        ImageView iv_tuan;
        ImageView iv_wai;
        ImageView iv_hui;
        TextView tv_tel;
        ImageView iv_olpay;
    }
}
