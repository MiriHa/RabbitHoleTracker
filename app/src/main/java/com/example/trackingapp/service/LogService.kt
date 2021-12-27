package com.example.trackingapp.service

import android.content.Intent
import android.util.Log
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.sensor.SensorList.getList

class LogService : AbstractService() {
    private var sensorList: List<AbstractSensor>? = null
    override fun onCreate() {
        TAG = javaClass.name
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val ret = super.onStartCommand(intent, flags, startId)
        sensorList = getList(this)
        Log.d(TAG, "size: " + sensorList!!.size)
        for (sensor in sensorList!!) {
            if (sensor.isEnabled && sensor.isAvailable(this)) {
                sensor.start(this)
                //if(sensor instanceof MyAccelerometerSensor) ((MyAccelerometerSensor)sensor).start(this);
                //if(sensor instanceof AppSensor) ((AppSensor)sensor).start(this);
                Log.d(TAG, sensor.sensorName + " turned on")
            } else {
                Log.w(TAG, sensor.sensorName + " turned off")
            }
        }
        return ret
    }

    override fun onDestroy() {
        for (sensor in sensorList!!) {
            if (sensor.isRunning) {
                sensor.stop()
            }
        }
        super.onDestroy()
    }
}