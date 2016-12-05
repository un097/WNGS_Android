package com.xuhai.wngs.ui.imgchoose;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xuhai.wngs.R;

public class PhotoBucketWindow extends PopupWindow implements
		OnItemClickListener {
	private final String TAG = getClass().getSimpleName();
	private Context context;
	private View view;
	private View anchor;
	private ListView listView;
	private List<ImageBucket> bucketList;
	private BitmapCache bitmapCache;
	private OnBucketSelectedListener selectedListener;
	private ImageBucket selectedBucket;

	public void setSelectedListener(OnBucketSelectedListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	Handler mHandler;
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

	public PhotoBucketWindow(Context context, View anchor,
			List<ImageBucket> bucketList, Handler mHandler, int width,
			int height) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.pop_list_img_bucket, null);
		listView = (ListView) view.findViewById(R.id.listView1);
		setContentView(view);
		setWidth(width);
		setHeight(height);
		setFocusable(true);
		setOutsideTouchable(true);
		setAnimationStyle(R.style.popup_win_ani);
		// setBackgroundDrawable(R.drawable.b)
		this.anchor = anchor;
		this.bucketList = bucketList;
		this.context = context;
		this.mHandler = mHandler;
		bitmapCache = new BitmapCache();
		ImageBucketAdapter adapter = new ImageBucketAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		selectedBucket = bucketList.get(0);
	}

	public void show() {
		showAsDropDown(anchor);

	}

	class ImageBucketAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (bucketList != null)
				return bucketList.size();
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
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageBucket bucket = bucketList.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_img_bucket, null);
			}

			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);
			ImageView imageView2 = (ImageView) convertView
					.findViewById(R.id.imageView2);
			if (bucket.isSelected)
				imageView2.setImageResource(R.drawable.img_bucket_duigou);
			else
				imageView2.setImageBitmap(null);
			TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
			TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
			tv1.setText(bucket.bucketName);
			tv2.setText(bucket.count + "张");
			if (bucket.imageList.size() > 0) {
                if ("我的图片".equals(bucket.bucketName))
                    bitmapCache
                            .dispBitmap(
                                    imageView,
                                    bucket.imageList.get(bucket.imageList.size() - 1).imagePath,
                                    bucket.imageList.get(bucket.imageList.size() - 1).thumbnailPath,
                                    callBack);
                else
                    bitmapCache.dispBitmap(imageView,
                            bucket.imageList.get(0).imagePath,
                            bucket.imageList.get(0).thumbnailPath, callBack);
            }
			return convertView;
		}

	}

	public interface OnBucketSelectedListener {
		public void onBucketSelected(List<ImageItem> imageList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ImageBucket curBucket = bucketList.get(position);
		ImageView imageView2 = (ImageView) view.findViewById(R.id.imageView2);
		List<ImageItem> imageList = curBucket.imageList;
		if (selectedListener != null) {
			selectedListener.onBucketSelected(imageList);
		}
		imageView2.setImageResource(R.drawable.img_bucket_duigou);
		curBucket.isSelected = true;
		if (curBucket != selectedBucket)
			selectedBucket.isSelected = false;
		selectedBucket = curBucket;
		dismiss();
	}
}
