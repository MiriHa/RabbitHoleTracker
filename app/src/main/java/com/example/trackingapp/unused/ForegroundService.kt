package com.example.trackingapp.unused

import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.trackingapp.R
import com.example.trackingapp.activity.MainActivity

abstract class ForegroundService : Service() {
    var TAG = javaClass.name
    private var m_wakeLock: PowerManager.WakeLock? = null
    override fun onCreate() {
        super.onCreate()
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(
            applicationContext
        ) //.setSmallIcon(R.drawable.ic_launcher)
            .setTicker(getText(R.string.notif_ticker))
            .setContentTitle(getText(R.string.notif_title))
            .setContentText(getText(R.string.notif_text))
            .setContentIntent(pendingIntent).setAutoCancel(true)
            .setOngoing(true).setContentInfo("")
        startForeground() //startForeground(42, notification.build());
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        m_wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
        val m_InProgress = false
    }

    private fun startForeground() {
        var channelId = ""
        channelId = createNotificationChannel("my_service", "My Background Service")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("LogEverything")
            .setContentText("App is running in background") //.setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        return channelId
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, newConfig.toString())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (!m_wakeLock!!.isHeld) {
            m_wakeLock!!.acquire()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service Stopped")
        if (m_wakeLock!!.isHeld) {
            m_wakeLock!!.release()
        }
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}