package com.lmu.trackingapp.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lmu.trackingapp.R
import com.lmu.trackingapp.activity.esm.ESMIntentionLockActivity
import com.lmu.trackingapp.activity.esm.ESMIntentionUnlockActivity


object NotificationHelper {

    const val TAG = "TRACKINGAPP_NOTIFICATION_HELPER"

    private fun NotificationManagerCompat.createESMNotificationChannel() {

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
        sessionID: String?,
    ) {
        Log.d(TAG, "Create ESM FullscreenNotification")
        dismissESMNotification(context)

        val destination = when (esmType) {
            ESMType.ESM_UNLOCK -> ESMIntentionUnlockActivity::class.java
            ESMType.ESM_LOCK -> ESMIntentionLockActivity::class.java
        }
        val fullScreenIntent = Intent(context, destination)
        fullScreenIntent.putExtra(CONST.ESM_SESSION_ID_MESSAGE, sessionID)
        fullScreenIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, CONST.CHANNEL_ID_ESM)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(description)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        with(notificationManager) {
            createESMNotificationChannel()
            val notification = builder.build()
            notify(CONST.NOTIFICATION_ID_ESM, notification)
        }
    }

    fun openESMActivity(context: Context, sessionID: String?, esmType: ESMType) {
        val destination = when (esmType) {
            ESMType.ESM_UNLOCK -> ESMIntentionUnlockActivity::class.java
            ESMType.ESM_LOCK -> ESMIntentionLockActivity::class.java
        }
        val ESMIntent = Intent(context, destination)
        ESMIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ESMIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        ESMIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ESMIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        ESMIntent.putExtra(CONST.ESM_SESSION_ID_MESSAGE, sessionID)
        context.startActivity(ESMIntent)
    }

    fun dismissESMNotification(context: Context) {
        Log.d(TAG, "Dismiss ESM Notification")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            cancel(CONST.NOTIFICATION_ID_ESM)
        }
    }

    fun createSurveyNotification(context: Context, type: SurveryType) {
        val notificationIntent = Intent(Intent.ACTION_VIEW, createSurveyLink(type))
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val title = if(type == SurveryType.SURVEY_START) context.getString(R.string.survey_start_notification_title) else context.getString(R.string.survey_end_notification_title)
        val builder = NotificationCompat.Builder(context, CONST.CHANNEL_ID_ESM)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(title)
            .setContentText(context.getString(R.string.survey_notification_description))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if(type == SurveryType.SURVEY_END){
            builder.setOngoing(true)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        with(notificationManager) {
            createESMNotificationChannel()
            val notification = builder.build()
            notify(CONST.NOTIFICATION_ID_SURVEY, notification)
        }
    }

    fun dismissSurveyNotification(context: Context){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        with(notificationManager) {
            cancel(CONST.NOTIFICATION_ID_SURVEY)
        }
    }

    fun createSurveyLink(type: SurveryType): Uri? {
        val serialNumber = DatabaseManager.user?.uid
        val questionnaireParameter = when(type) {
            SurveryType.SURVEY_START -> "?q=MRH1"
            SurveryType.SURVEY_END -> "?q=MRH2"
        }
        serialNumber?.let {  uID ->
            val serialNumberParameter = "&s=$uID"
            val uri = "${CONST.baseSurveyURL}$questionnaireParameter$serialNumberParameter"
            Log.d(TAG, "uri: $uri")
            return Uri.parse(uri)
        } ?: return null
    }
}

fun Activity.turnScreenOnAndKeyguardOff() {
    setShowWhenLocked(true)
    setTurnScreenOn(true)

    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
            requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
        }
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    setShowWhenLocked(false)
    setTurnScreenOn(false)
}

sealed class ESMType {
    object ESM_UNLOCK : ESMType()
    object ESM_LOCK : ESMType()
}

enum class SurveryType {
    SURVEY_START,
    SURVEY_END
}
