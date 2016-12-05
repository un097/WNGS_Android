package com.xuhai.wngs.ui.shzl;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectHeadersPostRequest;
import com.android.volley.toolbox.MultiPartStack;
import com.android.volley.toolbox.Volley;
import com.brtbeacon.sdk.BRTBeacon;
import com.brtbeacon.sdk.BRTBeaconManager;
import com.brtbeacon.sdk.BRTRegion;
import com.brtbeacon.sdk.RangingListener;
import com.brtbeacon.sdk.ServiceReadyCallback;
import com.brtbeacon.sdk.service.RangingResult;
import com.xuhai.wngs.BaseActionBarAsUpActivity;
import com.xuhai.wngs.R;
import com.xuhai.wngs.utils.AESEncryptor;
import com.xuhai.wngs.views.CustomToast;
import com.xuhai.wngs.views.ProgressDialogFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ShzlOpenDActitity extends BaseActionBarAsUpActivity{
    private String pid,title;
    SensorManager sensorManager = null;
    private SensorEventListener shakeListener;
    Vibrator vibrator = null;
    private boolean isRefresh = false;
    private BRTBeaconManager beaconManager;
    private ProgressDialogFragment newFragment;
    /**
     * 用于标识扫描指定uuid的设备 uuid为null 表示扫描所有
     */
//    private static final String UUID="C5F2FDD7-D3FC-D8F8-1D33-E71BB72CCCEC";
    private static final String UUID = "C5F2FDD7-D3FC-D8F8-1D33-E71BB72CCCEC";
    private static final BRTRegion ALL_BRIGHT_BEACONS_REGION = new BRTRegion("rid", UUID, null, null, null);
    private static final int REQUEST_ENABLE_BT = 1234;
    public RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("开门摇一摇", "");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yaoyiyao);
        beaconManager = new BRTBeaconManager(this);
        pid = getIntent().getStringExtra("pid");
        title = getIntent().getStringExtra("title");
        if (getActionBar() != null) {
            getActionBar().setTitle(title);
        }
        searchMachine();//判断是否开启蓝牙

        // 回调扫描结果
        beaconManager.setRangingListener(new RangingListener() {

            @Override
            public void onBeaconsDiscovered(final RangingResult rangingResult) {
                Log.d("====SIZE====", "" + rangingResult.beacons.size());
                for (int i = 0; i < rangingResult.beacons.size(); i++) {
                    BRTBeacon beacon = rangingResult.beacons.get(0);
                    Log.d("Major!!: ", "" + beacon.getMajor());//小区号
                    Log.d("Minor!!: ", "" + beacon.getMinor());//设备号
                    try {
                        Log.d("停止扫描了！！！！！！", "");
                        try {
                            // 停止扫描
                            beaconManager.stopRanging(ALL_BRIGHT_BEACONS_REGION);
                            // 关闭扫描服务
                            beaconManager.disconnect();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                        openDoorHttpRequest(""+beacon.getMajor(), ""+beacon.getMinor());//请求开门
                        break;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        isRefresh = false;
                    }
                }
            }

        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        shakeListener = new ShakeSensorListener();
    }

    //扫描
    private void searchMachine(){

        // 检查是否支持蓝牙低功耗
        if (!beaconManager.hasBluetoothle()) {
            Toast.makeText(ShzlOpenDActitity.this, "该设备没有BLE,不支持本功能.", Toast.LENGTH_LONG).show();
            return;
        }

        // 如果未打开蓝牙，则请求打开蓝牙。
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
//            connectToService();
        }
    }

    //扫描
    private void searchMachine1(){

        // 检查是否支持蓝牙低功耗
        if (!beaconManager.hasBluetoothle()) {
            Toast.makeText(ShzlOpenDActitity.this, "该设备没有BLE,不支持本功能.", Toast.LENGTH_LONG).show();
            return;
        }

        // 如果未打开蓝牙，则请求打开蓝牙。
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d("====open====", "" + 12345678);
            connectToService();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
//                connectToService();
                isRefresh = false;
            } else {
                Toast.makeText(ShzlOpenDActitity.this, "设备蓝牙未打开", Toast.LENGTH_LONG).show();
                isRefresh = false;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {
        // 扫描之前先建立扫描服务
        beaconManager.connect(new ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    // 开始扫描
                    Log.d("====SIZEopen====", "" + 1111111111);
                    beaconManager.startRanging(ALL_BRIGHT_BEACONS_REGION);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //请求开门接口
    public void openDoorHttpRequest(String sqid,String equipment_id){
        dialog();
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("userid", AESEncryptor.decrypt(spn.getString(SPN_UID, "")));
            params.put("sqid", AESEncryptor.decrypt(spn.getString(SPN_SQID,"")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("equipment_id", equipment_id);
        params.put("type_id",sqid);
        queue = Volley.newRequestQueue(this, new MultiPartStack());
        queue.start();
        JsonObjectHeadersPostRequest request = new JsonObjectHeadersPostRequest(Request.Method.POST, HTTP_OPENDOOR, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String recode, msg;
                Log.d("response===",response.toString());
                try {
                    //TODO......
                    recode = response.getString("recode");
                    msg = response.getString("msg");
                    if (recode.equals("0")) {
                        CustomToast.showToast(ShzlOpenDActitity.this, "已开门！", 1000);
                        isRefresh = false;
                        newFragment.dismiss();
                    }else{
                        CustomToast.showToast(ShzlOpenDActitity.this,msg, 1000);
                        newFragment.dismiss();
                        isRefresh = false;
                    }
                } catch (Exception e) {
                    newFragment.dismiss();
                    isRefresh = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    // 停止扫描
                    beaconManager.stopRanging(ALL_BRIGHT_BEACONS_REGION);
                    // 关闭扫描服务
                    beaconManager.disconnect();
                    isRefresh = false;
                }catch(Exception ex){
                    ex.printStackTrace();
                    isRefresh = false;
                }
            }
        });
        queue.add(request);
    }
    private void dialog() {
        FragmentManager fragmentManager = getFragmentManager();
        newFragment = new ProgressDialogFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 指定一个过渡动画
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // 作为全屏显示,使用“content”作为fragment容器的基本视图,这始终是Activity的基本视图
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    @Override
    public void onPause(){
        sensorManager.unregisterListener(shakeListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        sensorManager.registerListener(shakeListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);
        super.onResume();
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            // 停止扫描
            beaconManager.stopRanging(ALL_BRIGHT_BEACONS_REGION);
            // 关闭扫描服务
            beaconManager.disconnect();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_yaoyiyao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class ShakeSensorListener implements SensorEventListener {
        private static final int ACCELERATE_VALUE = 20;
        @Override
        public void onSensorChanged(SensorEvent event) {
            // 判断是否处于刷新状态(例如微信中的查找附近人)
            if (isRefresh) {
                return;
            }
            float[] values = event.values;

            /**
             * 一般在这三个方向的重力加速度达到20就达到了摇晃手机的状态 x : x轴方向的重力加速度，向右为正 y :
             * y轴方向的重力加速度，向前为正 z : z轴方向的重力加速度，向上为正
             */
            float x = Math.abs(values[0]);
            float y = Math.abs(values[1]);
            float z = Math.abs(values[2]);

            Log.d("zhengyi.wzy", "x is :" + x + " y is :" + y + " z is :" + z);

            if (x >= ACCELERATE_VALUE || y >= ACCELERATE_VALUE|| z >= ACCELERATE_VALUE) {
                vibrator.vibrate(500);
                isRefresh = true;
                searchMachine1();
                //     connectToService();
                shakeFun();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

    }

    private void shakeFun(){
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.shakeimage).startAnimation(shake);
    }

}
