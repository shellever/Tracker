package com.shellever.tracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by linuxfor on 8/11/2016.
 */
public class MyOrientationListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private Context mContext;
    private Sensor mSensor;
    private float lastX;

    public MyOrientationListener(Context context){
        this.mContext = context;
    }

    @SuppressWarnings("deprecation")
    public void start(){
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager != null){
            //获取方向传感器
            //Sensor.TYPE_ORIENTATION
            //使用注解来消除TYPE_ORIENTATION已过时的警告
            //使用SensorManager.getOrientation()方法来替代
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if(mSensor != null){
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop(){
        mSensorManager.unregisterListener(this);
    }

    //当数据变化时被触发调用
    @SuppressWarnings({"deprecation"})
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            float x = event.values[SensorManager.DATA_X];
            if(Math.abs(x - lastX) > 1.0){
                if(mOnOrientationListener != null){
                    mOnOrientationListener.onOrientationChanged(x);
                }
            }
            lastX = x;  //更新最近的数值
        }
    }

    //当获得数据的精度发生变化的时候被调用，比如突然无法获得数据时
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private OnOrientationListener mOnOrientationListener;

    public void setOnOrientationListener(OnOrientationListener listener) {
        this.mOnOrientationListener = listener;
    }

    public interface OnOrientationListener{
        void onOrientationChanged(float x);
    }
}
