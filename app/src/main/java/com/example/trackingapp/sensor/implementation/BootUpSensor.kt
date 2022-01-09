package com.example.trackingapp.sensor.implementation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.BootEventType
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.LoggingManager

class BootUpSensor : AbstractSensor(
    "BootUpSensor",
    "Booting"
) {

    private var mReceiver: BroadcastReceiver? = null
    private var m_context: Context? = null

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
        Log.d(TAG, "StartScreenSensor: ${CONST.dateTimeFormat.format(time)}")
        m_context = context

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BOOT_COMPLETED)
        filter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED)
        filter.addAction(Intent.ACTION_SHUTDOWN);
        mReceiver = BootUpReceiver()
        try {
            context.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        context.registerReceiver(mReceiver, filter)
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            m_context!!.unregisterReceiver(mReceiver)
        }
    }

}
   class BootUpReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val timestamp = System.currentTimeMillis()

            when (intent.action) {
                Intent.ACTION_SHUTDOWN -> {
                    if (LoggingManager.isDataRecordingActive) {
                        saveEntry(BootEventType.SHUTDOWM, timestamp)
                    }
                }
                Intent.ACTION_BOOT_COMPLETED -> {
                    if (LoggingManager.isDataRecordingActive) {
                        saveEntry(BootEventType.BOOTED, timestamp)
                    }
                    startLoggingManager(context)
                }
                else -> {
                    Log.i("BootUpReceiver", "Unexpected intent type received")
                }
            }
        }

        private fun startLoggingManager(context: Context) {
            //TODO
            //if (!LoggingManager.serivceIsRunning()) {
            //     LoggingManager.ensureLoggingManagerIsAlive(context)
            // }

        }

        /**
         * Method to save entry to DB.
         * @param type a BootEventType
         */
        private fun saveEntry(type: BootEventType, timestamp: Long) {
            Event(
                EventName.BOOT,
                CONST.dateTimeFormat.format(timestamp),
                type.name
            ).saveToDataBase()
        }
    }