package com.lmu.trackingapp.sensor.communication

import android.content.Context
import android.content.Intent
import android.util.Log
import com.lmu.trackingapp.sensor.AbstractSensor
import com.lmu.trackingapp.service.sensorservice.NotificationListener
import com.lmu.trackingapp.util.CONST
import com.lmu.trackingapp.util.PermissionManager

class NotificationSensor: AbstractSensor(
    "NOTIFICATION_SENSOR",
    "Notifications"
) {

    var mContext: Context? = null

    override fun isAvailable(context: Context): Boolean {
        return PermissionManager.isNotificationListenerEnabled(context)
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!isSensorAvailable) return

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