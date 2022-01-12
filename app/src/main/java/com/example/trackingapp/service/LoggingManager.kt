package com.example.trackingapp.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.trackingapp.service.stayalive.StartLoggingWorker
import com.example.trackingapp.util.CONST
import java.util.concurrent.TimeUnit

object LoggingManager {

    private const val TAG = "TRACKINGAPP_LOGGING_MANAGER"

    val loggingService: LoggingService = LoggingService()

    val isDataRecordingActive: Boolean
        get() = true //TODO save in preferences?


    fun startLoggingService(context: Context) {
        if (!loggingService.isRunning) {
            Log.d(TAG, "startService called")
            val t= LoggingService()
            val serviceIntent = Intent(context, LoggingService::class.java )
            ContextCompat.startForegroundService(context, serviceIntent)
            //context.startService(serviceIntent)
            startServiceViaWorker(context)

           // if (!LoggingManager.loggingService(context, LoggingManager::class.java)) {
          //      val loggingIntent = Intent(context, LoggingManager::class.java)
          //  context.startService(loggingIntent)


            /*val pendingIntent: PendingIntent = MainActivity.getPendingIntent(context)
            val m_AlarmInterval = (60 * 1000).toLong()
            context.getSystemService(Context.ALARM_SERVICE).setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + m_AlarmInterval,
                m_AlarmInterval,
                pendingIntent
            )
            val sp: SharedPreferences =
                context.getSharedPreferences(CONST.SP_LOG_EVERYTHING, Activity.MODE_PRIVATE)
            sp.edit().putBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, true).apply()*/
        }
    }

    fun stopLoggingService() {
        if (loggingService.isRunning) {
            Log.d(TAG, "stopService called")
            loggingService.stopService()
        }
    }

    private fun startServiceViaWorker(context: Context) {
        Log.d(TAG, "startServiceViaWorker called")
        val UNIQUE_WORK_NAME = "StartMyServiceViaWorker"
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
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}