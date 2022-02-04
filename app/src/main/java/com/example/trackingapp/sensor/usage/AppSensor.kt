package com.example.trackingapp.sensor.usage

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import java.util.*


class AppSensor : AbstractSensor(
    "APP_SENSOR",
    "App"
) {
    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        //TODO save all usageStats once per hour???
      //  val foregroundtask = getForeGroundTask(context)
      //  saveEntry(foregroundtask, timestamp)
    }

    override fun stop() {
        if (isRunning)
            isRunning = false

    }

    @Deprecated("Deprecated")
    private fun getForegroundApp(context: Context): String? {
        return try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getRunningTasks(1)[0].topActivity!!.packageName
        } catch (e: Exception) {
            null
        }
    }

    private fun getForeGroundTask(context: Context): String? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.runningAppProcesses
        val currentApp = tasks[0].processName

        Log.e("adapter", "Current App in foreground is: $currentApp")
        return currentApp
    }

    private fun saveEntry(foregroundApp: String?, timestamp: Long) {
        LogEvent(
            LogEventName.APPS,
            timestamp,
            foregroundApp
        ).saveToDataBase()
    }


    /**
     * Needs to be done in te background, do only once per hour
    */
    fun fetchLastAppsUsageStats(context: Context) {
        Log.d(TAG, "fetchLastAppsUsageStats()")

        //get UsageEvents since last job run
        /*var startTimestamp: Long = SharedPrefManager.getInstance().getLastAppUsageJobTime()
        if (startTimestamp == 0L) {       // if not set, load the last hour
            startTimestamp = System.currentTimeMillis() - 60 * 60 * 1000
        }

         */
        val startTimestamp = System.currentTimeMillis()
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events = usageStatsManager.queryEvents(
            startTimestamp,
            System.currentTimeMillis()
        )
        val eventList: MutableList<UsageEvents.Event> = ArrayList()

        // if events are younger than last entry from db, add them to eventList
        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)
            eventList.add(event)
        }
        Log.i(
            TAG,
            "Detected ${eventList.size.toLong()} not-logged processes running in the last hour."
        )

        //check every app
        for (event in eventList) {
            val packageManager = context.packageManager
            var appInfo: ApplicationInfo? = try {
                packageManager.getApplicationInfo(event.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            val applicationName =
                (if (appInfo != null) packageManager.getApplicationLabel(appInfo) else "(unknown)") as String
            val packageName = event.packageName
            val eventType: String = getEventName(event.eventType)

            // --- testing ---
            var descr: String? = null
            if ("STANDBY_BUCKET_CHANGED" == eventType) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                     val appStandbyBucket = usageStatsManager.appStandbyBucket
                     descr = "appStandbyBucket: $appStandbyBucket"
                     Log.i(TAG, "standby bucket of app $applicationName is $appStandbyBucket")

                } else {
                     Log.i(TAG, "standby bucket of app not possible, Build Version under P")
                }
            }
            saveEntry(
                context,
                applicationName,
                packageName,
                event.timeStamp,
                eventType,
                descr
            )
        }
    }

    private fun saveEntry(
        context: Context?,
        applicationName: String,
        packageName: String?,
        timestampMillis: Long,
        eventType: String?,
        description: String?
    ) {
        /*val savedActivity: UsageActivity = SQLite.select()
            .from(UsageActivity::class.java)
            .where(UsageActivity_Table.activityName.eq(UsageActivityName.APPS))
            .and(UsageActivity_Table.name.eq(applicationName))
            .and(UsageActivity_Table.timestamp.eq(timestampMillis))
            .querySingle()
        val alreadySaved = if (savedActivity != null) true else false
        if (alreadySaved) {
            Log.i(
                TAG,
                "Last app was also: $applicationName"
            )
            Log.i(
                TAG,
                "Last app timestamp: " + savedActivity.getTimestamp()
                    .toString() + "; Current app timestamp: " + timestampMillis.toString() + "; Diff: " + (timestampMillis - savedActivity.getTimestamp())
            )
        }

         */
            Log.i(TAG, "Logging app $applicationName")
            LogEvent(LogEventName.APPS, timestampMillis,eventType,description, applicationName, packageName).saveToDataBase()

    }

    private fun getEventName(eventType: Int): String {
        var result = ""
        when (eventType) {
            UsageEvents.Event.CONFIGURATION_CHANGE -> result = "CONFIGURATION_CHANGE"
            UsageEvents.Event.NONE -> result = "NONE"
            UsageEvents.Event.SHORTCUT_INVOCATION -> result = "SHORTCUT_INVOCATION"
            UsageEvents.Event.USER_INTERACTION -> result = "USER_INTERACTION"
            UsageEvents.Event.STANDBY_BUCKET_CHANGED -> result = "STANDBY_BUCKET_CHANGED"
            UsageEvents.Event.SCREEN_INTERACTIVE -> result = "SCREEN_INTERACTIVE"
            UsageEvents.Event.SCREEN_NON_INTERACTIVE -> result = "SCREEN_NON_INTERACTIVE"
            else -> {
                var eventName = "UNKNOWN"
                if (eventType == 10) {
                    eventName = "NOTIFICATION_SEEN"
                } else if (eventType == 12) {
                    eventName = "NOTIFICATION_INTERRUPTION"
                }
                result = "$eventType ($eventName)"
                Log.i(
                    TAG,
                    "unknown apps event occurred: $eventType"
                )
            }
        }
        return result
    }

}