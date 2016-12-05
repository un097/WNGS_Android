package com.xuhai.wngs.adapters.more;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.beans.more.MoreBankListBean;
import com.xuhai.wngs.beans.more.MoreJFDDListBean;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by WR on 2014/11/27.
 */
public class MoreCardListAdapter extends BaseAdapter {
    private Context mContext;
    private List<MoreBankListBean> mList;

    public MoreCardListAdapter(Context context,List<MoreBankListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_more_card, null);
            holder = new ViewHolder();
            //布局
            holder.img_card = (CircleImageView) convertView.findViewById(R.id.img_card);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        PicassoTrustAll.getInstance(mContext).load(mList.get(position).getBankphoto()).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).into(holder.img_card);
        holder.tv_name.setText(mList.get(position).getBankname());

        String showno = mList.get(position).getCardno().substring(mList.get(position).getCardno().length() - 4);
        holder.tv_number.setText("***************" + showno);



        return convertView;
    }

    private class ViewHolder {

        CircleImageView img_card;
        TextView tv_name;
        TextView tv_number;
    }
}
