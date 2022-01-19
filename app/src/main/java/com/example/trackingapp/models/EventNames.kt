package com.example.trackingapp.models

enum class ESM_Intention_Lock_Answer{
    ESM_INTENTION_FINISHED,
    ESM_INTENTION_UNFINISHED,
    ESM_MORE_THAN_INITIAL_INTENTION,
    ESM_NOT_MORE_THAN_INITIAL_INTENTION
}

enum class EventName {

    ACCESSIBILITY,
    APPS,
    AIRPLANEMODE,
    ACCELEROMETER,
    BLUETOOTH,
    BOOT,
    PHONE,
    POWER,
    NOTIFICATION,
    SCREEN,
    SCREEN_ORIENTATION,
    RINGER_MODE,
    SMS,
    INTERNET,
    LOGIN,
    ESM,
}

enum class ESMState {
    ESM_UNLOCK,
    ESM_LOCK_Q1,
    ESM_LOCK_Q2
}

enum class BootEventType {
    BOOTED,
    SHUTDOWM
}

enum class ONOFFSTATE{
    ON,
    OFF
}

enum class PowerState {
    CONNECTED,
    DISCONNECTED
}

enum class ScreenState {
    ON_LOCKED,
    ON_UNLOCKED,
    OFF_UNLOCKED,
    OFF_LOCKED,
    ON_USERPRESENT,
    UNKNOWN
}

enum class WifiConnectionState {
    DISABLED,
    ENABLED,
    UNKNOWN
}

enum class ConnectionType {
    CONNECTED_WIFI,
    CONNECTED_MOBILE,
    CONNECTED_ETHERNET,
    CONNECTED_VPN,
    UNKNOWN
}

enum class SmsEventType {
    UNKNOWN,
    INBOX,
    SENT,
    DRAFT,
    OUTBOX
}

enum class ScreenOrientationType {
    SCREEN_ORIENTATION_PORTRAIT,
    SCREEN_ORIENTATION_LANDSCAPE,
    SCREEN_ORIENTATION_UNDEFINED
}

enum class RingerMode {
    SILENT_MODE,
    VIBRATE_MODE,
    NORMAL_MODE,
    UNKNOWN
}

enum class AccelerometerAccuracy {
    ACCURACY_UNRELAIABLE
}
