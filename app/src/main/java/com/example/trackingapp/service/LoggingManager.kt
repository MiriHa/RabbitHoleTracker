package com.example.trackingapp.service

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.example.trackingapp.util.NotificationHelper
import com.example.trackingapp.util.SharedPrefManager
import com.example.trackingapp.util.SurveryType
import java.util.*
import java.util.concurrent.TimeUnit


object LoggingManager {
    private const val TAG = "LOGGING_MANAGER"

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

    val userPresent: Boolean
        get() = SharedPrefManager.getBoolean(CONST.PREFERENCES_USER_PRESENT)

    val currentSessionID: String?
        get() = cachedSessionID ?: SharedPrefManager.getCurrentSessionID()

    var cachedSessionID: String? = null

    val _isLoggingActive = MutableLiveData(false)
    val isLoggingActive: LiveData<Boolean> = _isLoggingActive

    val isDataRecordingActive: Boolean
        get() = SharedPrefManager.getBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE)

    private fun firstStartLoggingService(context: Context) {
        PhoneState.logCurrentPhoneState(context)
        calculateStudyInterval()
    }

    fun startLoggingService(context: Context) {
        if (!SharedPrefManager.getBoolean(CONST.PREFERENCES_LOGGING_FIRST_STARTED)) {
            firstStartLoggingService(context)
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_LOGGING_FIRST_STARTED, true)
        }
        Log.d(TAG, "startService called")
        val serviceIntent = Intent(context, LoggingService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
        startServiceViaWorker(context)
        _isLoggingActive.value = true
    }

    fun stopLoggingService(context: Context) {
        Log.d(TAG, "stopService called")
        val stopIntent = Intent(context, LoggingService::class.java)
        context.applicationContext.stopService(stopIntent)
        cancelServiceViaWorker(context)
        _isLoggingActive.value = false
    }

    fun ensureLoggingManagerIsAlive(context: Context) {
        Log.d(TAG, "ensureLoggingManager is alive, restartneeded: ${isLoggingActive.value} $isDataRecordingActive}")
        if (isLoggingActive.value == false && isDataRecordingActive) {
            startLoggingService(context)
        }
    }

    private fun startServiceViaWorker(context: Context) {
        Log.d(TAG, "startServiceViaWorker called")
        val workManager: WorkManager = WorkManager.getInstance(context)

        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes, but in practice 15 doesn't work. Using 16 here
        val request: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            StartLoggingWorker::class.java,
            CONST.LOGGING_CHECK_FOR_LOGGING_ALIVE_INTERVAL,
            TimeUnit.MINUTES
        ).build()

        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
        workManager.enqueueUniquePeriodicWork(
            CONST.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun cancelServiceViaWorker(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(CONST.UNIQUE_WORK_NAME)
    }

    private fun calculateStudyInterval() {
        val calendar: Calendar = Calendar.getInstance()
        val studyStart: Long = calendar.timeInMillis
        calendar.add(Calendar.WEEK_OF_MONTH, 2)
        val studyEnd: Long = calendar.timeInMillis
        SharedPrefManager.saveLong(CONST.PREFERENCES_STUDY_START, studyStart)
        SharedPrefManager.saveLong(CONST.PREFERENCES_STUDY_END, studyEnd)
    }

    fun isStudyOver(context: Context) {
        val calendarNow = Calendar.getInstance().time
        val studyEnd = SharedPrefManager.getLong(CONST.PREFERENCES_STUDY_END, 0L)
        val isStudyOver = calendarNow.after(Date(studyEnd))
        if(isStudyOver){
            NotificationHelper.createSurveyNotification(context, SurveryType.SURVEY_END)
        }
    }

    fun generateSessionID(timestamp: Long): String {
        val sessionID = timestamp.toString() + "_" + UUID.randomUUID().toString()
        cachedSessionID = sessionID
        Log.d("xxx", "generate sessionID: $cachedSessionID or $sessionID")
        return sessionID
    }
}
