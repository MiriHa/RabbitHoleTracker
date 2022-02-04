package com.example.trackingapp.sensor.usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST


class UsageStatsSensor : AbstractSensor(
    "USAGE_STATS_SENSOR",
    "Usage Stats"
) {

    private lateinit var usageStatsManager: UsageStatsManager
    private var mContext: Context? = null
    private var lastTimeStamp : Long = 0

    override fun isAvailable(context: Context?): Boolean {
        //TODO check for permission?
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")
        mContext = context
        usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        lastTimeStamp = time - 500
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
        }
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
//        val timestamp = System.currentTimeMillis()
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val eventStats =
                usageStatsManager.queryEventStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis() - 1000 * 3600 * 24, System.currentTimeMillis())
        }*/
       // CoroutineScope(Dispatchers.IO).launch {
            getEvents()
        //}
    }

    private fun getEvents(){
        val timestamp = System.currentTimeMillis()
        Log.d(TAG, "Savesnapshot: between  ${CONST.dateTimeFormat.format(lastTimeStamp)} ${CONST.dateTimeFormat.format(timestamp)}")
        val events = usageStatsManager.queryEvents(lastTimeStamp, timestamp)
        val event = UsageEvents.Event()
        while (events.hasNextEvent()){
            events.getNextEvent(event)
            Log.d(TAG, "addEvent: ${event.eventType} ${event.className} ${event.packageName}")
            saveEntry(event)
        }
        lastTimeStamp = timestamp
        Log.d(TAG, "finisehd")
    }

    private fun saveEntry(event: UsageEvents.Event) {
        LogEvent(
            LogEventName.USAGE_EVENTS,
            timestamp = event.timeStamp,
            event = userFacingEventType(event.eventType),
            name = event.className,
            description = event.packageName,
        ).saveToDataBase()
    }

    private fun userFacingEventType(eventType: Int): String{
        return when(eventType){
            UsageEvents.Event.ACTIVITY_PAUSED -> "ACTIVITY_PAUSED"
            UsageEvents.Event.ACTIVITY_RESUMED -> "ACTIVITY_RESUMED"
            UsageEvents.Event.ACTIVITY_STOPPED -> "ACTIVITY_STOPPED"
            UsageEvents.Event.CONFIGURATION_CHANGE -> "CONFIGURATION_CHANGE"
            UsageEvents.Event.DEVICE_SHUTDOWN -> "DEVICE_SHUTDOWN"
            UsageEvents.Event.DEVICE_STARTUP -> "DEVICE_STARTUP"
            UsageEvents.Event.FOREGROUND_SERVICE_START -> "FOREGROUND_SERVICE_START"
            UsageEvents.Event.FOREGROUND_SERVICE_STOP -> "FOREGROUND_SERVICE_STOP"
            UsageEvents.Event.KEYGUARD_HIDDEN -> "KEYGUARD_HIDDEN"
            UsageEvents.Event.KEYGUARD_SHOWN -> "KEYGUARD_SHOWN"
            UsageEvents.Event.SCREEN_INTERACTIVE -> "SCREEN_INTERACTIVE"
            UsageEvents.Event.SCREEN_NON_INTERACTIVE -> "SCREEN_NON_INTERACTIVE"
            UsageEvents.Event.SHORTCUT_INVOCATION -> "SHORTCUT_INVOCATION"
            UsageEvents.Event.STANDBY_BUCKET_CHANGED -> "STANDBY_BUCKET_CHANGED"
            UsageEvents.Event.USER_INTERACTION -> "USER_INTERACTION"
            UsageEvents.Event.NONE -> "NONE"
            else -> "DEFAULT"
        }
    }
}