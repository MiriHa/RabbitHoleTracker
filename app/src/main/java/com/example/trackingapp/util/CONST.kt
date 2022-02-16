package com.example.trackingapp.util

import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object CONST {
    private const val TAG = "TRACKINGAPP_CONST"
    @JvmField
	//val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    var currentLocale: Locale = Locale.GERMAN
    var dateTimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS", currentLocale)

    const val PREFERENCES_FILE: String = "TRACKING_APP"
    const val PREFERENCES_INTENTION_NAME: String = "SAVED_INTENTION"
    const val PREFERENCES_INTENTION_LIST: String = "INTENTION_LIST"
    const val PREFERENCES_ONBOARDING_FINISHED: String = "ONBOARDING_FINISHED"
    const val PREFERENCES_LAST_ESM_FULL_TIMESTAMP: String = "PREFERENCES_LAST_ESM_FULL_TIMESTAMP"
    const val PREFERENCES_LOGGING_FIRST_STARTED: String = "PREFERENCES_LOGGING_FIRST_STARTED"

    const val firebaseReferenceUsers = "users"
    const val firebaseReferenceLogs = "logs"
    const val firebaseReferenceIntentions = "intentionList"

    const val CHANNEL_ID_LOGGING = "LOGGINGMANAGER_NOTIFICATION_CHANNEL"
    const val CHANNEL_NAME_ESM_LOGGING = "Logging Manager"
    const val NOTIFICATION_ID_LOGGING = 24755

    const val CHANNEL_ID_ESM = "rabbitholeAlert"
    const val CHANNEL_NAME_ESM = "RabbitHole Alert"
    const val NOTIFICATION_ID_ESM = 24756

    const val LOGGING_CHECK_FOR_LOGGING_ALIVE_INTERVAL: Long = 20

    const val PERMISSION_REQUEST_CODE = 123

    const val LOGGING_INTERVAL = 60 * 0.1; // 60 * 1 = 1 minutes
    const val LOGGING_FREQUENCY: Long = 500 //milliseconds

    const val UNIQUE_WORK_NAME = "StartMyServiceViaWorker"

    val numberFormat: NumberFormat = NumberFormat.getInstance()
}