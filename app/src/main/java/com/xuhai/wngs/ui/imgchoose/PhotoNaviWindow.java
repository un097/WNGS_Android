package com.xuhai.wngs.ui.imgchoose;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

import com.xuhai.wngs.R;

public class PhotoNaviWindow extends PopupWindow {
	private OnClickListener onClickListener;
	private View view;
	private View parent;
	private Button btn1;
	private Button btn2;
	private Button btn3;

	public PhotoNaviWindow(Context context, View parent, int width, int height) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.pop_img_nav_view, null);
		setContentView(view);
		setWidth(width);
		setHeight(height);
		setFocusable(true);
		setOutsideTouchable(true);
		this.parent = parent;
		btn1 = (Button) view.findViewById(R.id.button1);
		btn2 = (Button) view.findViewById(R.id.button2);
		btn3 = (Button) view.findViewById(R.id.button3);
		setAnimationStyle(R.style.popup_win_ani);

	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
		btn1.setOnClickListener(onClickListener);
		btn2.setOnClickListener(onClickListener);
		btn3.setOnClickListener(onClickListener);

	}

	public void show() {
		showAtLocation(parent, Gravity.BOTTOM, 0, 0);

	}
}
