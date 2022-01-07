package com.example.trackingapp.sensor.implementation

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.View
import androidx.core.app.NotificationManagerCompat
import com.example.trackingapp.R
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.ESMType
import com.example.trackingapp.util.NotificationHelper

class ScreenOnOffSensor : AbstractSensor() {
    private var mReceiver: BroadcastReceiver? = null
    private var m_context: Context? = null
    private var wasScreenOn = true
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
        Log.d("xxx", "StartScreenSensor")
       /* val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isScreenOn
        m_context = context
        try {
            if (isScreenOn) {
                m_OutputStream!!.write("$t,on\n".toByteArray())
            } else {
                m_OutputStream!!.write("$t,off\n".toByteArray())
            }
            m_OutputStream!!.flush()
        } catch (e: Exception) {
            ModelLog.e(TAG, e.toString())
        }*/

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
            m_context!!.unregisterReceiver(mReceiver)
        }
    }


    inner class ScreenReceiver : BroadcastReceiver() {

        private var desc: String? = null
        var screenOn = false

        override fun onReceive(context: Context, intent: Intent) {
            val time = System.currentTimeMillis()
            val currentState: ScreenState = determineScreenState(context, intent)

            Log.d("xxx","ScreenREciver recived: $currentState isRunning: $isRunning")


            if (isRunning) {
                val notificationManager = NotificationManagerCompat.from(context)
                when {
                    currentState == ScreenState.OFF_LOCKED && !screenOffAsked -> {
                        screenOffAsked = true
                        NotificationHelper.createFullScreenNotification(context, notificationManager, ESMType.ESMINTENTIONCOMPLETED,
                            context.getString(R.string.esm_lock_intention_question_1))
                    }
                    currentState == ScreenState.ON_USERPRESENT -> {
                        screenOffAsked = false
                        NotificationHelper.createFullScreenNotification(context, notificationManager, ESMType.ESMINTENTION,
                            context.getString(R.string.esm_unlock_intention_question))
                    }
                    else -> {

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
    }


    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        isRunning = false
        TAG = javaClass.name
        sensorName = "Screen On/Off"
    }
}


enum class ScreenState {
    ON_LOCKED, ON_UNLOCKED, OFF_UNLOCKED, OFF_LOCKED, ON_USERPRESENT, UNKNOWN
}
