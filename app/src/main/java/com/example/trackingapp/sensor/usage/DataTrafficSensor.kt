package com.example.trackingapp.sensor.usage

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.metadata.MetaDataTraffic
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.SharedPrefManager


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
        SharedPrefManager.init(context)
        lastTimeStamp = System.currentTimeMillis() - 500
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
            //var lastTimestamp = SharedPrefManager.getLong(CONST.PREFERENCES_LAST_DATA_TRAFFIC_TIMESTAMP)
            //SharedPrefManager.saveLong(CONST.PREFERENCES_LAST_DATA_TRAFFIC_TIMESTAMP, timestamp)

            /* if(lastTimestamp == 0L) */
            // 60* 1000 = 1 second?

            val subsriberId = getSubscriberId()
            val wifiBucket = networkManager.querySummaryForDevice(NetworkCapabilities.TRANSPORT_WIFI, null, lastTimeStamp, timestamp)
            val networkBucket = networkManager.querySummaryForDevice(NetworkCapabilities.TRANSPORT_CELLULAR, subsriberId, lastTimeStamp, timestamp)

           // val wifiBucketDetail = networkManager.queryDetails(NetworkCapabilities.TRANSPORT_WIFI, null, lastTimestamp, timestamp)
           // val networkBucketDetails = networkManager.queryDetails(NetworkCapabilities.TRANSPORT_CELLULAR, subsriberId, lastTimestamp, timestamp)
            // java.lang.NullPointerException: Attempt to invoke virtual method 'int android.app.usage.NetworkStats$Bucket.getRoaming()' on a null object reference
            //val roamingState = userFacingRoamingState(bucket.roaming)
            Log.d("DATA_TRAFFIC_SENSOR", "Savesnapshot: between  ${CONST.dateTimeFormat.format(lastTimeStamp)} ${CONST.dateTimeFormat.format(timestamp)}")
            var wifiRX: Long? = null
            var wifiTX: Long? = null
            var mobileRX: Long? = null
            var mobileTX: Long? = null

            if (wifiBucket != null) {
                Log.d("DATA_TRAFFIC_SENSOR", "Savesnapshot wifibucket: ${wifiBucket.rxBytes} ${wifiBucket.txBytes}")
                wifiRX = wifiBucket.rxBytes
                wifiTX = wifiBucket.txBytes
            }
            if (networkBucket != null) {
                Log.d("DATA_TRAFFIC_SENSOR", "Savesnapshot wifibucket: ${networkBucket.rxBytes} ${networkBucket.txBytes}")
                mobileRX = networkBucket.rxBytes
                mobileTX = networkBucket.txBytes
            }

            //TODO substract from bytes bevor to get difference??
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