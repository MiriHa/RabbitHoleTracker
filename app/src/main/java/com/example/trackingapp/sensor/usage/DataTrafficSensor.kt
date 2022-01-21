package com.example.trackingapp.sensor.usage

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.view.View
import com.example.trackingapp.sensor.AbstractSensor

class DataTrafficSensor : AbstractSensor(
    "APP_SENSOR",
    "app"
) {

    override fun getSettingsView(context: Context?): View? {
        return null
    }

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val networkManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
       // val bucket = networkManager.querySummaryForDevice(NetworkStats.Bucket.DEFAULT_NETWORK_ALL)
       // val rxBytes = bucket.rxBytes
       // val txBytes = bucket.txBytes

    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()

    }

    override fun stop() {
        if (isRunning)
            isRunning = false

    }
}