package com.lmu.trackingapp.sensor.modes

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.lmu.trackingapp.R
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.models.ScreenState
import com.lmu.trackingapp.sensor.AbstractSensor
import com.lmu.trackingapp.service.LoggingManager
import com.lmu.trackingapp.util.CONST
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase
import com.lmu.trackingapp.util.ESMType
import com.lmu.trackingapp.util.NotificationHelper
import com.lmu.trackingapp.util.SharedPrefManager

class ScreenStateSensor : AbstractSensor(
    "SCREEN_STATE_SENSOR",
    "ScreenState"
) {
    private var mReceiver: BroadcastReceiver? = null
    private var mContext: Context? = null
    private var screenOffAskedCounter = 0

    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        mContext = context
        esmAnswered = false

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        filter.addAction(CONST.ESM_ANSWERED)
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

    companion object {
        var isDeviceSecure = false
        var esmAnswered = false
        var currentSessionID: String = ""
        var lastSessionStarted = 0L
        var lastESMShown = 0L
    }


    inner class ScreenReceiver : BroadcastReceiver() {

        private var desc: String? = null
        var screenOn = false

        override fun onReceive(context: Context, intent: Intent) {
            val time = System.currentTimeMillis()
            val currentState: ScreenState = determineScreenState(context, intent)

            Log.d(TAG, "ScreenReceiver: $currentState isRunning: $isRunning")

            if (isRunning) {
                when {
                    intent.action == CONST.ESM_ANSWERED -> {
                        esmAnswered = intent.getBooleanExtra(CONST.ESM_ANSWERED_MESSAGE, true)
                        lastESMShown = time
                    }
                    currentState == ScreenState.OFF_LOCKED -> {
                        saveEntry(currentState, time)
                        lastESMShown = time
                        onPhoneLock(currentState, time, context)
                    }
                    currentState == ScreenState.OFF_UNLOCKED -> {
                        saveEntry(currentState, time)
                        onPhoneLock(currentState, time, context)
                    }
                    currentState == ScreenState.ON_LOCKED -> {
                        saveEntry(currentState, time)
                    }
                    currentState == ScreenState.ON_UNLOCKED -> {
                        saveEntry(currentState, time)
                    }
                    currentState == ScreenState.ON_USERPRESENT -> {
                        saveEntry(currentState, time)
                        onPhoneUnLock(time, context)
                    }
                    else -> {
                        saveEntry(ScreenState.UNKNOWN, time)
                    }
                }
            }
        }

        private fun onPhoneLock(screenState: ScreenState, time: Long, context: Context) {
            val notificationManager = NotificationManagerCompat.from(context)
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_USER_PRESENT, false)
            if (screenOffAskedCounter <= CONST.ESM_LOCK_ASK_COUNT
                && System.currentTimeMillis() - lastSessionStarted > CONST.ESM_SESSION_TIMEOUT
                && !esmAnswered
            ) {
                screenOffAskedCounter += 1
                lastESMShown = time

                if(screenState == ScreenState.OFF_UNLOCKED && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
                    NotificationHelper.openESMActivity(context, currentSessionID, ESMType.ESM_LOCK)
                }
                NotificationHelper.dismissESMNotification(context)
                NotificationHelper.createESMFullScreenNotification(
                    context, notificationManager, ESMType.ESM_LOCK,
                    context.getString(R.string.esm_lock_notification_title),
                    context.getString(R.string.esm_lock_notification_description),
                    sessionID = LoggingManager.currentSessionID//currentSessionID
                )
            }
        }

        private fun onPhoneUnLock(time: Long, context: Context) {
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_USER_PRESENT, true)
            currentSessionID = LoggingManager.generateSessionID(time)
            SharedPrefManager.saveCurrentSessionID(currentSessionID)
            lastSessionStarted = System.currentTimeMillis()
            if (time - lastESMShown > CONST.ESM_LOCK_TIMEOUT) {
                val notificationManager = NotificationManagerCompat.from(context)
                screenOffAskedCounter = 0
                esmAnswered = false
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    NotificationHelper.openESMActivity(context, currentSessionID, ESMType.ESM_UNLOCK)
                }
                NotificationHelper.dismissESMNotification(context)
                NotificationHelper.createESMFullScreenNotification(
                    context, notificationManager, ESMType.ESM_UNLOCK,
                    context.getString(R.string.esm_unlock_notification_title),
                    context.getString(R.string.esm_unlock_notification_description),
                    sessionID = LoggingManager.currentSessionID
                )
            }
        }

        private fun determineScreenState(context: Context, intent: Intent): ScreenState {
            val action = intent.action

            //Is the screen on?
            if (action == Intent.ACTION_SCREEN_ON) screenOn = true
            if (action == Intent.ACTION_SCREEN_OFF) screenOn = false

            //Is the device locked? This is dependent on screen on or off and device locked or unlocked
            val keyguardMgr = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val isLocked: Boolean = keyguardMgr.isKeyguardLocked

            //If locked - is it additionally secured by pw/pin/pattern/..?
            isDeviceSecure = keyguardMgr.isDeviceSecure //|| keyguardMgr.isKeyguardSecure

            //Is user present?
            val userPresent = intent.action == Intent.ACTION_USER_PRESENT

            val state: ScreenState = when {
                userPresent -> ScreenState.ON_USERPRESENT
                screenOn -> {
                    if (isLocked) ScreenState.ON_LOCKED else ScreenState.ON_UNLOCKED
                }
                !screenOn -> {
                    if (isLocked) ScreenState.OFF_LOCKED else ScreenState.OFF_UNLOCKED
                }
                else -> {
                    ScreenState.UNKNOWN
                }
            }

            Log.d(TAG, "determine screenstate: locked:$isLocked screenOn:$screenOn state:$state desc:$desc")
            return state
        }

        private fun saveEntry(type: ScreenState, timestamp: Long) {
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
