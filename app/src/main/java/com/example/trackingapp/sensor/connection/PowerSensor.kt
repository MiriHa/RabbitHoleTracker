package com.example.trackingapp.sensor.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import android.view.View
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.example.trackingapp.models.PowerState
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST

class PowerSensor : AbstractSensor(
    "POWER_SENSOR",
    "Charging"
) {

    private var mReceiver: BroadcastReceiver? = null
    private var mContext: Context? = null

    override fun getSettingsView(context: Context?): View? {
        return null
    }

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)

        mContext = context
        mReceiver = PowerReceiver()
        try {
            context.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        context.registerReceiver(mReceiver, filter)
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext?.unregisterReceiver(mReceiver)
        }
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        val connectionState = isConnected(context)
        val chargeState = getBatteryLevel(context)
        saveEntry(connectionState, chargeState.toString(), timestamp)
    }

    /**
     * Method to retrieve current battery charge level ratio.
     * @param context the application context
     * @return charge level at the ratio of current level : fully charged level
     */
    private fun getBatteryLevel(context: Context): Float {
        //Retrieve Info from sticky Intent
        val batteryIntent = context.applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        var level = -1
        var scale = -1
        try {
            level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        } catch (npe: NullPointerException) {
            Log.e(TAG, "Nullpointer in batteryIntent.getIntExtra()")
        }
        if (level == -1 || scale == -1) {
            Log.e(
                TAG,
                "Battery level or scale couldn't be determined - returning default value of 50%"
            )
            return 50.0f
        }

        //calculate and return the ratio
        return level.toFloat() / scale.toFloat() * 100.0f
    }

    private fun isConnected(context: Context): PowerState {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)

        val intent = context.registerReceiver(null, filter)
        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB) PowerState.CONNECTED else PowerState.DISCONNECTED
    }

    /**
     * Method to save entry to DB.
     * @param type a BootEventType
     * @param charge the charge level
     */
    private fun saveEntry(type: PowerState?, charge: String, timestamp: Long) {
        Event(
            EventName.POWER,
            timestamp,
            type?.name,
            charge
        ).saveToDataBase()
    }

    inner class PowerReceiver : BroadcastReceiver() {
        private var state: PowerState? = null

        override fun onReceive(context: Context, intent: Intent) {
            if (LoggingManager.isDataRecordingActive == false) {
                return
            }
            val timestamp = System.currentTimeMillis()

            //determine current charge level
            val charge = getBatteryLevel(context).toString()

            //determine PowerState
            when (intent.action) {
                Intent.ACTION_POWER_CONNECTED -> {
                    state = PowerState.CONNECTED
                    Log.i(TAG, "Power was connected")
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    state = PowerState.DISCONNECTED
                    Log.i(TAG, "Power was disconnected")
                }
                else -> Log.i(TAG, "Unexpected intent type received")
            }

            //save entry containing state and current charge level
            saveEntry(state, charge, timestamp)
        }

    }
}