package com.example.trackingapp.sensor

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
        Log.i(TAG, "OnRecvice. ${LoggingManager.isDataRecordingActive}")
        //if (!LoggingManager.isDataRecordingActive) {
       //     return
       // } else {
            val timestamp = System.currentTimeMillis()

            when (intent.action) {
                Intent.ACTION_SHUTDOWN -> {
                    saveEntry(BootEventType.SHUTDOWM, timestamp)
                }
                Intent.ACTION_BOOT_COMPLETED -> {
                    saveEntry(BootEventType.BOOTED, timestamp)
                    Log.d("StartLogging", "Start Logging after Bootup: ${LoggingManager.isDataRecordingActive}")
                    startLoggingManager(context)
                }
                else -> {
                    Log.i(TAG, "Unexpected intent type received")
                }
            }
       // }
    }

    private fun startLoggingManager(context: Context) {
        Log.d("StartLogging", "Start Logging after Bootup: ${LoggingManager.isDataRecordingActive}")
        //if (LoggingManager.isDataRecordingActive) {
            LoggingManager.startLoggingService(context)
       // }
    }

    private fun saveEntry(type: BootEventType, timestamp: Long) {
        LogEvent(
            LogEventName.BOOT,
           timestamp,
            type.name
        ).saveToDataBase()
    }
}