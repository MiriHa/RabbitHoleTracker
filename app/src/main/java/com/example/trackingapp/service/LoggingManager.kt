package com.example.trackingapp.service

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.example.trackingapp.util.SharedPrefManager
import java.util.concurrent.TimeUnit


object LoggingManager {

    private const val TAG = "LOGGING_MANAGER"

    var userPresent = false

    var sensorList: MutableList<AbstractSensor> = arrayListOf(
        AccelerometerSensor(),
        AccessibilitySensor(),
        ActivityRecognitionSensor(),
        AirplaneModeSensor(),
        AppInstallsSensor(),
        BluetoothSensor(),
        CallSensor(),
        DataTrafficSensor(),
        GyroscopeSensor(),
        LightSensor(),
        NotificationSensor(),
        OrientationSensor(),
        PowerSensor(),
        ProximitySensor(),
        RingerModeSensor(),
        ScreenOrientationSensor(),
        ScreenStateSensor(),
        SmsSensor(),
        UsageStatsSensor(),
        WifiSensor()
    )

    init {
        Log.d("TAG", "init LoggingManager/sensorlist $sensorList")
        //if (sensorList == null) sensorList = createSensorList()
    }

    val _isLoggingActive = MutableLiveData(false)
    val isLoggingActive: LiveData<Boolean> = _isLoggingActive

    val isDataRecordingActive: Boolean
        get() = SharedPrefManager.getBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE)

    private fun firstStartLoggingService(context: Context) {
        PhoneState.logCurrentPhoneState(context)
    }

    fun startLoggingService(context: Context) {
        if (!SharedPrefManager.getBoolean(CONST.PREFERENCES_LOGGING_FIRST_STARTED)) {
            firstStartLoggingService(context)
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_LOGGING_FIRST_STARTED, true)
        }
        if (isLoggingActive.value == false) {
            Log.d(TAG, "startService called")
            Toast.makeText(context, "Start LoggingService", Toast.LENGTH_LONG).show()
            val serviceIntent = Intent(context, LoggingService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
            startServiceViaWorker(context)
            _isLoggingActive.value = true
        }
    }

    fun stopLoggingService(context: Context) {
        Log.d(TAG, "stopService called")
        Toast.makeText(context, "Stop LoggingService", Toast.LENGTH_LONG).show()
        val stopIntent = Intent(context, LoggingService::class.java)
        context.applicationContext.stopService(stopIntent)
        cancleServiceViaWorker(context)
        _isLoggingActive.value = false
    }

    fun ensureLoggingManagerIsAlive(context: Context) {
        if (isLoggingActive.value == false && isDataRecordingActive) {
            startLoggingService(context)
        }
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
        Log.d(TAG, "createSensorList")
        val list = arrayListOf<AbstractSensor>()
        list.add(AccelerometerSensor())
        list.add(AccessibilitySensor())
        list.add(ActivityRecognitionSensor())
        list.add(AirplaneModeSensor())
        list.add(AppInstallsSensor())
        list.add(BluetoothSensor())
        list.add(CallSensor())
        list.add(DataTrafficSensor())
        list.add(GyroscopeSensor())
        list.add(LightSensor())
        list.add(NotificationSensor())
        list.add(OrientationSensor())
        list.add(PowerSensor())
        list.add(ProximitySensor())
        list.add(RingerModeSensor())
        list.add(ScreenOrientationSensor())
        list.add(ScreenStateSensor())
        list.add(SmsSensor())
        list.add(UsageStatsSensor())
        list.add(WifiSensor())
        return list
    }
}
