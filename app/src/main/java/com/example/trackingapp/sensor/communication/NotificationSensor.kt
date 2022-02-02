package com.example.trackingapp.sensor.communication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.NotificationListener
import com.example.trackingapp.util.CONST

class NotificationSensor: AbstractSensor(
    "NOTIFICATION_SENSOR",
    "notifications"
) {

    var mContext: Context? = null

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

        mContext = context
        context.startService(Intent(context, NotificationListener::class.java))
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")


        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext?.stopService(Intent(mContext, NotificationListener::class.java))
        }
    }

}