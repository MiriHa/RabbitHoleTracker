package com.example.trackingapp.sensor

import android.content.Context
import com.example.trackingapp.sensor.implementation.BootUpSensor
import com.example.trackingapp.sensor.implementation.PowerSensor
import com.example.trackingapp.sensor.implementation.ScreenOnOffSensor
import com.example.trackingapp.sensor.implementation.WifiSensor
import java.util.*

object SensorList {
    @JvmStatic
	fun getList(pContext: Context?): List<AbstractSensor> {
        val list: MutableList<AbstractSensor> = ArrayList()
        //list.add(AccessibilitySensor())
        //list.add(MyAccelerometerSensor())
        //list.add(new ActivitySensor()); // This is not longer supported by Android
        //list.add(new AirplaneModeSensor());
        //list.add(AppSensor())
        //list.add(new AudioLevelSensor());
        //list.add(ChargingSensor())
//        list.add(MyGyroscopeSensor())
//        list.add(MyLightSensor())
//        list.add(MyProximitySensor())
//        list.add(OrientationSensor())
//        list.add(RingtoneVolumeSensor())
        list.add(ScreenOnOffSensor())
//        list.add(ScreenOrientationSensor())
//        list.add(StillAliveSensor())
//        list.add(TouchSensor())
       list.add(WifiSensor())
       list.add(BootUpSensor())
        list.add(PowerSensor())

        return list
    }
}