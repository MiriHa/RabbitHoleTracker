package com.example.trackingapp.sensor.implementation

import android.content.Context
import android.content.Intent
import android.view.View
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.AccessibilityLogService

class AccessibilitySensor : AbstractSensor(
    "ACCESIBILITYSENSOR",
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
        mContext = context

        val startIntent = Intent(mContext, AccessibilityLogService::class.java)
        context.startService(startIntent)

        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            val stopIntent = Intent(mContext, AccessibilityLogService::class.java)
            mContext?.stopService(stopIntent)
        }
    }

}