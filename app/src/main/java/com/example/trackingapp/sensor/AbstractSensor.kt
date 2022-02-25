package com.example.trackingapp.sensor

import android.content.Context
import android.util.Log
import com.example.trackingapp.util.SharedPrefManager
import com.google.firebase.FirebaseApp
import java.io.Serializable

abstract class AbstractSensor protected constructor(
    protected var TAG: String?,
    var sensorName: String?
) : Serializable {

    var isEnabled = true
    var isRunning = false

    protected var isSensorAvailable = false

    abstract fun isAvailable(context: Context): Boolean

    open fun start(context: Context) {
        isSensorAvailable = isAvailable(context)
        if (!isSensorAvailable) Log.i(TAG, "Sensor not available")
        FirebaseApp.initializeApp(context)
        SharedPrefManager.init(context)
    }
    abstract fun stop()

    open fun saveSnapshot(context: Context){
        //Log.i(TAG, "save Snapshot if possible")
    }
}