package com.example.trackingapp.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.WindowManager
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
        description: String = "Description"
    ) {
        Log.d(TAG, "Create ESM FullscreenNotification")
        dismissESMNotification(context)

        val destination = when(esmType){
            ESMType.ESMINTENTION -> ESMIntentionUnlockActivity::class.java
            ESMType.ESMINTENTIONCOMPLETED -> ESMIntentionLockActivity::class.java
        }
        val fullScreenIntent = Intent(context, destination)
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, 0)

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

    fun dismissESMNotification(context: Context){
        Log.d(TAG, "Dismiss ESM Notification")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            cancel(CONST.NOTIFICATION_ID_ESM)
        }
    }
}

fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}

sealed class ESMType{
    object ESMINTENTION: ESMType()
    object ESMINTENTIONCOMPLETED : ESMType()
}