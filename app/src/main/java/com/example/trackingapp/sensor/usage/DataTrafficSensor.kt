package com.example.trackingapp.sensor.usage

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.os.Build
import android.view.View
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.DataTrafficType
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.SharedPrefManager

class DataTrafficSensor : AbstractSensor(
    "DATA_TRAFFIC_SENSOR",
    "dataTraffic"
) {

    private lateinit var networkManager: NetworkStatsManager
    private val subscriberId = "DataTrafficQuery"

    override fun getSettingsView(context: Context?): View? {
        return null
    }

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        networkManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        var lastTimestamp = SharedPrefManager.getLong(CONST.PREFERENCES_LAST_DATA_TRAFFIC_TIMESTAMP)
        SharedPrefManager.saveLong(CONST.PREFERENCES_LAST_DATA_TRAFFIC_TIMESTAMP, timestamp)

        if(lastTimestamp == 0L) lastTimestamp = timestamp - (500)

         val bucket = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
             networkManager.querySummaryForDevice(NetworkStats.Bucket.DEFAULT_NETWORK_ALL, subscriberId, lastTimestamp, timestamp)
         } else {
             networkManager.querySummaryForDevice(NetworkStats.Bucket.STATE_ALL, subscriberId, lastTimestamp, timestamp)
         }

        val roamingState = userFacingRoamingState(bucket.roaming)
        saveEntry(timestamp, DataTrafficType.BYTES_RECEIVED, bucket.rxBytes, roamingState)
        saveEntry(timestamp, DataTrafficType.BYTES_TRANSMITTED, bucket.txBytes, roamingState)
    }

    override fun stop() {
        if (isRunning)
            isRunning = false

    }

    private fun saveEntry(timestamp: Long, type: DataTrafficType, value: Long, state: String){
        LogEvent(
            LogEventName.DATA_TRAFFIC,
            timestamp = timestamp,
            event = type.name,
            description = value.toString(),
            name = state
        ).saveToDataBase()
    }

    private fun userFacingRoamingState(roamingState: Int): String{
        return when(roamingState){
            //Combined usage across all roaming states. Covers both roaming and non-roaming usage.
            NetworkStats.Bucket.ROAMING_ALL -> "ROAMING_ALL"
            //Usage that occurs on a home, non-roaming network.
            NetworkStats.Bucket.ROAMING_NO -> "ROAMING_NO"
            //Usage that occurs on a roaming network
            NetworkStats.Bucket.ROAMING_YES -> "ROAMING_YES"
            else -> "DEFAULT"
        }
    }
}