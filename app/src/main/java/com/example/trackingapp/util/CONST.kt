package com.example.trackingapp.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object CONST {
    private const val TAG = "CONST"
    @JvmField
	//val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    var dateTimeFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS", Locale.GERMAN)

    const val PREFERENCES_FILE: String = "ESM_INTENTION"
    const val PREFERENCES_INTENTION_NAME: String = "SAVED_INTENTION"

    const val firebaseReferenceUsers = "users"
    const val firebaseReferenceLogs = "logs"
    const val firebaseReferenceIntentions = "intentionList"
}