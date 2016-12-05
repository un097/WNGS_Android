package com.xuhai.wngs.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.PicassoTrustAll;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2014/12/4.
 */
public class MyViewpager {
    private ImageView[] imageViews;
    private ImageView imageView;
    private ViewPager vp_img;
    private ViewGroup vg_img;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
    private Context context;
    private List imgs;


    public MyViewpager(Context mcontext, List list , ViewPager viewPager,ViewGroup viewGroup) {
        this.context = mcontext;
        this.imgs = list;
        this.vp_img = viewPager;
        this.vg_img = viewGroup;
    }

    public void setBanner() {
            imageViews = new ImageView[imgs.size()];
//小图标
        for (int i = 0; i < imgs.size(); i++) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            //imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);  // , 1是可选写的
            lp.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(lp);
            imageView.setPadding(5, 5, 5, 5);
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i]
                        .setBackgroundResource(R.drawable.round_on);

            } else {
                imageViews[i]
                        .setBackgroundResource(R.drawable.round_off);
            }
            vg_img.addView(imageViews[i]);
        }
        AdvAdapter adapter = new AdvAdapter();//声明适配器

        vp_img.setAdapter(adapter);
        vp_img.setOnPageChangeListener(new GuidePageChangeListener());
        vp_img.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        viewHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }

        }).start();
    }

    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > imageViews.length - 1) {
            what.getAndAdd(-4);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }
    }

    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            vp_img.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }

    };


    final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0]
                        .setBackgroundResource(R.drawable.round_on);
                if (arg0 != i) {
                    imageViews[i]
                            .setBackgroundResource(R.drawable.round_off);
                }
            }

        }

    }

    final class AdvAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        public AdvAdapter() {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imgs.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View imageLayout = inflater.inflate(R.layout.item_viewpager_images,
                    container, false);
            final ImageView photoView = (ImageView) imageLayout
                    .findViewById(R.id.imageView);
            final String imgUrl = (String) imgs.get(position);
//            imageLoader.displayImage(imgUrl, photoView, options);
            PicassoTrustAll.getInstance(context).load(imgUrl).into(photoView);
            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }
    }
}
