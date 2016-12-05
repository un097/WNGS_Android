package com.xuhai.wngs.ui.main;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.adapters.main.MainGridAdapter;
import com.xuhai.wngs.beans.main.MainBean;
import com.xuhai.wngs.beans.main.MainModdtlBean;
import com.xuhai.wngs.ui.more.LoginActivity;
import com.xuhai.wngs.ui.more.MoreWDDDActivity;
import com.xuhai.wngs.ui.more.MoreWDKDActivity;
import com.xuhai.wngs.ui.shzl.ShzlBBSActivity;
import com.xuhai.wngs.ui.shzl.ShzlBLDActivity;
import com.xuhai.wngs.ui.shzl.ShzlOpenDActitity;
import com.xuhai.wngs.ui.sjfw.SjfwPublicListActivity;
import com.xuhai.wngs.ui.wyfw.WyfwBMFWActivity;
import com.xuhai.wngs.ui.wyfw.WyfwGJXXActivity;
import com.xuhai.wngs.ui.wyfw.WyfwHDActivity;
import com.xuhai.wngs.ui.wyfw.WyfwTSBXActivity;
import com.xuhai.wngs.ui.wyfw.WyfwZFCXActivity;
import com.xuhai.wngs.ui.wyfw.WyfwZXGGActivity;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ScrollGridView;

import java.io.Serializable;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements Constants, ViewTreeObserver.OnGlobalLayoutListener {

    public static final String TAG = "MainFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int index; //页面索引
    private List<MainModdtlBean> mList;

    private View gridMarginTopView;
    private ImageView imageView;
    private ScrollGridView gridView;
    private MainGridAdapter gridAdapter;

    public static MainFragment newInstance(List<MainModdtlBean> param1,int param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mList = (List<MainModdtlBean>) getArguments().getSerializable(ARG_PARAM1);
            index = getArguments().getInt(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridMarginTopView = view.findViewById(R.id.gridMarginTopView);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        gridView = (ScrollGridView) view.findViewById(R.id.gridView);
        gridView.setFocusable(false);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                //物业服务
                if (mList.get(position).getFucbs().equals("xwys")) {
//                    Intent intent = new Intent(getActivity(), WyfwTSBXActivity.class);
                    if (((MainActivity) getActivity()).IS_LOGIN) {
                        intent = new Intent(getActivity(), WyfwTSBXActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, Constants.STATE_LOGIN);
                    }
                }
                else if (mList.get(position).getFucbs().equals("bmfu")) {
                    intent.setClass(getActivity(), WyfwBMFWActivity.class);
                    startActivity(intent);
                }
                else if (mList.get(position).getFucbs().equals("sqzx")) {
                    intent.setClass(getActivity(), WyfwZXGGActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                else if (mList.get(position).getFucbs().equals("gjxx")) {
                    if (((MainActivity) getActivity()).IS_LOGIN) {
                        intent = new Intent(getActivity(), WyfwGJXXActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, Constants.STATE_LOGIN);
                    }
                }
                else if (mList.get(position).getFucbs().equals("zfcx")) {
                    if (((MainActivity) getActivity()).IS_LOGIN) {
                        intent = new Intent(getActivity(), WyfwZFCXActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, Constants.STATE_LOGIN);
                    }
                }


                //生活助理
//                else if (mList.get(position).getFuncid().equals("bmxx")) {
//                    intent.setClass(getActivity(), ShzlBMXXActivity.class);
//                    startActivity(intent);
//                } else if (mList.get(position).getName().equals("yhxx")) {
//                    intent.setClass(getActivity(), ShzlYHXXActivity.class);
//                    startActivity(intent);
//                }
//
                else if (mList.get(position).getFucbs().equals("llq")) {
                    intent.setClass(getActivity(), ShzlBBSActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                else if (mList.get(position).getFucbs().equals("hd")) {
                    intent.setClass(getActivity(), WyfwHDActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                else if (mList.get(position).getFucbs().equals("kd")) {
                    if (((MainActivity) getActivity()).IS_LOGIN) {
                        intent = new Intent(getActivity(), MoreWDKDActivity.class);
                        startActivityForResult(intent, Constants.STATE_LOGIN);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, Constants.STATE_LOGIN);
                    }
                }
                else if (mList.get(position).getFucbs().equals("km")) {
                    if (((MainActivity) getActivity()).IS_LOGIN) {
                        if (((MainActivity) getActivity()).IS_AUTH) {
                            intent = new Intent(getActivity(), ShzlOpenDActitity.class);
                            intent.putExtra("pid", "lanya");
                            intent.putExtra("title", "蜗牛开门");
                            startActivity(intent);
                        } else {
                            CustomToast.showToast(getActivity(), "请先认证",2000);
                        }
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, Constants.STATE_LOGIN);
                    }
                }

                //商家服务
                else if (mList.get(position).getFucbs().equals("sj")) {
                    intent.setClass(getActivity(), SjfwPublicListActivity.class);
                    intent.putExtra("funcid", mList.get(position).getFuncid());
                    intent.putExtra("title", mList.get(position).getFucname());
                    startActivity(intent);
                }

                else if (mList.get(position).getFucbs().equals("wy")){
                    intent.setClass(getActivity(), WebActivity.class);
                    intent.putExtra("url", mList.get(position).getFucurl());
                    intent.putExtra("title", mList.get(position).getFucname());
                    startActivity(intent);
                }

            }
        });

        handler.sendEmptyMessage(LOAD_SUCCESS);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    switch (index) {
                        case 0:
                            imageView.setBackgroundResource(R.drawable.two);
                            break;
                        case 1:
                            imageView.setBackgroundResource(R.drawable.one);
                            break;
                        case 2:
                            imageView.setBackgroundResource(R.drawable.three);
                            break;
                    }


                    gridAdapter = new MainGridAdapter(getActivity(), mList);
                    gridView.setAdapter(gridAdapter);

//                    gridAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onGlobalLayout() {
        if (imageView.getHeight() > 0) {
            ((MainActivity) getActivity()).editor.putInt(SPN_MAIN_TOP_HEIGHT, imageView.getHeight());
            ((MainActivity) getActivity()).editor.commit();
        }


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ((MainActivity) getActivity()).spn.getInt(SPN_MAIN_TOP_HEIGHT, 0));
        gridMarginTopView.setLayoutParams(params);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ((MainActivity) getActivity()).spn.getInt(SPN_MAIN_TOP_HEIGHT, 0));
        gridMarginTopView.setLayoutParams(params);

        MobclickAgent.onPageStart("MainScreen"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
