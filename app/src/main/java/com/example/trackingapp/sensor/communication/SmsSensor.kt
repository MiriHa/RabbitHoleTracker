package com.example.trackingapp.sensor.communication

import android.content.BroadcastReceiver
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.SmsObserver
import com.example.trackingapp.util.CONST

class SmsSensor: AbstractSensor(
"SMS_SENSOR",
"SMS"
) {
    private var mReceiver: BroadcastReceiver? = null
    private var m_context: Context? = null

    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        try{
            val smsObserver = SmsObserver(null, context);
            context.contentResolver.registerContentObserver(Uri.parse("content://sms/"), true, smsObserver);
            Log.d(TAG, "SMSObserver registered");
        } catch (e: SecurityException ){
            Log.e(TAG,"security exception at sms observer - permission could be denied", e);
        }

        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            m_context?.unregisterReceiver(mReceiver)
        }
    }
}