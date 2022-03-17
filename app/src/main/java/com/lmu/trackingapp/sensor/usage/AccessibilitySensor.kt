package com.lmu.trackingapp.sensor.usage

import android.content.Context
import android.util.Log
import com.lmu.trackingapp.sensor.AbstractSensor
import com.lmu.trackingapp.util.CONST
import com.lmu.trackingapp.util.PermissionManager

class AccessibilitySensor : AbstractSensor(
    "ACCESIBILITY_SENSOR",
    "Accessibility"
) {

    private var mContext: Context? = null

    override fun isAvailable(context: Context): Boolean {
        return PermissionManager.isAccessibilityServiceEnabled(context)
    }

    override fun start(context: Context) {
        super.start(context)
        if (!isSensorAvailable) return

        val time = System.currentTimeMillis()
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        mContext = context

        /* Service gets started via settings
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