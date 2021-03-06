package com.lmu.trackingapp.models

import com.lmu.trackingapp.service.LoggingManager
import java.util.*

open class LogEvent(
    val eventName : LogEventName,
    val timestamp: Long? = null,
    val event: String? = null,
    val description: String? = null,
    val name: String? = null,
    val packageName: String? = null,
    var id: String? = null,
    var timezoneOffset: Int? = null
) {
    init {
        timezoneOffset = calculateTimezoneOffset(timestamp)
        if (eventName != LogEventName.ESM) id = LoggingManager.currentSessionID
    }

    private fun calculateTimezoneOffset(timestamp: Long?): Int {
        timestamp?.let {
            return TimeZone.getDefault().getOffset(timestamp)
        }
        return 0
    }
}