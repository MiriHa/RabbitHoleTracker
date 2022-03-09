package com.example.trackingapp.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object CONST {
    var currentLocale: Locale = Locale.GERMAN
    var dateTimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS", currentLocale)

    //Shared Preferences constants
    const val PREFERENCES_FILE: String = "TRACKING_APP"
    const val PREFERENCES_INTENTION_NAME: String = "SAVED_INTENTION"
    const val PREFERENCES_INTENTION_LIST: String = "INTENTION_LIST"
    const val PREFERENCES_LAST_ESM_FULL_TIMESTAMP: String = "PREFERENCES_LAST_ESM_FULL_TIMESTAMP"
    const val PREFERENCES_LOGGING_FIRST_STARTED: String = "PREFERENCES_LOGGING_FIRST_STARTED"
    const val PREFERENCES_DATA_RECORDING_ACTIVE: String = "PREFERENCES_DATA_RECORDING_ACTIVE"
    const val PREFERENCES_USER_PRESENT: String = "PREFERENCES_USER_PRESENT"
    const val PREFERENCES_ESM_LOCK_ANSWERED: String = "PREFERENCES_ESM_LOCK_ANSWERED"
    const val PREFERENCES_IS_NO_CONCRETE_INTENTION: String = "PREFERENCES_IS_NO_CONCRETE_INTENTION"
    const val PREFERENCES_SESSION_ID: String = "PREFERENCES_SESSION_ID"

    //Firebase
    const val firebaseReferenceUsers = "users"
    const val firebaseReferenceLogs = "logs"
    const val firebaseReferenceIntentions = "intentionList"

    //Notifications
    const val CHANNEL_ID_LOGGING = "LOGGING_MANAGER_NOTIFICATION_CHANNEL"
    const val CHANNEL_NAME_ESM_LOGGING = "Logging Manager"
    const val NOTIFICATION_ID_LOGGING = 24755

    const val CHANNEL_ID_ESM = "RabbitHoleAlert"
    const val CHANNEL_NAME_ESM = "RabbitHole Alert"
    const val NOTIFICATION_ID_ESM = 24756

    //Permissions
    const val PERMISSION_REQUEST_CODE = 123

    //Frequency and Constants
    const val LOGGING_FREQUENCY: Long = 500 //milliseconds
    const val ESM_FREQUENCY: Long = 20 * 60 * 1000 //20 min
    const val LOGGING_CHECK_FOR_LOGGING_ALIVE_INTERVAL: Long = 16 //minutes
    const val ESM_LOCK_ASK_COUNT = 3
    const val ESM_SESSION_TIMEOUT = 45 * 1000 //45s
    const val ESM_LOCK_TIMEOUT = 3 * 1000 //1s

    //Intent
    const val ESM_ANSWERED: String = "com.example.trackingapp.ESM_ANSWERED"
    const val ESM_ANSWERED_MESSAGE = "ESM_ANSWERED_MESSAGE"
    const val ESM_SESSION_ID_MESSAGE = "ESM_SESSION_ID_MESSAGE"

    const val UNIQUE_WORK_NAME = "RABBIT_HOLE_TRACKER_STAY_ALIVE"

}