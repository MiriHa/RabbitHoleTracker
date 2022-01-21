package com.example.trackingapp.sensor.modes

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import androidx.core.app.NotificationManagerCompat
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.example.trackingapp.models.ScreenState
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.ESMType
import com.example.trackingapp.util.NotificationHelper

class ScreenStateSensor : AbstractSensor(
    "SCREEN_STATE_SENSOR",
    "ScreenState"
) {
    private var mReceiver: BroadcastReceiver? = null
    private var mContext: Context? = null
    private var screenOffAsked = false

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
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        mContext = context

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = ScreenReceiver()
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
            mContext?.unregisterReceiver(mReceiver)
        }
    }


    inner class ScreenReceiver : BroadcastReceiver() {

        private var desc: String? = null
        var screenOn = false

        override fun onReceive(context: Context, intent: Intent) {
            val time = System.currentTimeMillis()
            val currentState: ScreenState = determineScreenState(context, intent)

            Log.d("TAG","ScreenReceiver: $currentState isRunning: $isRunning")


            if (isRunning) {
                val notificationManager = NotificationManagerCompat.from(context)
                when (currentState) {
                    ScreenState.OFF_LOCKED -> {
                        if(!screenOffAsked) {
                            screenOffAsked = true
                            NotificationHelper.createESMFullScreenNotification(
                                context, notificationManager, ESMType.ESMINTENTIONCOMPLETED,
                                context.getString(R.string.esm_lock_intention_question_1)
                            )
                        }
                        saveEntry(currentState, time)
                        LoggingManager.userPresent = false
                    }
                    ScreenState.OFF_UNLOCKED -> {
                        saveEntry(currentState, time)
                    }
                    ScreenState.ON_LOCKED -> {
                        saveEntry(currentState, time)
                    }
                    ScreenState.ON_UNLOCKED -> {
                        saveEntry(currentState, time)
                    }
                    ScreenState.ON_USERPRESENT -> {
                        screenOffAsked = false
                        LoggingManager.userPresent = true
                        NotificationHelper.createESMFullScreenNotification(context, notificationManager, ESMType.ESMINTENTION,
                            context.getString(R.string.esm_unlock_intention_question))
                        saveEntry(currentState, time)
                    }
                    else -> {
                        saveEntry(ScreenState.UNKNOWN, time)
                    }
                }
            }
        }

        private fun determineScreenState(context: Context, intent: Intent): ScreenState {
            val action = intent.action

            //Goal: Determine Device State
            val state: ScreenState

            //This is dependent on screen on or off and device locked or unlocked
            val isLocked: Boolean

            //Is the screen on?
            if (action == Intent.ACTION_SCREEN_ON) screenOn = true
            if (action == Intent.ACTION_SCREEN_OFF) screenOn = false

            //Is the device locked?
            val keyguardMgr = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            isLocked = keyguardMgr.isKeyguardLocked

            //If locked - is it additionally secured by pw/pin/pattern/..?
            desc = "not applicable"
            desc = if (keyguardMgr.isDeviceSecure) "secured" else "not secured"
            //Now determine current device state
            val userpresent = intent.action == Intent.ACTION_USER_PRESENT
            state = if(userpresent)ScreenState.ON_USERPRESENT else if (screenOn) if (isLocked) ScreenState.ON_LOCKED else ScreenState.ON_UNLOCKED else if (isLocked) ScreenState.OFF_LOCKED else ScreenState.OFF_UNLOCKED

            return state
        }

        private fun saveEntry(type: ScreenState,  timestamp: Long) {
            Event(
                EventName.SCREEN,
               timestamp,
                type.name,
            ).saveToDataBase()
        }
    }

    init {
        isRunning = false
        TAG = javaClass.name
        sensorName = "Screen On/Off"
    }
}
