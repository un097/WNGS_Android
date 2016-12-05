package com.xuhai.wngs.ui.shzl;

import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.HackyViewPager;

import java.util.List;

public class ShzlPublicTPFDActivity extends BaseActionBarAsUpActivity {

    public final String TAG = "ShzlPublicTPFDActivity";

    private List<String> imgList;

    private static final String STATE_POSITION = "STATE_POSITION";
    int pagerPosition;
    ViewPager pager;
    ImagePagerAdapter pagerAdapter;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pager = new HackyViewPager(this);

        imgList = getIntent().getStringArrayListExtra("imglist");
        size = getIntent().getIntExtra("size", 0);
//        img1 = getIntent().getStringExtra("img1");
//        img2 = getIntent().getStringExtra("img2");
//        img3 = getIntent().getStringExtra("img3");
//        size = getIntent().getStringExtra("size");
//        Bundle bundle = getIntent().getExtras();
//        if (img1 == null || img1.equals("")) {
//            imageUrls = new String[]{};
//        } else if (img2 == null || img2.equals("")) {
//             imageUrls = new String[]{img1};
//        } else if (img3 == null || img3.equals("")) {
//            imageUrls = new String[]{img1,img2};
//        } else if (!img1.equals("") && !img2.equals("") && !img3.equals("")) {
//            imageUrls = new String[]{img1,img2,img3};
//        }
//        if (size.equals("a")) {
//            pagerPosition = 0;
//        } else if (size.equals("b")) {
//            pagerPosition = 1;
//        } else if (size.equals("c")) {
//            pagerPosition = 2;
//        }
        pagerPosition = size;
//        if (savedInstanceState != null) {
//            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
//        }

        pagerAdapter = new ImagePagerAdapter(imgList);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(pagerPosition);

        setContentView(pager);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putInt(STATE_POSITION, pager.getCurrentItem());
//    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<String> imageList;
        private LayoutInflater inflater;

        ImagePagerAdapter(List<String> images) {
            imageList = images;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            PicassoTrustAll.getInstance(ShzlPublicTPFDActivity.this).load(imageList.get(position)).into(imageView);
//            imageLoader.displayImage(imageList.get(position), imageView, options, new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                    String message = null;
//                    switch (failReason.getType()) {
//                        case IO_ERROR:
//                            message = "Input/Output error";
//                            break;
//                        case DECODING_ERROR:
//                            message = "Image can't be decoded";
//                            break;
//                        case NETWORK_DENIED:
//                            message = "Downloads are denied";
//                            break;
//                        case OUT_OF_MEMORY:
//                            message = "Out Of Memory error";
//                            break;
//                        case UNKNOWN:
//                            message = "Unknown error";
//                            break;
//                    }
//                    CustomToast.showToast(ShzlPublicTPFDActivity.this, message, 1000);
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//
//
//                }
//
//            });

                    ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }


}


