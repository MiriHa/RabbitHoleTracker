package com.lmu.trackingapp.sensor.usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.sensor.AbstractSensor
import com.lmu.trackingapp.util.CONST
import com.lmu.trackingapp.util.PermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UsageStatsSensor : AbstractSensor(
    "USAGE_STATS_SENSOR",
    "Usage Stats"
) {

    private lateinit var usageStatsManager: UsageStatsManager
    private var mContext: Context? = null
    private var lastTimeStamp: Long = 0L

    override fun isAvailable(context: Context): Boolean {
        return PermissionManager.isUsageInformationPermissionEnabled(context)
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")
        mContext = context
        usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        lastTimeStamp = time - CONST.LOGGING_FREQUENCY
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
        }
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        CoroutineScope(Dispatchers.IO).launch {
            getEvents()
        }
    }

    private fun getEvents() {
        val timestamp = System.currentTimeMillis()
        Log.d(TAG, "Savesnapshot: between  ${CONST.dateTimeFormat.format(lastTimeStamp)} ${CONST.dateTimeFormat.format(timestamp)}")
        if (lastTimeStamp == 0L) lastTimeStamp = System.currentTimeMillis() - CONST.LOGGING_FREQUENCY
        val events = usageStatsManager.queryEvents(lastTimeStamp, timestamp)
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            Log.d(TAG, "addEvent: ${event.eventType} ${event.className} ${event.packageName}")
            saveEntry(event)
        }
        lastTimeStamp = timestamp
        Log.d(TAG, "finisehd")
    }

    private fun saveEntry(event: UsageEvents.Event) {
        val pm = mContext?.packageManager
        val appInfo: ApplicationInfo? = try {
            pm?.getApplicationInfo(event.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        val applicationName =
            (if (appInfo != null) pm?.getApplicationLabel(appInfo) else "(unknown)") as String

        LogEvent(
            LogEventName.USAGE_EVENTS,
            timestamp = event.timeStamp,
            event = userFacingEventType(event.eventType),
            name = event.className,
            description = applicationName,
            packageName = event.packageName
        ).saveToDataBase()
    }

    private fun userFacingEventType(eventType: Int): String {
        return when (eventType) {
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
            else -> {
                var eventName = "UNKNOWN"
                if (eventType == 10) {
                    eventName = "NOTIFICATION_SEEN"
                } else if (eventType == 12) {
                    eventName = "NOTIFICATION_INTERRUPTION"
                }
                return "$eventType ($eventName)"
            }
        }
    }
}