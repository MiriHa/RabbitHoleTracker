package com.example.trackingapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.trackingapp.R
import com.example.trackingapp.activity.MainActivity
import com.example.trackingapp.service.stayalive.StayAliveReceiver
import com.example.trackingapp.util.CONST
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoggingService : Service() {
    private val mTAG = "TRACKINGAPP_LOGGING_SERVICE"

    private val scope = MainScope()
    var job: Job? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(mTAG, "onCreate")

        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(CONST.CHANNEL_ID_LOGGING, CONST.CHANNEL_NAME_ESM_LOGGING, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        LoggingManager._isLoggingActive.value = true

        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, CONST.CHANNEL_ID_LOGGING)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(getString(R.string.logging_notification_service_title))
            .setContentText(getString(R.string.logging_notification_service_description))
            .setContentIntent(notificationPendingIntent)

        startForeground(CONST.NOTIFICATION_ID_LOGGING, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(mTAG, "onStarCommand: Start Foreground Service")
        startSensors()
        startLoggingUpdates()

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(mTAG, "onDestroy called: Datarecording active: ${LoggingManager.isDataRecordingActive}")
        LoggingManager._isLoggingActive.value = false
        stopForeground(true)
        stopSensors()
        stopLoggingUpdates()

        // Restart LoggingService if it is killed
        if (LoggingManager.isDataRecordingActive) {
            val broadcastIntent = Intent(this, StayAliveReceiver::class.java)
            sendBroadcast(broadcastIntent)
        }

        super.onDestroy()
    }

    private fun collectSnapShots() {
        LoggingManager.sensorList?.let { list ->
            Log.d(mTAG, "size: " + list.size)
            for (sensor in list) {
                if (sensor.isEnabled && sensor.isAvailable(this)) {
                    sensor.saveSnapshot(this)
                    Log.d(mTAG, sensor.sensorName + " saveSnapshot")
                }
            }
        }
    }

    private fun startLoggingUpdates() {
        Log.d(mTAG, "startSensorThread")
        stopLoggingUpdates()
        job = scope.launch {
            while (true) {
                if(LoggingManager.userPresent/* && LoggingManager.isDataRecordingActive*/)
                    collectSnapShots() // the function that should be ran every second
                delay(CONST.LOGGING_FREQUENCY)
            }
        }
    }

    private fun stopLoggingUpdates() {
        job?.cancel()
        job = null
    }

    private fun startSensors() {
        LoggingManager.sensorList?.let { list ->
            Log.d(mTAG, "size: " + list.size)
            for (sensor in list) {
                if (sensor.isEnabled && sensor.isAvailable(this)) {
                    sensor.start(this)
                    Log.d(mTAG, sensor.sensorName + " turned on")
                }
            }
        }
    }

    private fun stopSensors() {
        LoggingManager.sensorList?.let { list ->
            for (sensor in list) {
                if (sensor.isRunning) {
                    sensor.stop()
                }
            }
        }
    }
}
