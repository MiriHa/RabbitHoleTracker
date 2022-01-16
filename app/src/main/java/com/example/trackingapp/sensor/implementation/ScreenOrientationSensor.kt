package com.example.trackingapp.sensor.implementation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.util.Log
import android.view.View
import com.example.trackingapp.sensor.AbstractSensor

class ScreenOrientationSensor : AbstractSensor(
    "SCREN_ORIENTATION_SENSOR",
    "screenOrientation"
) {
    private var m_Receiver: BroadcastReceiver? = null
    private var m_context: Context? = null
    var m_WasScreenOn = true

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
        m_context = context
            try {
                if (m_context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                } else if (m_context?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {

                } else if (m_context?.resources?.configuration?.orientation == Configuration.ORIENTATION_UNDEFINED) {

                } else {

                }

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)
        m_Receiver = ScreenReceiver()
        try {
            m_context!!.unregisterReceiver(m_Receiver)
        } catch (e: Exception) {
            //Not Registered
        }
        m_context?.registerReceiver(m_Receiver, filter)
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            m_context?.unregisterReceiver(m_Receiver)

        }
    }

    inner class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val t = System.currentTimeMillis()
            if (isRunning) {
                if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
                    try {
                        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                        } else if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                        } else if (context.resources.configuration.orientation == Configuration.ORIENTATION_UNDEFINED) {

                        } else {

                        }

                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}