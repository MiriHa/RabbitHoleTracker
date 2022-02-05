package com.example.trackingapp.service

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.sensor.activityrecognition.ActivityRecognitionSensor
import com.example.trackingapp.sensor.androidsensors.*
import com.example.trackingapp.sensor.communication.CallSensor
import com.example.trackingapp.sensor.communication.NotificationSensor
import com.example.trackingapp.sensor.communication.SmsSensor
import com.example.trackingapp.sensor.connection.BluetoothSensor
import com.example.trackingapp.sensor.connection.PowerSensor
import com.example.trackingapp.sensor.connection.WifiSensor
import com.example.trackingapp.sensor.modes.AirplaneModeSensor
import com.example.trackingapp.sensor.modes.RingerModeSensor
import com.example.trackingapp.sensor.modes.ScreenOrientationSensor
import com.example.trackingapp.sensor.modes.ScreenStateSensor
import com.example.trackingapp.sensor.usage.*
import com.example.trackingapp.service.stayalive.StartLoggingWorker
import com.example.trackingapp.util.CONST
import java.util.concurrent.TimeUnit


object LoggingManager {

    private const val TAG = "TRACKINGAPP_LOGGING_MANAGER"

    var userPresent = false

    var sensorList: MutableList<AbstractSensor>

    init {
        Log.d("xxx", "init LoggingManager/sensorlist")
        sensorList = createSensorList()
    }

    fun isServiceRunning(context: Context): Boolean {
        Log.d(TAG, "isServiceRunning: ${LoggingService.isRunning}")
        return LoggingService.isRunning
        //return SharedPrefManager.getBoolean(CONST.PREFERENCES_IS_LOGGING_SERVICE_RUNNING)
    }

    var isDataRecordingActive: Boolean? = null


    fun startLoggingService(context: Context) {
        if (!isServiceRunning(context)) {
            Log.d(TAG, "startService called")
            Toast.makeText(context, "Start LoggingService", Toast.LENGTH_LONG).show()
            PhoneState.logCurrentPhoneState(context)
            val serviceIntent = Intent(context, LoggingService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
            startServiceViaWorker(context)
        }
    }

    fun stopLoggingService(context: Context) {
        //if (isServiceRunning(context)) {
        Log.d(TAG, "stopService called")
        isDataRecordingActive = false
        Toast.makeText(context, "Stop LoggingService", Toast.LENGTH_LONG).show()
        PhoneState.logCurrentPhoneState(context)
        val stopIntent = Intent(context, LoggingService::class.java)
        context.applicationContext.stopService(stopIntent)
        cancleServiceViaWorker(context)
        // }
    }

    private fun startServiceViaWorker(context: Context) {
        Log.d(TAG, "startServiceViaWorker called")
        val workManager: WorkManager = WorkManager.getInstance(context)

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
        val request: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            StartLoggingWorker::class.java,
            CONST.LOGGING_CHECK_FOR_LOGGING_ALIVE_INTERVAL,
            TimeUnit.MINUTES
        ).build()

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        // do check for AutoStart permission
        workManager.enqueueUniquePeriodicWork(
            CONST.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun cancleServiceViaWorker(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(CONST.UNIQUE_WORK_NAME)
    }

    private fun createSensorList(): MutableList<AbstractSensor> {
        val list = arrayListOf<AbstractSensor>()
        list.add(AirplaneModeSensor())
        list.add(ScreenStateSensor())
        list.add(NotificationSensor())
        list.add(WifiSensor())
        list.add(PowerSensor())
        list.add(AccessibilitySensor())
        list.add(CallSensor())
        list.add(SmsSensor())
        list.add(ScreenOrientationSensor())
        list.add(RingerModeSensor())
        list.add(AccelerometerSensor())
        list.add(GyroscopeSensor())
        list.add(LightSensor())
        list.add(ProximitySensor())
        list.add(OrientationSensor())
        list.add(BluetoothSensor())
        list.add(ActivityRecognitionSensor())
        list.add(DataTrafficSensor())
        list.add(UsageStatsSensor())
        list.add(AppInstallsSensor())
        return list
    }
}
