package com.xuhai.wngs.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.xuhai.wngs.R;

/**
 * Created by WR on 2014/12/8.
 */
public class ProgressDialogFragment extends DialogFragment{

    /**
     * 通过系统判断DialogFragment的显示形式，不论是以Dialog的形式显示，还是嵌入的Fragment显示
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 获得视图
        View view = inflater.inflate(R.layout.activity_dialog_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        AnimationDrawable anmi = (AnimationDrawable) imageView.getDrawable();
        anmi.start();
        return view;
    }

    /**
     * The system calls this only when creating the layout in a dialog. 当创建Dialog的时候系统将会调用
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /**
         * 你唯一可能会覆盖这个方法的原因就是当使用onCreateView()去修改任意Dialog特点的时候。例如，
         * dialog都有一个默认的标题，但是使用者可能不需要它。因此你可以去掉标题，但是你必须调用父类去获得Dialog。
         */

        Dialog dialog = new Dialog(getActivity(), R.style.DialogStyle);
//
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
