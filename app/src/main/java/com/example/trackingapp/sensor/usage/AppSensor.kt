package com.example.trackingapp.sensor.usage

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor


class AppSensor : AbstractSensor(
    "APP_SENSOR",
    "App"
) {
    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        //TODO save all usageStats once per hour???
        val foregroundtask = getForeGroundTask(context)
        saveEntry(foregroundtask, timestamp)
    }

    override fun stop() {
        if (isRunning)
            isRunning = false

    }

    @Deprecated("Deprecated")
    private fun getForegroundApp(context: Context): String? {
        return try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getRunningTasks(1)[0].topActivity!!.packageName
        } catch (e: Exception) {
            null
        }
    }

    private fun getForeGroundTask(context: Context): String? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.runningAppProcesses
        val currentApp = tasks[0].processName

        Log.e("adapter", "Current App in foreground is: $currentApp")
        return currentApp
    }

    private fun saveEntry(foregroundApp: String?, timestamp: Long) {
        LogEvent(
            LogEventName.APPS,
            timestamp,
            foregroundApp
        ).saveToDataBase()
    }

}