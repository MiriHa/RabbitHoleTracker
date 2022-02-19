package com.example.trackingapp.sensor.modes

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.activity.esm.ESMIntentionUnlockActivity
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.ScreenState
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.ESMType
import com.example.trackingapp.util.NotificationHelper
import com.example.trackingapp.util.SharedPrefManager

class ScreenStateSensor : AbstractSensor(
    "SCREEN_STATE_SENSOR",
    "ScreenState"
) {
    private var mReceiver: BroadcastReceiver? = null
    private var mContext: Context? = null
    private var screenOffAsked = false

    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!isSensorAvailable) return
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
                        SharedPrefManager.saveBoolean(CONST.PREFERENCES_USER_PRESENT, false)
                        if(!screenOffAsked) {
                            screenOffAsked = true
                            NotificationHelper.dismissESMNotification(context)
                            NotificationHelper.createESMFullScreenNotification(
                                context, notificationManager, ESMType.ESMINTENTIONCOMPLETED,
                                context.getString(R.string.esm_lock_notification_title)
                            )
                        }
                        saveEntry(currentState, time)
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
                        SharedPrefManager.saveBoolean(CONST.PREFERENCES_USER_PRESENT, true)
                        val unlockESMintent = Intent(context, ESMIntentionUnlockActivity::class.java)
                        unlockESMintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        unlockESMintent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        unlockESMintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(unlockESMintent)

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
            LogEvent(
                LogEventName.SCREEN,
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
