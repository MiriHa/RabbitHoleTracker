package com.example.trackingapp.sensor

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.BootEventType
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.service.LoggingManager

class BootUpReceiver : BroadcastReceiver() {

    private val TAG = "BootUpReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "Unexpected intent type received")
        if (LoggingManager.isDataRecordingActive == false) {
            return
        } else {
            val timestamp = System.currentTimeMillis()

            when (intent.action) {
                Intent.ACTION_SHUTDOWN -> {
                    saveEntry(BootEventType.SHUTDOWM, timestamp)
                }
                Intent.ACTION_BOOT_COMPLETED -> {
                    saveEntry(BootEventType.BOOTED, timestamp)
                    startLoggingManager(context)
                }
                else -> {
                    Log.i(TAG, "Unexpected intent type received")
                }
            }
        }
    }

    private fun startLoggingManager(context: Context) {
        if (!LoggingManager.isServiceRunning(context)) {
            //TODO -> test if this is not killing the app
            LoggingManager.startLoggingService(context as Activity)
        }
    }

    /**
     * Method to save entry to DB.
     * @param type a BootEventType
     */
    private fun saveEntry(type: BootEventType, timestamp: Long) {
        LogEvent(
            LogEventName.BOOT,
           timestamp,
            type.name
        ).saveToDataBase()
    }
}