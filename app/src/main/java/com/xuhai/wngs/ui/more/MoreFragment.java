package com.xuhai.wngs.ui.more;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.mobilesecuritysdk.deviceID.LOG;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.JsonObjectHeadersRequest;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xuhai.wngs.Constants;
import com.xuhai.wngs.MainActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.WebActivity;
import com.xuhai.wngs.beans.main.MainInfoBean;
import com.xuhai.wngs.ui.main.MainCitySelActivity;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.utils.Dianjill;
import com.xuhai.wngs.utils.EncryptionByMD5;
import com.xuhai.wngs.utils.PicassoTrustAll;
import com.xuhai.wngs.utils.UpdateManager;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link com.xuhai.wngs.ui.more.MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment implements View.OnClickListener, Constants {

    public static final String TAG = "MoreFragment";

    public boolean isAuth = false, isCheckIn = false;

    private ProgressDialogFragment newFragment;

    private CircleImageView img_head;
    private Button btn_sign;
    private TextView tv_login, tv_auth, tv_points;
    private RelativeLayout layout_auth, layout_integral, my_wddd, my_shdz, my_jfdd, more_gywm,layout_card,layout_yue;

//    private TextView tv_yue;
    private MainInfoBean mainInfoBean;

//    private Boolean IS_CHECKIN;

    public SharedPreferences spn;
    public SharedPreferences.Editor editor;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newFragment = new ProgressDialogFragment();

        spn = getActivity().getSharedPreferences(SPN_WNGS, Activity.MODE_PRIVATE);
        editor = spn.edit();

        img_head = (CircleImageView) view.findViewById(R.id.img_head);
        img_head.setOnClickListener(this);
        btn_sign = (Button) view.findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(this);
        tv_login = (TextView) view.findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);

        layout_auth = (RelativeLayout) view.findViewById(R.id.layout_auth);
        layout_auth.setOnClickListener(this);
        layout_integral = (RelativeLayout) view.findViewById(R.id.layout_integral);
        layout_integral.setOnClickListener(this);
        my_jfdd = (RelativeLayout) view.findViewById(R.id.myjfdd);
        my_jfdd.setOnClickListener(this);
        my_wddd = (RelativeLayout) view.findViewById(R.id.mydd);
        my_wddd.setOnClickListener(this);
        my_shdz = (RelativeLayout) view.findViewById(R.id.myshdz);
        my_shdz.setOnClickListener(this);
