package com.example.trackingapp.sensor.modes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log
import android.view.View
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.RingerMode
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST

class RingerModeSensor: AbstractSensor(
    "RINGER_MODE_SENSOR",
    "RingerMode"
) {

    private var mContext: Context? = null
    private var mReceiver: BroadcastReceiver? = null

   override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")
        mContext = context


        val filter = IntentFilter()
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION)
        mReceiver = RingerModeReceiver()

        try {
            mContext?.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        mContext?.registerReceiver(mReceiver, filter)

        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext?.unregisterReceiver(mReceiver)
        }
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        val ringerMode = getCurrentRingerMode(context)
        saveEntry(ringerMode, timestamp)
    }

    private fun saveEntry(ringerMode: RingerMode, timestamp: Long){
        LogEvent(LogEventName.RINGER_MODE, timestamp, ringerMode.name).saveToDataBase()
    }

    private fun getCurrentRingerMode(context: Context?): RingerMode{
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        return when (audioManager?.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> RingerMode.SILENT_MODE
            AudioManager.RINGER_MODE_VIBRATE -> RingerMode.VIBRATE_MODE
            AudioManager.RINGER_MODE_NORMAL -> RingerMode.NORMAL_MODE
            else -> RingerMode.UNKNOWN
        }
    }



    inner class RingerModeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val time = System.currentTimeMillis()
            if (isRunning) {
                if (intent.action == AudioManager.RINGER_MODE_CHANGED_ACTION) {
                    val ringerMode = getCurrentRingerMode(context)
                    saveEntry(ringerMode, time)
                }
            }
        }
    }
}