package com.lmu.trackingapp.sensor.modes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.util.Log
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.models.ScreenOrientationType
import com.lmu.trackingapp.sensor.AbstractSensor

class ScreenOrientationSensor : AbstractSensor(
    "SCREEN_ORIENTATION_SENSOR",
    "Screen Orientation"
) {
    private var m_Receiver: BroadcastReceiver? = null
    private var mContext: Context? = null

    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        if (!isSensorAvailable) return
        mContext = context

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)
        m_Receiver = ScreenOrientationReceiver()

        try {
            mContext?.unregisterReceiver(m_Receiver)
        } catch (e: Exception) {
            //Not Registered
        }
        mContext?.registerReceiver(m_Receiver, filter)

        val orientationType = getScreenOrientation(context)
        saveEntry(orientationType, System.currentTimeMillis())

        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext?.unregisterReceiver(m_Receiver)

        }
    }

    fun saveEntry(orientation: ScreenOrientationType, timestamp: Long) {
        LogEvent(LogEventName.SCREEN_ORIENTATION, timestamp, orientation.name).saveToDataBase()
    }

    fun getScreenOrientation(context: Context?): ScreenOrientationType {
        var orientationType: ScreenOrientationType = ScreenOrientationType.SCREEN_ORIENTATION_UNDEFINED
        try {
            orientationType = when {
                (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) ->
                    ScreenOrientationType.SCREEN_ORIENTATION_LANDSCAPE

                (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) ->
                    ScreenOrientationType.SCREEN_ORIENTATION_PORTRAIT

                (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_UNDEFINED) ->
                    ScreenOrientationType.SCREEN_ORIENTATION_UNDEFINED

                else -> ScreenOrientationType.SCREEN_ORIENTATION_UNDEFINED
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        return orientationType
    }

    inner class ScreenOrientationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val time = System.currentTimeMillis()
            if (isRunning) {
                if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
                    val orientationType = getScreenOrientation(context)
                    saveEntry(orientationType, time)
                }
            }
        }
    }

}