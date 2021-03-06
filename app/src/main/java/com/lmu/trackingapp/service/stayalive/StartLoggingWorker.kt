package com.lmu.trackingapp.service.stayalive

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.service.LoggingManager
import com.lmu.trackingapp.service.LoggingService
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase
import com.lmu.trackingapp.util.PermissionManager

class StartLoggingWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    val TAG = "TRACKINGAPP_StartLoggingWorker"

    override fun doWork(): Result {
        Log.d(TAG, "doWork ${this.id}: ServiceRunning: ${LoggingManager.isLoggingActive.value}")
        if (LoggingManager.isDataRecordingActive) {
            Log.d("StartLogging", "isLoggingActive: ${LoggingManager.isLoggingActive.value}")
            if (LoggingManager.isLoggingActive.value == false) {
                try {
                    Log.d(TAG, "Start LoggingService from LoggingWorker")
                    val intent = Intent(this.context, LoggingService::class.java)
                    ContextCompat.startForegroundService(context, intent)
                    LogEvent(
                        LogEventName.ADMIN,
                        System.currentTimeMillis(),
                        "RESTARTED_LOGGING_WORKER",
                    ).saveToDataBase()
                    if (!PermissionManager.isAccessibilityServiceEnabled(this.context)) {
                        val permissionIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        permissionIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        this.context.startActivity(permissionIntent)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }
        return Result.success()
    }

    override fun onStopped() {
        Log.d(TAG, "onStopped  ${this.id}")
        super.onStopped()
    }

}