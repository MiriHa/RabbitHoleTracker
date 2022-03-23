package com.lmu.trackingapp.service.sensorservice

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.models.metadata.MetaNotification
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase

class NotificationListener : NotificationListenerService() {
    val TAG = "NotificationReceiver"
    var context: Context? = null

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        try {

            Log.d(TAG, "onNotificationPosted()")
            Log.d(TAG, "Id :" + sbn.id + "\t" + "Tickertext: " + sbn.notification.tickerText + "\t" + sbn.packageName)

            //get Notification object
            val notification = sbn.notification
            val packageName = sbn.packageName
            val timestamp = sbn.postTime

            //get category
            val category = notification.category
            Log.d(TAG, "Category: $category")

            //get priority
            val priority = notification.priority
            Log.d(TAG, "Notification extras: " + notification.extras.toString())

            //get title and text
            var title = ""
            var text = ""
            var infoText = ""
            var subText = ""
            if (notification.extras["android.title"] != null) {
                title = notification.extras["android.title"].toString()
            }
            if (notification.extras["android.text"] != null) {
                text = notification.extras["android.text"].toString()
            }
            if (notification.extras["android.infoText"] != null) {
                infoText = notification.extras["android.infoText"].toString()
            }
            if (notification.extras["android.subText"] != null) {
                subText = notification.extras["android.subText"].toString()
            }
            Log.d(TAG, "Title: $title Text: $text InfoText: $infoText")
            //hash every title and text
            if (context?.packageName != packageName) {
                if ("" != title) {
                    title = title.hashCode().toString()
                }
                if ("" != text) {
                    text = text.hashCode().toString()
                }
                if ("" != infoText) {
                    infoText = text.hashCode().toString()
                }
                if ("" != subText) {
                    subText = text.hashCode().toString()
                }
            }

            checkIfNotificationExistsAndSave(
                title,
                timestamp,
                text,
                subtext = subText,
                infoText = infoText,
                priority,
                packageName,
                category,
                NotificationInteraction.NOTIFICATION_POSTED
            )
            Log.d(TAG, "onNotificationPosted done")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    //debug
    private fun getString(actions: Array<Notification.Action>?): String {
        var s = ""
        if (actions != null) {
            for (a in actions) {
                s += "\t " + a.title
            }
        }
        return s
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        try {
            val timestamp = System.currentTimeMillis()
            Log.d(TAG, "onNotificationPosted()")
            Log.d(TAG, "Id :" + sbn.id + "\t" + "Tickertext: " + sbn.notification.tickerText + "\t" + sbn.packageName)

            //get Notification object
            val notification = sbn.notification
            val packageName = sbn.packageName

            //get category
            val category = notification.category
            Log.d(TAG, "Category: $category")

            //get priority
            val priority = notification.priority
            Log.d(TAG, "Notification extras: " + notification.extras.toString())

            //get title and text
            var title = ""
            var text = ""
            var infoText = ""
            var subText = ""
            if (notification.extras["android.title"] != null) {
                title = notification.extras["android.title"].toString()
            }
            if (notification.extras["android.text"] != null) {
                text = notification.extras["android.text"].toString()
            }
            if (notification.extras["android.infoText"] != null) {
                infoText = notification.extras["android.infoText"].toString()
            }
            if (notification.extras["android.subText"] != null) {
                subText = notification.extras["android.subText"].toString()
            }
            Log.d(TAG, "Title: $title Text: $text InfoText: $infoText")
            //hash every title and text
            if (context?.packageName != packageName) {
                if ("" != title) {
                    title = title.hashCode().toString()
                }
                if ("" != text) {
                    text = text.hashCode().toString()
                }
                if ("" != infoText) {
                    infoText = text.hashCode().toString()
                }
                if ("" != subText) {
                    subText = text.hashCode().toString()
                }
            }
            checkIfNotificationExistsAndSave(
                title,
                timestamp,
                text,
                subtext = subText,
                infoText = infoText,
                priority,
                packageName,
                category,
                NotificationInteraction.NOTIFICATION_REMOVED
            )
        } catch (e: Exception) {
            Log.e(TAG, "Notification Removed expetion")
        }
        Log.i(TAG, "Notification Removed")
    }

    override fun onBind(mIntent: Intent): IBinder? {
        val mIBinder = super.onBind(mIntent)
        Log.d(TAG, "onBind()")
        return mIBinder
    }

    override fun onUnbind(mIntent: Intent): Boolean {
        val mOnUnbind = super.onUnbind(mIntent)
        Log.d(TAG, "onUnbind()")
        try {
        } catch (e: Exception) {
            Log.e(TAG, "Error during unbind", e)
        }
        return mOnUnbind
    }

    private fun checkIfNotificationExistsAndSave(
        title: String,
        timestamp: Long,
        text: String,
        subtext: String?,
        infoText: String?,
        priority: Int,
        packageName: String,
        category: String?,
        action: NotificationInteraction
    ) {
        Log.d(TAG, "checkIfNotificationExistsAndSave() $packageName ${packageName != "com.lmu.trackingapp"}")
        if (packageName != "com.lmu.trackingapp") {
            if (category != Notification.CATEGORY_CALL ||
                category != Notification.CATEGORY_PROGRESS ||
                category != Notification.CATEGORY_STOPWATCH ||
                category != Notification.CATEGORY_LOCATION_SHARING ||
                category != Notification.CATEGORY_ALARM
            ) {
                saveEntry(title, timestamp, text, subtext, infoText, priority, packageName = packageName, category, action)
            }
        }
    }

    private fun saveEntry(
        title: String,
        timestamp: Long,
        text: String,
        subtext: String?,
        infoText: String?,
        priority: Int,
        packageName: String,
        category: String?,
        action: NotificationInteraction
    ) {
        val metaNotification = MetaNotification(priority, category, infoText = infoText, subText = subtext, interaction = action.name)
        LogEvent(LogEventName.NOTIFICATION, timestamp, event = title, description = text, packageName = packageName).saveToDataBase(metaNotification)

    }

    enum class NotificationInteraction {
        NOTIFICATION_POSTED,
        NOTIFICATION_REMOVED
    }

}