package com.lmu.trackingapp.models

enum class LogEventName {

    ACCESSIBILITY,
    ACCESSIBILITY_BROWSER_URL,
    ACCESSIBILITY_KEYBOARD_INPUT,
    APPS_INSTALL,
    AIRPLANEMODE,
    ACCELEROMETER,
    ACTIVITY,
    BLUETOOTH,
    BOOT,
    DATA_TRAFFIC,
    DEVICE_INFO,
    ESM,
    GYROSCOPE,
    INTERNET,
    INSTALLED_APP,
    LIGHT,
    NOTIFICATION,
    PHONE_ORIENTATION,
    PHONE,
    POWER,
    PROXIMITY,
    RINGER_MODE,
    SCREEN,
    SCREEN_ORIENTATION,
    SMS,
    USAGE_EVENTS
}

enum class BootEventType {
    BOOTED,
    SHUTDOWN,
    REBOOT
}

enum class ONOFFSTATE {
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

enum class SensorAccuracy {
    ACCURACY_UNRELAIABLE,
    ACCURACY_ELSE
}

enum class ActivityType {
    IN_VEHICLE,
    ON_BICYCLE,
    ON_FOOT,
    RUNNING,
    STILL,
    TILTING,
    WALKING,
    UNKNOWN
}

enum class ActivityTransitionType {
    ACTIVITY_TRANSITION_ENTER,
    ACTIVITY_TRANSITION_EXIT,
    ACTIVITY_TRANSITION_UNKNOWN
}

enum class InstallEventType {
    INSTALLED,
    UPDATED,
    UNINSTALLED_AND_DATA_REMOVED,
    UNINSTALLED,
    DATA_CLEARED,
    UNKNOWN
}