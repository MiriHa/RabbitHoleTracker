package com.example.trackingapp.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.google.firebase.FirebaseApp


//TODO lPointerException: Parameter specified as non-null is null: method kotlin.jvm.internal.Intrinsics.checkNotNullParameter, parameter intent
// at com.example.trackingapp.service.AccessibilityLogService.onStartCommand(Unknown Source:2)

class AccessibilityLogService : AccessibilityService() {
   // private var mWakeLock: PowerManager.WakeLock? = null
    private val info = AccessibilityServiceInfo()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        FirebaseApp.initializeApp(this)
       // val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        //mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
       // mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, classTAG)
    }

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.v(TAG, "onServiceConnected")


        // Set the type of events that this service wants to listen to.  Others
        // won't be passed to this service.
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK

        // If you only want this service to work with specific applications, set their
        // package names here.  Otherwise, when the service is activated, it will listen
        // to events from all applications.
        //info.packageNames = new String[]{"com.example.android.myFirstApp", "com.example.android.mySecondApp"};

        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK

        // Default services are invoked only if no package-specific ones are present
        // for the type of AccessibilityEvent generated.  This service *is*
        // application-specific, so the flag isn't necessary.  If this was a
        // general-purpose service, it would be worth considering setting the
        // DEFAULT flag.

        // info.flags = AccessibilityServiceInfo.DEFAULT;
        info.flags = AccessibilityServiceInfo.DEFAULT
        info.notificationTimeout = 100
        this.serviceInfo = info
    }

    private fun getEventText(event: AccessibilityEvent?): String {
        val sb = StringBuilder()
        event?.let {
            for (s in event.text) {
                sb.append(s)
            }
        }
        return sb.toString()
    }

    private fun tryGetActivity(componentName: ComponentName): ActivityInfo? {
        return try {
            packageManager.getActivityInfo(componentName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//        Log.d(TAG, "onStartCommand() was called")
////        mWakeLock?.let { wakeLock ->
////            if (!wakeLock.isHeld) {
////                wakeLock.acquire(10*60*1000L /*10 minutes*/)
////            }
////            Handler(Looper.getMainLooper()).postDelayed(
////                {
////                    if (wakeLock.isHeld) {
////                        wakeLock.release()
////                    }
////                },
////                10000
////            )
////        }
//        return START_STICKY
//    }

    override fun onDestroy() {
        Log.d(TAG, "service stopped")
//        mWakeLock?.let { wakeLock ->
//            if (wakeLock.isHeld) {
//                wakeLock.release()
//            }
//        }
        stopForeground(true)
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == event?.eventType) return //TODO also record window content? -> to much info?

        var eventName = EventName.ACCESSIBILITY
        if (AccessibilityEvent.TYPE_WINDOWS_CHANGED == event?.eventType) {
            eventName = EventName.APPS
        }

        Event(
            eventName,
            timestamp = System.currentTimeMillis(),
            event = getEventType(event),
            description = getEventText(event),
            name = event?.className.toString(),
            packageName = event?.packageName.toString()
        ).saveToDataBase()
    }

    private fun getEventType(event: AccessibilityEvent?): String {
        when (event?.eventType) {
            AccessibilityEvent.TYPE_ANNOUNCEMENT -> return "TYPE_ANNOUNCEMENT"
            AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> return "TYPE_GESTURE_DETECTION_END"
            AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> return "TYPE_GESTURE_DETECTION_START"
            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> return "TYPE_TOUCH_EXPLORATION_GESTURE_END"
            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> return "TYPE_TOUCH_EXPLORATION_GESTURE_START"
            AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> return "TYPE_TOUCH_INTERACTION_END"
            AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> return "TYPE_TOUCH_INTERACTION_START"
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED"
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED"
            AccessibilityEvent.TYPE_VIEW_CLICKED -> return "TYPE_VIEW_CLICKED"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> return "TYPE_VIEW_FOCUSED"
            AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> return "TYPE_VIEW_HOVER_ENTER"
            AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> return "TYPE_VIEW_HOVER_EXIT"
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> return "TYPE_VIEW_LONG_CLICKED"
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> return "TYPE_VIEW_SCROLLED"
            AccessibilityEvent.TYPE_VIEW_SELECTED -> return "TYPE_VIEW_SELECTED"
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> return "TYPE_VIEW_TEXT_CHANGED"
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> return "TYPE_VIEW_TEXT_SELECTION_CHANGED"
            AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> return "TYPE_WINDOW_CONTENT_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> return "TYPE_WINDOW_STATE_CHANGED"
            AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT ->  return "TYPE_ASSIST_READING_CONTEXT"
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED ->  return "TYPE_NOTIFICATION_STATE_CHANGED"
            AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED ->  return "TYPE_VIEW_CONTEXT_CLICKED"
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> return "TYPE_WINDOWS_CHANGED"
        }
        return "default"
    }

    companion object {
        val TAG: String = "ACCESSIBILITYSERVICE" // AccessibilityLogService::class.java.simpleName
        //val classTAG = AccessibilityLogService::class.java.simpleName
    }
}