//        tv_yue = (TextView) view.findViewById(R.id.tv_yue);
        more_gywm = (RelativeLayout) view.findViewById(R.id.moregywm);
        more_gywm.setOnClickListener(this);

        layout_card = (RelativeLayout) view.findViewById(R.id.layout_card);
        layout_card.setOnClickListener(this);
        layout_yue = (RelativeLayout) view.findViewById(R.id.layout_yue);
        layout_yue.setOnClickListener(this);

        tv_auth = (TextView) view.findViewById(R.id.tv_auth);
        tv_points = (TextView) view.findViewById(R.id.tv_points);

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_me_red);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img_head.getLayoutParams();
        params.topMargin = mBitmap.getHeight() - dip2px(getActivity(),64) / 2;
        img_head.setLayoutParams(params);


        if (spn.getBoolean(SPN_IS_LOGIN,false)) {
            httpGetinfo(HTTP_USER_INFO);

            Log.d("url===",HTTP_USER_INFO + "?sqid=" + AESEncryptor.decrypt(spn.getString(SPN_SQID, "")) + "&uid=" + AESEncryptor.decrypt(spn.getString(SPN_UID, "")));
        } else {
            handler.sendEmptyMessage(Constants.LOAD_SUCCESS);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.LOAD_SUCCESS:
                    try {
                        setUserHead(AESEncryptor.decrypt(spn.getString(SPN_USER_HEAD, "")));
                        setPoints(AESEncryptor.decrypt(spn.getString(SPN_POINTS_TOTLA, "")));
                        setLoginOrName(AESEncryptor.decrypt(spn.getString(SPN_NICK_NAME, "")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                        setCheckin(spn.getBoolean(SPN_USER_CHECKIN, false));
                        setAuth(spn.getBoolean(SPN_AUTH, false));
//                        setBalance(spn.getString(SPN_BALANCE,""));
                    break;
            }
            return false;
        }
    });

    private void setUserHead(String head) {
        if (spn.getBoolean(SPN_IS_LOGIN,false)) {
            if (head.equals("")) {
                img_head.setImageResource(R.drawable.ic_more_camera);
            } else {
                PicassoTrustAll.getInstance(getActivity()).load(head).placeholder(R.drawable.ic_huisewoniu).error(R.drawable.ic_huisewoniu).resizeDimen(R.dimen.base_dimen_64, R.dimen.base_dimen_64).centerCrop().into(img_head);

            }
        } else {
            img_head.setImageResource(R.drawable.ic_more_camera);
        }
    }

    private void setLoginOrName(String name) {
        if (spn.getBoolean(SPN_IS_LOGIN,false)) {
            if (name.equals("")) {
                try {
                    tv_login.setText(AESEncryptor.decrypt(spn.getString(SPN_USER_PHONE, "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                tv_login.setText(name);
            }
        } else {
            tv_login.setText(getString(R.string.more_login_or_register));
        }
    }

    private void setCheckin(boolean checkin) {
        if (checkin) {
            btn_sign.setVisibility(View.GONE);
        } else {
            btn_sign.setVisibility(View.VISIBLE);
        }
    }

    private void setPoints(String points) {
        tv_points.setText("积分 " + points);
    }

//    private void setBalance(String balance) {
//        if (!spn.getString(SPN_BALANCE,"").equals("")){
//            tv_yue.setText(balance + "元");
//        }else {
//            tv_yue.setText("");
//        }
//    }

    private void setAuth(boolean auth) {
        if (spn.getBoolean(SPN_IS_LOGIN,false)) {
            if (auth) {
                tv_auth.setText(getResources().getString(R.string.has_auth));
            } else {
                tv_auth.setText(getResources().getString(R.string.un_auth));
            }
        } else {
            tv_auth.setText(getResources().getString(R.string.un_auth));

        }
    }

    //判断网络
    public static boolean checkNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    public void httpGetinfo(String url) {

        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("sqid", AESEncryptor.decrypt(spn.getString(SPN_SQID, "")));
            params.put("uid", AESEncryptor.decrypt(spn.getString(SPN_UID, "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                try {
                    if (response.has("recode")) {
                        Log.d("res====",response + "");

                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {


                            if (response.has("head")) {
                                editor.putString(SPN_USER_HEAD, AESEncryptor.encrypt(response.getString("head")));
                            }
                            if (response.has("nickname")) {
                                editor.putString(SPN_NICK_NAME, AESEncryptor.encrypt(response.getString("nickname")));
                            }
                            if (response.has("bankuid")){
                                editor.putString(SPN_BANK_UID, AESEncryptor.encrypt(response.getString("bankuid")));
                            }
                            if (response.has("note")) {
                                editor.putString(SPN_USER_NOTE, AESEncryptor.encrypt(response.getString("note")));
                            }
//                            if (response.has("express")) {
//                                editor.putString(SPN_EXPRESS, AESEncryptor.encrypt(response.getString("express")));
//                            }if (response.has("info")) {
//                                editor.putString(SPN_INFO, AESEncryptor.encrypt(response.getString("info")));
//                            }if (response.has("bbs")) {
//                                editor.putString(SPN_BBS, AESEncryptor.encrypt(response.getString("bbs")));
//                            }
//                            //新增
                            if (response.has("balance")) {
                                editor.putString(SPN_BALANCE, AESEncryptor.encrypt(response.getString("balance")));
                            }
                            if (response.has("ischeck")){
                                if (response.getString("ischeck").equals("0")){
                                    editor.putBoolean(SPN_IS_BANKCHECK,true);
                                }
                            }
                            if (response.has("ispasswd")){
                                if (response.getString("ispasswd").equals("0")){
                                    editor.putBoolean(SPN_IS_BANKPWD,true);
                                }
                            }
//                            //
                            if (response.has("checkin")) {
                                if (response.getString("checkin").equals("0")) {
                                    editor.putBoolean(SPN_USER_CHECKIN, false);
                                } else if (response.get("checkin").equals("1")) {
                                    editor.putBoolean(SPN_USER_CHECKIN, true);
                                }
                            }
                            if (response.has("auth")) {
                                if (response.getString("auth").equals("0")) {
                                    editor.putBoolean(SPN_AUTH, false);
                                } else if (response.getString("auth").equals("1")) {
                                    editor.putBoolean(SPN_AUTH, true);
                                }
                            }
                            if (response.has("auth_name")) {
                                editor.putString(SPN_AUTH_NAME, AESEncryptor.encrypt(response.getString("auth_name")));
                            }
                            if (response.has("auth_phone")) {
                                editor.putString(SPN_AUTH_PHONE, AESEncryptor.encrypt(response.getString("auth_phone")));
                            }
                            if (response.has("auth_building")) {
                                editor.putString(SPN_AUTH_BUILDING, AESEncryptor.encrypt(response.getString("auth_building")));
                            }
                            if (response.has("auth_unit")) {
                                editor.putString(SPN_AUTH_UNIT, AESEncryptor.encrypt(response.getString("auth_unit")));
                            }
                            if (response.has("auth_room")) {
                                editor.putString(SPN_AUTH_ROOM, AESEncryptor.encrypt(response.getString("auth_room")));
                            }
                            if (response.has("integral")) {
                                editor.putString(SPN_POINTS_TOTLA, AESEncryptor.encrypt(response.getString("integral")));
                            }
//
                            editor.commit();
                            handler.sendEmptyMessage(LOAD_SUCCESS);
                        }else {
                            CustomToast.showToast(getActivity(),msg,1000);
                        }
                    } else {
                        CustomToast.showToast(getActivity(),R.string.http_fail,1000);
                    }
                } catch (Exception e) {
                    Log.d("loginn",e+"");
                    CustomToast.showToast(getActivity(),R.string.http_fail,1000);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("loginn",error+"");
                CustomToast.showToast(getActivity(),R.string.http_fail,1000);
            }
        });
        ((MainActivity) getActivity()).queue.add(request);
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_head:
                if (spn.getBoolean(SPN_IS_LOGIN,false)) {
                    Intent intent = new Intent(getActivity(), MyinfoActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }

                break;

            case R.id.tv_login:
                if (spn.getBoolean(SPN_IS_LOGIN,false)) {
                    Intent intent = new Intent(getActivity(), MyinfoActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                break;

            case R.id.btn_sign:
                if (Dianjill.isFastDoubleClick()) {
                    return;
                }
                httpCheckin(Constants.HTTP_CHECK);
                break;

            case R.id.layout_auth:
                Intent intent = new Intent();
                if (spn.getBoolean(SPN_IS_LOGIN,false)) {
                    if (isAuth) {
                        CustomToast.showToast(getActivity(), "亲，您已经认证过啦！", 2000);
                    } else {
                        intent = new Intent(getActivity(), MoreYZRZActivity.class);
                        startActivityForResult(intent, Constants.STATE_LOGIN);
                    }
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }

                break;
            case R.id.layout_integral:

                if (spn.getBoolean(SPN_IS_LOGIN,false)) {
                    intent = new Intent(getActivity(), MoreWDJFActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                break;
            case R.id.mydd:
                if (spn.getBoolean(SPN_IS_LOGIN,false)) {
                    intent = new Intent(getActivity(), MoreWDDDActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                break;
            case R.id.myjfdd:
                if (spn.getBoolean(SPN_IS_LOGIN,false)) {
                    intent = new Intent(getActivity(), MoreJFDDActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                break;
            case R.id.myshdz:
                if (spn.getBoolean(SPN_IS_LOGIN,false)) {
                    intent = new Intent(getActivity(), MoreSHDZActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.STATE_LOGIN);
                }
                break;

            case R.id.moregywm:
                intent = new Intent(getActivity(), MoreGYWMActivity.class);

                startActivity(intent);
                break;
            case R.id.layout_card:
                intent = new Intent(getActivity(),MoreCardListActivity.class);
                intent.putExtra("action", "jiebang");
                startActivity(intent);
                break;
            case R.id.layout_yue:
                intent = new Intent(getActivity(),MoreYueActivity.class);
                startActivityForResult(intent, Constants.STATE_LOGIN);
                break;

        }
    }


    //签到
    private void httpCheckin(String url) {
        newFragment.show(getActivity().getFragmentManager(),"1");
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("sqid", AESEncryptor.decrypt(spn.getString(SPN_SQID,"")));
            params.put("uid", AESEncryptor.decrypt(spn.getString(SPN_UID,"")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String recode, msg;
                try {
                    if (response.has("recode")) {
                        recode = response.getString("recode");
                        msg = response.getString("msg");

                        if (recode.equals("0")) {
                            newFragment.dismiss();
                            btn_sign.setVisibility(View.GONE);
                            editor.putBoolean(Constants.SPN_USER_CHECKIN, true);
                            editor.commit();
                            CustomToast.showToast(getActivity(), "签到成功", 1000);

                            tv_points.setText("积分 "+Integer.valueOf(Integer.valueOf(AESEncryptor.decrypt(spn.getString(SPN_POINTS_TOTLA,"")))+1));

                        } else if (recode.equals("2")) {
                            newFragment.dismiss();
                            btn_sign.setVisibility(View.GONE);
                            editor.putBoolean(Constants.SPN_USER_CHECKIN, true);
                            editor.commit();
                            CustomToast.showToast(getActivity(), msg, 1000);
                        } else {
                            newFragment.dismiss();
                            CustomToast.showToast(getActivity(), msg, 1000);
                        }
                    } else {
                        newFragment.dismiss();
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newFragment.dismiss();
            }
        });
        ((MainActivity) getActivity()).queue.add(request);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); //统计页面
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
