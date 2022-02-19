package com.example.trackingapp.service.sensorservice

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.metadata.MetaNotification
import com.example.trackingapp.service.LoggingManager

class NotificationListener: NotificationListenerService() {
    val TAG = "NotificationReceiver"
    var context: Context? = null

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (!LoggingManager.isDataRecordingActive) {
            return
        }
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
        var titleHashed = ""
        var textHashed = ""
        //hash every title and text
        if (context?.packageName != packageName) {
            if ("" != title) {
                titleHashed = title.hashCode().toString()
            }
            if ("" != text) {
                textHashed = text.hashCode().toString()
            }
        }

        /*if(notification.actions != null){
            LogHelper.d(TAG, "Notification actions: "+getString(notification.actions));
        }*/
        //TODO hash
        checkIfNotificationExistsAndSave(title, timestamp, "$text, info: $infoText, subText: $subText", priority, packageName, category)

        Log.d(TAG, "onNotificationPosted done")
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
        if (!LoggingManager.isDataRecordingActive) {
            return
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

    private fun checkIfNotificationExistsAndSave(title: String, timestamp: Long, text: String, priority: Int, packageName: String, category: String?) {
        Log.d(TAG, "checkIfNotificationExistsAndSave()")
        if(packageName != "com.example.trackingapp" ||
            category != Notification.CATEGORY_CALL ||
            category != Notification.CATEGORY_PROGRESS ||
            category != Notification.CATEGORY_STOPWATCH ||
            category != Notification.CATEGORY_LOCATION_SHARING ||
            category != Notification.CATEGORY_ALARM){
            saveEntry(title, timestamp, text, priority, packageName, category)
        }
    }

    private fun saveEntry(title: String, timestamp: Long, text: String, priority: Int, packageName: String, category: String?) {
        val metaNotification = MetaNotification(priority, category)
        LogEvent(LogEventName.NOTIFICATION, timestamp, event= title, description= text, packageName = packageName).saveToDataBase(metaNotification)

    }
}