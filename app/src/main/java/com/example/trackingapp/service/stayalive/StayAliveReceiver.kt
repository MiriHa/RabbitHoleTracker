package com.example.trackingapp.service.stayalive

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class StayAliveReceiver: BroadcastReceiver() {
    val TAG = "StayAliveReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "onReceive called")
        Toast.makeText(context, "onStayAlive recive", Toast.LENGTH_LONG).show()
        val workManager: WorkManager = WorkManager.getInstance(context)
        val startLoggingRequest = OneTimeWorkRequest.Builder(StartLoggingWorker::class.java).build()
        workManager.enqueue(startLoggingRequest)
    }
}