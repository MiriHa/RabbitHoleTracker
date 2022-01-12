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
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.sensor.implementation.AppSensor
import com.example.trackingapp.sensor.implementation.PowerSensor
import com.example.trackingapp.sensor.implementation.ScreenOnOffSensor
import com.example.trackingapp.sensor.implementation.WifiSensor
import com.example.trackingapp.service.stayalive.StayAliveReceiver
import com.example.trackingapp.util.CONST


class LoggingService : Service() {
    private val mTAG = "TRACKINGAPP_LOGGING_SERVICE"

    var isRunning: Boolean = false

    lateinit var sensorList: MutableList<AbstractSensor>

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(mTAG,"onCreate")
        sensorList = createSensorList()

        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel( CONST.CHANNEL_ID_LOGGING, CONST.CHANNEL_NAME_ESM_LOGGING, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
        isRunning = true

        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, CONST.CHANNEL_ID_LOGGING)
            .setSmallIcon(R.drawable.ic_logo_placholder)
            .setContentTitle(getString(R.string.logging_notification_service_title))
            .setContentText(getString(R.string.logging_notification_service_descrption))
            .setContentIntent(notificationPendingIntent)

        startForeground(CONST.NOTIFICATION_ID_LOGGING, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       Log.d(mTAG,"onStarCommand")
       /*val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, CONST.CHANNEL_ID_LOGGING)
            .setSmallIcon(R.drawable.ic_logo_placholder)
            .setContentTitle(getString(R.string.logging_notification_service_title))
            .setContentText(getString(R.string.logging_notification_service_descrption))
            .setContentIntent(notificationPendingIntent)
            .build()

        startForeground(1, notification)
        startSensors()*/
        startSensors()
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(mTAG, "onDestroy called")
        isRunning = false
        stopForeground(true)
        stopSensors()

        val boradcasIntent = Intent(this, StayAliveReceiver::class.java)
        sendBroadcast(boradcasIntent)

        super.onDestroy()
    }

    fun stopService(){
        Log.d(mTAG, "Stop Logging Service")
        stopForeground(true)
        stopSelf()
    }

    private fun startSensors(){
        sensorList.let { list ->
            Log.d(mTAG, "size: " + list.size)
            for (sensor in list) {
                if (sensor.isEnabled && sensor.isAvailable(this)) {
                    sensor.start(this)
                    Log.d(mTAG, sensor.sensorName + " turned on")
                }
            }
        }
    }

    private fun stopSensors(){
        sensorList.let { list ->
            for (sensor in list) {
                if (sensor.isRunning) {
                    sensor.stop()
                }
            }
        }
    }

    fun collectSnapShots(){
        sensorList?.let { list ->
            Log.d(mTAG, "size: " + list.size)
            for (sensor in list) {
                if (sensor.isEnabled && sensor.isAvailable(this)) {
                    sensor.saveSnapshot(this)
                    //if(sensor instanceof MyAccelerometerSensor) ((MyAccelerometerSensor)sensor).start(this);
                    //if(sensor instanceof AppSensor) ((AppSensor)sensor).start(this);
                    Log.d(mTAG, sensor.sensorName + " saveSnapshot")
                }
            }
        }
    }

    private fun createSensorList(): MutableList<AbstractSensor>{
        val list = arrayListOf<AbstractSensor>()
        list.add(ScreenOnOffSensor())
        list.add(WifiSensor())
        list.add(PowerSensor())
        list.add(AppSensor())
        return list
    }

    companion object{
        val loggingService = LoggingService()

    }
}