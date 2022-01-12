package com.example.trackingapp.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object CONST {
    private const val TAG = "TRACKINGAPP_CONST"
    @JvmField
	//val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    var dateTimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS", Locale.GERMAN)

    const val PREFERENCES_FILE: String = "ESM_INTENTION"
    const val PREFERENCES_INTENTION_NAME: String = "SAVED_INTENTION"
    const val PREFERENCES_IS_LOGGING_SERVICE_RUNNING: String = "IS_LOGGING_SERVICE_RUNNING"

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

}