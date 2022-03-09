package com.example.trackingapp.sensor.usage

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.example.trackingapp.util.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.metadata.MetaDataTraffic
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST


class DataTrafficSensor : AbstractSensor(
    "DATA_TRAFFIC_SENSOR",
    "Data Traffic"
) {

    private lateinit var networkManager: NetworkStatsManager
    private lateinit var telephonyManager: TelephonyManager

    private var lastTimeStamp : Long = 0

   override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        lastTimeStamp = System.currentTimeMillis() - CONST.LOGGING_FREQUENCY

        networkManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }
        isRunning = true

    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()

        try {
           val subsriberId = getSubscriberId()

            val wifiBucket = networkManager.querySummaryForDevice(NetworkCapabilities.TRANSPORT_WIFI, null, lastTimeStamp, timestamp)
            val mobileBucket = networkManager.querySummaryForDevice(NetworkCapabilities.TRANSPORT_CELLULAR, subsriberId, lastTimeStamp, timestamp)

            var wifiRX: Long? = null
            var wifiTX: Long? = null
            var mobileRX: Long? = null
            var mobileTX: Long? = null

            if (wifiBucket != null) {
                Log.d("DATA_TRAFFIC_SENSOR", "Savesnapshot wifibucket: ${wifiBucket.rxBytes} ${wifiBucket.txBytes}")
                wifiRX = wifiBucket.rxBytes
                wifiTX = wifiBucket.txBytes
            }
            if (mobileBucket != null) {
                Log.d("DATA_TRAFFIC_SENSOR", "Savesnapshot wifibucket: ${mobileBucket.rxBytes} ${mobileBucket.txBytes}")
                mobileRX = mobileBucket.rxBytes
                mobileTX = mobileBucket.txBytes
            }

            saveEntry(timestamp, mobileRX = mobileRX, mobileTX = mobileTX, wifiRX = wifiRX, wifiTX = wifiTX)
            lastTimeStamp = timestamp

        } catch (e: Exception) {
            Log.e("DATA_TRAFFIC_SENSOR", "Couldn't save Data Traffic snapshop.", e)
        }
    }

    override fun stop() {
        if (isRunning)
            isRunning = false

    }

    private fun saveEntry(timestamp: Long, mobileRX: Long?, mobileTX: Long?, wifiRX: Long?, wifiTX: Long?) {
        val dataTrafficMeta = MetaDataTraffic(
            WIFI_BYTES_TRANSMITTED = wifiTX,
            WIFI_BYTES_RECEIVED = wifiRX,
            MOBILE_BYTES_TRANSMITTED = mobileTX,
            MOBILE_BYTES_RECEIVED = mobileRX
        )

        LogEvent(
            LogEventName.DATA_TRAFFIC,
            timestamp = timestamp,
        ).saveToDataBase(dataTrafficMeta)
    }

    private fun userFacingRoamingState(roamingState: Int): String {
        return when (roamingState) {
            //Combined usage across all roaming states. Covers both roaming and non-roaming usage.
            NetworkStats.Bucket.ROAMING_ALL -> "ROAMING_ALL"
            //Usage that occurs on a home, non-roaming network.
            NetworkStats.Bucket.ROAMING_NO -> "ROAMING_NO"
            //Usage that occurs on a roaming network
            NetworkStats.Bucket.ROAMING_YES -> "ROAMING_YES"
            else -> "DEFAULT"
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSubscriberId(): String? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                telephonyManager.subscriberId //MissingPermission
            } catch (e: SecurityException) {
                null
            }
        } else {
            null
        }
    }
}