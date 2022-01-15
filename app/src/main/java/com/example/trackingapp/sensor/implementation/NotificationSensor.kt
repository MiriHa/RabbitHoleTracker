package com.example.trackingapp.sensor.implementation

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.NotificationListener
import com.example.trackingapp.util.CONST

class NotificationSensor: AbstractSensor(
    "TRACKINGAPP_NOTIFICATION_SENSOR",
    "notifications"
) {

    var m_context: Context? = null

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

        m_context = context
        context.startService(Intent(context, NotificationListener::class.java))
        Log.d(TAG, "StartScreenSensor: ${CONST.dateTimeFormat.format(time)}")


        isRunning = true
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        //TODO save

    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            m_context?.stopService(Intent(m_context, NotificationListener::class.java))
        }
    }

}