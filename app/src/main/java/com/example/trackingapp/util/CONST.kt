package com.example.trackingapp.util

import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object CONST {
    var currentLocale: Locale = Locale.GERMAN
    var dateTimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS", currentLocale)

    const val PREFERENCES_FILE: String = "TRACKING_APP"
    const val PREFERENCES_INTENTION_NAME: String = "SAVED_INTENTION"
    const val PREFERENCES_INTENTION_LIST: String = "INTENTION_LIST"
    const val PREFERENCES_ONBOARDING_FINISHED: String = "ONBOARDING_FINISHED"
    const val PREFERENCES_LAST_ESM_FULL_TIMESTAMP: String = "PREFERENCES_LAST_ESM_FULL_TIMESTAMP"
    const val PREFERENCES_LOGGING_FIRST_STARTED: String = "PREFERENCES_LOGGING_FIRST_STARTED"
    const val PREFERENCES_DATA_RECORDING_ACTIVE: String = "PREFERENCES_DATA_RECORDING_ACTIVE"
    const val PREFERENCES_USER_PRESENT: String = "PREFERENCES_USER_PRESENT"

    const val firebaseReferenceUsers = "users"
    const val firebaseReferenceLogs = "logs"
    const val firebaseReferenceIntentions = "intentionList"

    const val CHANNEL_ID_LOGGING = "LOGGING_MANAGER_NOTIFICATION_CHANNEL"
    const val CHANNEL_NAME_ESM_LOGGING = "Logging Manager"
    const val NOTIFICATION_ID_LOGGING = 24755

    const val CHANNEL_ID_ESM = "RabbitHoleAlert"
    const val CHANNEL_NAME_ESM = "RabbitHole Alert"
    const val NOTIFICATION_ID_ESM = 24756

    const val PERMISSION_REQUEST_CODE = 123

    const val LOGGING_FREQUENCY: Long = 500 //milliseconds
    const val ESM_FREQUENCY: Long = 30 * 60 * 1000 //30 min
    const val LOGGING_CHECK_FOR_LOGGING_ALIVE_INTERVAL: Long = 16 //minutes

    const val UNIQUE_WORK_NAME = "StartMyServiceViaWorker"

    val numberFormat: NumberFormat = NumberFormat.getInstance()
}