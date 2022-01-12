package com.example.trackingapp.service

import android.content.Intent
import android.util.Log
import com.example.trackingapp.sensor.AbstractSensor

class LogService : AbstractService() {
    private var sensorList: List<AbstractSensor>? = null
    override fun onCreate() {
        TAG = javaClass.name
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val ret = super.onStartCommand(intent, flags, startId)
        sensorList = LoggingManager.loggingService.sensorList
        sensorList?.let { list ->
            Log.d(TAG, "size: " + list.size)
            for (sensor in list) {
                if (sensor.isEnabled && sensor.isAvailable(this)) {
                    sensor.start(this)
                    //if(sensor instanceof MyAccelerometerSensor) ((MyAccelerometerSensor)sensor).start(this);
                    //if(sensor instanceof AppSensor) ((AppSensor)sensor).start(this);
                    Log.d(TAG, sensor.sensorName + " turned on")
                } else {
                    Log.w(TAG, sensor.sensorName + " turned off")
                }
            }
        }
        return ret
    }

    override fun onDestroy() {
        sensorList?.let { list ->
            for (sensor in list) {
                if (sensor.isRunning) {
                    sensor.stop()
                }
            }
        }
        super.onDestroy()
    }

    fun collectSnapShots(){
        sensorList?.let { list ->
            Log.d(TAG, "size: " + list.size)
            for (sensor in list) {
                if (sensor.isEnabled && sensor.isAvailable(this)) {
                    sensor.saveSnapshot(this)
                    //if(sensor instanceof MyAccelerometerSensor) ((MyAccelerometerSensor)sensor).start(this);
                    //if(sensor instanceof AppSensor) ((AppSensor)sensor).start(this);
                    Log.d(TAG, sensor.sensorName + " saveSnapshot")
                }
            }
        }
    }
}