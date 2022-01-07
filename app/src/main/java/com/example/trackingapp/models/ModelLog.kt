package com.example.trackingapp.models

class ModelLog (
    val activity: LogActivity? = null,
    val activity_details: String? = null,
    val time_timestamp: String? = null,
    val time_weekday: String? = null,
    val time_hourOfDay: String? = null
)

enum class LogActivity{
    LOGIN,
    SCREEN_LOCK,
    SCREEN_UNLOCK,
    ESM_UNLOCK,
    ESM_LOCK
}

enum class ESM_Intention_Lock_Answer{
    ESM_INTENTION_FINISHED,
    ESM_INTENTION_UNFINISHED,
    ESM_MORE_THAN_INITIAL_INTENTION,
    ESM_NOT_MORE_THAN_INITIAL_INTENTION
}