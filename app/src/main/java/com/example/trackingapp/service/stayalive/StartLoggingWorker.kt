package com.example.trackingapp.service.stayalive

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.service.LoggingService

class StartLoggingWorker(val context: Context, params: WorkerParameters): Worker(context, params) {

    val TAG = "TRACKINGAPP_StartLoggingWorker"

    override fun doWork(): Result {
        Log.d(TAG, "doWork ${this.id}: ServiceRunning: ${LoggingManager.loggingService.isRunning}")
        if(!LoggingManager.loggingService.isRunning){
            Log.d(TAG,"Start LoggingService from LoggingWorker")
            val intent = Intent(this.context, LoggingService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }

    override fun onStopped() {
        Log.d(TAG, "onStopped  ${this.id}")
        super.onStopped()
    }

}