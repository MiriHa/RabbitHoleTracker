package com.example.trackingapp.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.trackingapp.R
import com.example.trackingapp.activity.esm.ESMIntentionLockActivity
import com.example.trackingapp.activity.esm.ESMIntentionUnlockActivity


object NotificationHelper {

    val TAG ="TRACKINGAPP_NOTIFICATION_HELPER"

    fun NotificationManagerCompat.createESMNotificationChannel() {

        val channel = NotificationChannel(
            CONST.CHANNEL_ID_ESM, CONST.CHANNEL_NAME_ESM, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        createNotificationChannel(channel)
    }

    fun createESMFullScreenNotification(
        context: Context,
        notificationManager: NotificationManagerCompat,
        esmType: ESMType,
        title: String = "Title",
        description: String = "Description",
        sessionID: String? ,
    ) {
        Log.d(TAG, "Create ESM FullscreenNotification")
        dismissESMNotification(context)

        val destination = when(esmType){
            ESMType.ESMINTENTION -> ESMIntentionUnlockActivity::class.java
            ESMType.ESMINTENTIONCOMPLETED -> ESMIntentionLockActivity::class.java
        }
        val fullScreenIntent = Intent(context, destination)
        fullScreenIntent.putExtra(CONST.ESM_SESSION_ID_MESSAGE, sessionID)
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, CONST.CHANNEL_ID_ESM)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(description)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        with(notificationManager){
            createESMNotificationChannel()
            val notification = builder.build()
            notify(CONST.NOTIFICATION_ID_ESM, notification)
        }
    }

    fun openESMUnlockActivity(context: Context, sessionID: String?){
        val unlockESMIntent = Intent(context, ESMIntentionUnlockActivity::class.java)
        unlockESMIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        unlockESMIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        unlockESMIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        unlockESMIntent.putExtra(CONST.ESM_SESSION_ID_MESSAGE, sessionID)
        context.startActivity(unlockESMIntent)
    }

    fun dismissESMNotification(context: Context){
        Log.d(TAG, "Dismiss ESM Notification")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            cancel(CONST.NOTIFICATION_ID_ESM)
        }
    }
}

fun Activity.turnScreenOnAndKeyguardOff() {
    setShowWhenLocked(true)
    setTurnScreenOn(true)

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        val lock = newKeyguardLock("LOCK")
        requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    setShowWhenLocked(false)
    setTurnScreenOn(false)
}

sealed class ESMType{
    object ESMINTENTION: ESMType()
    object ESMINTENTIONCOMPLETED : ESMType()
}