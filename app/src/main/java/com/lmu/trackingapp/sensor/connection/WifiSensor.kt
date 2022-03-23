package com.lmu.trackingapp.sensor.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo.DetailedState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import com.lmu.trackingapp.models.ConnectionType
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.models.WifiConnectionState
import com.lmu.trackingapp.sensor.AbstractSensor
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase

class WifiSensor : AbstractSensor(
    "WIFI_SENSOR",
    "Internet Connection"
) {

    private var mReceiver: BroadcastReceiver? = null
    private var m_context: Context? = null

    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        if (!isSensorAvailable) return
        m_context = context

        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        mReceiver = WifiReceiver()
        try {
            context.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        context.registerReceiver(mReceiver, filter)

        isRunning = true
    }

    override fun stop() {
        if (isRunning)
            isRunning = false
        m_context?.unregisterReceiver(mReceiver)
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        val netWorkType = getCurrentNetworkType(context)
        val wifiName = getWifiName(context)
        if (isWifiEnabled(context)) {
            saveEntry(connectionState = WifiConnectionState.ENABLED, connectionType = netWorkType, wifiName = wifiName, timestamp = timestamp)
        } else {
            saveEntry(connectionState = WifiConnectionState.DISABLED, connectionType = netWorkType, timestamp)
        }
    }

    private fun getCurrentNetworkType(context: Context?): ConnectionType {
        val cm: ConnectivityManager? =
            context?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        val network = cm?.activeNetwork
        val capabilities = cm?.getNetworkCapabilities(network)

        return if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.CONNECTED_WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CONNECTED_MOBILE
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.CONNECTED_ETHERNET
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.CONNECTED_VPN
                else -> ConnectionType.UNKNOWN
            }
        } else ConnectionType.UNKNOWN

    }

    private fun isWifiEnabled(context: Context): Boolean {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }

    private fun getWifiName(context: Context): String {
        val manager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (manager.isWifiEnabled) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                val wifiInfo = manager.connectionInfo
                if (wifiInfo != null) {
                    val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                    if (state == DetailedState.CONNECTED || state == DetailedState.OBTAINING_IPADDR) {
                        return wifiInfo.ssid
                    }
                }
            } else {
                val cm: ConnectivityManager? =
                    context.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                val network = cm?.activeNetwork

                val linkProperties = cm?.getLinkProperties(network)
                return linkProperties?.interfaceName ?: "wifiName"
            }
        }
        return "wifiName"
    }


    private fun saveEntry(
        connectionState: WifiConnectionState,
        connectionType: ConnectionType,
        timestamp: Long
    ) {
        LogEvent(LogEventName.INTERNET, timestamp, connectionState.name, connectionType.name).saveToDataBase()

    }

    private fun saveEntry(
        connectionState: WifiConnectionState,
        connectionType: ConnectionType,
        wifiName: String,
        timestamp: Long
    ) {
        LogEvent(LogEventName.INTERNET, timestamp, connectionState.name, connectionType.name, wifiName).saveToDataBase()
    }


    inner class WifiReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            try {
                val timestamp = System.currentTimeMillis()

                if (intent.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                    //find out if Wifi was enabled or disabled

                    when (intent.getIntExtra(
                        WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN
                    )) {
                        WifiManager.WIFI_STATE_ENABLED -> {
                            val netWorkType = getCurrentNetworkType(context)
                            saveEntry(WifiConnectionState.ENABLED, netWorkType, timestamp)
                        }

                        WifiManager.WIFI_STATE_DISABLED -> {
                            val netWorkType = getCurrentNetworkType(context)
                            saveEntry(WifiConnectionState.DISABLED, netWorkType, timestamp)
                        }

                        else ->                     //ignore cases ENABLING, DISABLING, UNKNOWN
                            Log.i(TAG, "Wifi is enabling, disabling or unknown - ignoring state change")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}
