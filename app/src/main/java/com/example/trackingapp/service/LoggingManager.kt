package com.example.trackingapp.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.trackingapp.service.stayalive.StartLoggingWorker
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.SharePrefManager
import java.util.concurrent.TimeUnit


object LoggingManager {

    private const val TAG = "TRACKINGAPP_LOGGING_MANAGER"

    var userPresent = false

    fun isServiceRunning(context: Context): Boolean {

        return SharePrefManager.getBoolean(context, CONST.PREFERENCES_IS_LOGGING_SERVICE_RUNNING)
    }

    var isDataRecordingActive: Boolean? = null


    fun startLoggingService(context: Context) {
        if (!isServiceRunning(context)) {
            Log.d(TAG, "startService called")
            Toast.makeText(context, "Start LoggingService", Toast.LENGTH_LONG).show()
            val serviceIntent = Intent(context, LoggingService::class.java )
            ContextCompat.startForegroundService(context, serviceIntent)
            startServiceViaWorker(context)
        }
    }

    fun stopLoggingService(context: Context) {
        //if (isServiceRunning(context)) {
        Log.d(TAG, "stopService called")
        LoggingManager.isDataRecordingActive = false
        Toast.makeText(context, "Stop LoggingService", Toast.LENGTH_LONG).show()
        SharePrefManager.saveBoolean(context, CONST.PREFERENCES_IS_LOGGING_SERVICE_RUNNING, false)
        val stopIntent = Intent(context, LoggingService::class.java )
        context.applicationContext.stopService(stopIntent)
        cancleServiceViaWorker(context)
        // }
    }


    fun scheduleStartRecordingAlarmManager(context: Context) {
        val startTime = System.currentTimeMillis()
        Log.d(TAG, "Start Recording AlarmManager $startTime")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(context)
        val m_AlarmInterval = CONST.LOGGING_INTERVAL.toLong()
        //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, startTime, pendingIntent)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + m_AlarmInterval, m_AlarmInterval, pendingIntent)

    }

    fun cancleRecordingAlarmManager(context: Context){
        Log.d(TAG, "Cancel Recording AlarmManager")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(context)
        alarmManager.cancel(pendingIntent)
    }

    private fun getPendingIntent(context: Context): PendingIntent? {
        val alarmIntent = Intent(context.applicationContext, LoggingService::class.java)
        return PendingIntent.getService(context, 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
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

    fun cancleServiceViaWorker(context: Context){
        WorkManager.getInstance(context).cancelAllWorkByTag(CONST.UNIQUE_WORK_NAME)
    }
}
