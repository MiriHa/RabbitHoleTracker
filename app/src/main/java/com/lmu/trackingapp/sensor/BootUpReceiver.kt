package com.lmu.trackingapp.sensor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lmu.trackingapp.models.BootEventType
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.service.LoggingManager
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase

class BootUpReceiver : BroadcastReceiver() {

    private val TAG = "BootUpReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "OnRecvice")
        val timestamp = System.currentTimeMillis()
        when {
            Intent.ACTION_SHUTDOWN.equals(intent.action, ignoreCase = true) -> {
                saveEntry(BootEventType.SHUTDOWN, timestamp)
            }
            Intent.ACTION_REBOOT.equals(intent.action, ignoreCase = true) -> saveEntry(BootEventType.REBOOT, timestamp)
            Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.action, ignoreCase = true) ||
                    Intent.ACTION_BOOT_COMPLETED.equals(intent.action, ignoreCase = true) -> {
                saveEntry(BootEventType.BOOTED, timestamp)
                //  Log.d("StartLogging", "Start Logging after Bootup: ${LoggingManager.isDataRecordingActive}")
                // LoggingManager.startServiceViaWorker(context)
                try {
                    if (LoggingManager.isDataRecordingActive) LoggingManager.startLoggingService(context)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
            else -> {
                saveEntry(BootEventType.UNDEFINED, timestamp)
                Log.i(TAG, "Unexpected intent type received")
            }
        }
    }

    private fun saveEntry(type: BootEventType, timestamp: Long) {
        try {
            LogEvent(
                LogEventName.BOOT,
                timestamp,
                type.name
            ).saveToDataBase()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}