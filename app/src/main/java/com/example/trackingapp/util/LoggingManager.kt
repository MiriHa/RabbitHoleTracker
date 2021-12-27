package com.example.trackingapp.util

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LoggingManager: Service() {

    val isRunning = false


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}