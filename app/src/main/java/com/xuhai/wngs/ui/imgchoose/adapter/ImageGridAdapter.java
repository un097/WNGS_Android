package com.xuhai.wngs.ui.imgchoose.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuhai.wngs.R;
import com.xuhai.wngs.ui.imgchoose.BitmapBucket;
import com.xuhai.wngs.ui.imgchoose.BitmapCache;
import com.xuhai.wngs.ui.imgchoose.ImageChooseActivity;
import com.xuhai.wngs.ui.imgchoose.ImageItem;

public class ImageGridAdapter extends BaseAdapter {
	private TextCallBack textCasllBack = null;
	private final String TAG = getClass().getSimpleName();
	private List<ImageItem> dataList;
	private Activity act;
	public Map<String, String> map = new HashMap<String, String>();
	private BitmapCache cache;
	private Handler mHandler;
	private int selectedTotal = 0;
	public BitmapCache.ImageCallBack callBack = new BitmapCache.ImageCallBack() {

		@Override
		public void doImageLoad(ImageView iv, Bitmap bitmap, Object... params) {
			if (iv != null && bitmap != null) {
				String url = params[0].toString();
				if (url != null && iv.getTag() != null
						&& url.equals(iv.getTag().toString())) {
					iv.setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "图片不相同...");
				}
			} else {
				Log.e(TAG, "你写了些什么！？bitmap怎么是null");
			}

		}
	};

	public void setTextCallBack(TextCallBack textCallBack) {
		this.textCasllBack = textCallBack;
	}

	public ImageGridAdapter(Activity act, List<ImageItem> dataList,
			Handler mHandler) {
		this.act = act;
		this.dataList = dataList;
		this.mHandler = mHandler;
		this.cache = new BitmapCache();
	}

	@Override
	public int getCount() {
		if (dataList != null) {
			return dataList.size() + 1;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
        if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_grid_img_cell, null);
			holder.iv = (ImageView) convertView
					.findViewById(R.id.img_grid_item);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.check_img);
			holder.text = (TextView) convertView
					.findViewById(R.id.img_grid_text);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

        if (position == 0) {
            holder.iv.setImageResource(R.drawable.ic_take_photo);
            holder.selected.setVisibility(View.GONE);
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ImageChooseActivity) act).takePhoto();
                }
            });
        } else {
            holder.selected.setVisibility(View.VISIBLE);
            final ImageItem item = dataList.get(position - 1);
            holder.iv.setTag(item.imagePath);
            cache.dispBitmap(holder.iv, item.imagePath, item.thumbnailPath,
                    callBack);
            if (item.isSelected) {
                holder.selected.setImageResource(R.drawable.ic_img_check_on);
                holder.text.setBackgroundColor(0xb2293334);
            } else {
                holder.selected.setImageResource(R.drawable.ic_img_check_off);
                holder.text.setBackgroundColor(0x00000000);
            }
            holder.iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String path = dataList.get(position - 1).imagePath;
                    if (selectedTotal + BitmapBucket.bitlist.size() < BitmapBucket.max) {
                        item.isSelected = !item.isSelected;
                        if (item.isSelected) {
                            holder.selected.setImageResource(R.drawable.ic_img_check_on);
                            holder.text.setBackgroundColor(0xb2293334);
                            selectedTotal += 1;
                            if (textCasllBack != null) {
                                textCasllBack.onListen(selectedTotal
                                        + BitmapBucket.bitlist.size());
                            }
                            map.put(path, path);
                        } else if (!item.isSelected) {
                            holder.selected.setImageResource(R.drawable.ic_img_check_off);
                            holder.text.setBackgroundColor(0x00000000);
                            selectedTotal -= 1;
                            if (textCasllBack != null) {
                                textCasllBack.onListen(selectedTotal
                                        + BitmapBucket.bitlist.size());
                            }
                            map.remove(path);
                        }
                    } else if (selectedTotal + BitmapBucket.bitlist.size() >= BitmapBucket.max) {
                        if (item.isSelected) {
                            item.isSelected = !item.isSelected;
                            holder.selected.setImageResource(R.drawable.ic_img_check_off);
                            holder.text.setBackgroundColor(0x00000000);
                            selectedTotal--;
                            if (textCasllBack != null) {
                                textCasllBack.onListen(selectedTotal
                                        + BitmapBucket.bitlist.size());
                            }
                            map.remove(path);
                        } else {
                            mHandler.sendEmptyMessage(0);
                        }
                    }
                }
            });
        }
		return convertView;
	}

    class Holder {
		private ImageView iv;
		private ImageView selected;
		private TextView text;
	}

	public static interface TextCallBack {
		public void onListen(int count);
	}
}
