package com.xuhai.wngs.ui.imgchoose;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class BitmapCache {
	public Handler h = new Handler();
	private static final String TAG = BitmapCache.class.getSimpleName();

	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	public BitmapCache() {

	}

	public void put(String path, Bitmap bitmap) {
		if (!TextUtils.isEmpty(path)) {
			imageCache.put(path, new SoftReference<Bitmap>(bitmap));
		}
	}

	public void dispBitmap(final ImageView iv, final String sourcePath,
			final String thumbPath, final ImageCallBack callBack) {
		if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)) {
			Log.e(TAG, "没有图片路径。。");
			return;
		}
		final String path;
		final boolean isThumbPath;
		if (!TextUtils.isEmpty(thumbPath)) {
			path = thumbPath;
			isThumbPath = true;
		} else if (!TextUtils.isEmpty(sourcePath)) {
			path = sourcePath;
			isThumbPath = false;
		} else {
			return;
		}
		if (imageCache.containsKey(path)) {
			SoftReference<Bitmap> cache = imageCache.get(path);
			Bitmap b = cache.get();
			if (b != null) {
				if (callBack != null) {
					callBack.doImageLoad(iv, b, sourcePath);
				}
				iv.setImageBitmap(b);
				return;
			}
		}
		iv.setImageBitmap(null);
		new Thread() {
			Bitmap thumb;

			public void run() {

				try {
					if (isThumbPath) {
						thumb = BitmapFactory.decodeFile(thumbPath);
						if (thumb == null) {
							thumb = revitionImageSize(sourcePath);
						}
					} else {
						thumb = revitionImageSize(sourcePath);
					}

				} catch (Exception e) {
					Log.e(TAG, e.getLocalizedMessage());
				}
				if (thumb == null) {
					thumb = ImageChooseActivity.def;
				}
//				Log.e(TAG, "thumb:" + thumb);
				put(path, thumb);
				if (callBack != null) {
					h.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							callBack.doImageLoad(iv, thumb, sourcePath);
						}
					});
				}
			}
		}.start();

	}

	public Bitmap revitionImageSize(String sourcePath) throws IOException {
		if (TextUtils.isEmpty(sourcePath)) {
			return null;
		}
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(sourcePath)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
//			Log.i(TAG, "width:" + (options.outWidth >> i) + " height:"
//					+ (options.outHeight >> i) + " i:" + i);
			if ((options.outWidth >> i <= 256)
					&& (options.outHeight >> i <= 256)) {
				in = new BufferedInputStream(new FileInputStream(new File(
						sourcePath)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				in.close();
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	public interface ImageCallBack {
		public void doImageLoad(ImageView iv, Bitmap bitmap, Object... params);
	}

}
