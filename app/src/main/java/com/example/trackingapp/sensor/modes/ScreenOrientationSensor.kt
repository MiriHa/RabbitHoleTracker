package com.example.trackingapp.sensor.modes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.util.Log
import android.view.View
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.ScreenOrientationType
import com.example.trackingapp.sensor.AbstractSensor

class ScreenOrientationSensor : AbstractSensor(
    "SCREEN_ORIENTATION_SENSOR",
    "screenOrientation"
) {
    private var m_Receiver: BroadcastReceiver? = null
    private var mContext: Context? = null

    override fun getSettingsView(context: Context?): View? {
        return null
    }

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val t = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
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
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext?.unregisterReceiver(m_Receiver)

        }
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
       //val time = System.currentTimeMillis()
       // val orientationType = getScreenOrientation(context)
       // saveEntry(orientationType, time)
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