package com.xuhai.wngs.utils;

import android.graphics.ColorMatrixColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ClickEffect {

	/**
	 * ********************************************* 
	 * RGB和Alpha的终值计算方法如下：
	 * 
	 * Red通道终值 = a[0] * srcR + a[1] * srcG + a[2] * srcB + a[3] * srcA + a[4]
	 * 
	 * Green通道终值 = a[5] * srcR + a[6] * srcG + a[7] * srcB + a[8] * srcA + a[9]
	 * 
	 * Blue通道终值 = a[10] * srcR + a[11] * srcG + a[12] * srcB + a[13] * srcA +
	 * a[14]
	 * 
	 * Alpha通道终值 = a[15] * srcR + a[16] * srcG + a[17] * srcB + a[18] * srcA +
	 * a[19]
	 */
	/**
	 * 按钮被按下
	 */
	private final static float[] BUTTON_PRESSED = new float[] {

	0.35f, 0, 0, 0, 18.525f, 0, 0.35f, 0, 0, 18.525f, 0, 0, 0.35f, 0, 18.525f, 0, 0, 0, 1, 0

	};

	/**
	 * 按钮恢复原状
	 */

	private final static float[] BUTTON_RELEASED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
			1, 0 };

	private static final OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_PRESSED));
				v.setBackgroundDrawable(v.getBackground());
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_RELEASED));
				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
		}

	};

	public static void setButtonStateChangeListener(View v) {

		v.setOnTouchListener(touchListener);
	}
}
