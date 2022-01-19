package com.example.trackingapp.sensor.implementation

import android.content.Context
import android.util.Log
import android.view.View
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST

class AccessibilitySensor : AbstractSensor(
    "ACCESIBILITY_SENSOR",
    "accessibility"
) {

    private var mContext: Context? = null

    override fun getSettingsView(context: Context?): View? {
        return null
    }

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        if (!m_isSensorAvailable) return

        val time = System.currentTimeMillis()
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        mContext = context

        /* TODO Service gets started via settings???
        val startIntent = Intent(context, AccessibilityLogService::class.java)
        context.startService(startIntent)

         */

        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
           // val stopIntent = Intent(mContext, AccessibilityLogService::class.java)
            //mContext?.stopService(stopIntent)
        }
    }

}