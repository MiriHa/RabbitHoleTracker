package com.example.trackingapp.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.ContentChangeEvent
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.google.firebase.FirebaseApp


//TODO lPointerException: Parameter specified as non-null is null: method kotlin.jvm.internal.Intrinsics.checkNotNullParameter, parameter intent
// at com.example.trackingapp.service.AccessibilityLogService.onStartCommand(Unknown Source:2)

class AccessibilityLogService : AccessibilityService() {

    val TAG = "ACCESSIBILITYLOGSERVICE"

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
        isRunning = true
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
        isRunning = false
    }

    var keyboardEvents = mutableListOf<Event>()
    var initialContent: String? = null
    var cachedHintText: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val time = System.currentTimeMillis()
        try {
            when {
                event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> return //TODO also record window content? -> to much info?

                event?.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                    LogEvent(
                        LogEventName.ACCESSIBILITY,
                        timestamp = time,
                        event = getEventType(event),
                        description = getWindowChangeType(event)
                    ).saveToDataBase()
                }
                //represents and foreground change
                event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    LogEvent(
                        LogEventName.APPS,
                        timestamp = time,
                        event = getEventType(event),
                        description = getEventText(event),
                        name = event?.className.toString(),
                        packageName = event?.packageName.toString()
                    ).saveToDataBase()
                }
                event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED && !event.isPassword -> {

                    // create ContentChangeEvents
                    val contentChangeEvent = ContentChangeEvent(event.text[0].toString(), null)
                    // ---- hint text ----
                    // first: try to get hint text property
                    if (event.source != null && event.source.hintText != null) {
                        contentChangeEvent.fieldHintText = event.source.hintText.toString()

                    } else if (cachedHintText != null) {
                        // if that doesnt work, use the previous text if there is one cached
                        // TODO using cached hint text is disabeld, as it may sometimes log user content
                        //                    contentChangeEvent.setFieldHintText(cachedHintText);
                        //                    LogHelper.i(TAG,"used cached hint text: "+cachedHintText);
                    }
                    try {
                        contentChangeEvent.fieldPackageName = event.packageName.toString()
                    } catch (e: Exception) {
                        Log.i(
                            TAG,
                            "Could not fetch packageName of event source node",
                            e
                        )
                    }
                    keyboardEvents.add(contentChangeEvent)
                    if (keyboardEvents.size == 1) {
                        initialContent = if (event.beforeText != null) {
                            event.beforeText.toString()
                        } else {
                            ""
                        }
                    }
                }

                // entering a new node -> cache the hint text, in case this is a textfield
                AccessibilityEvent.TYPE_VIEW_FOCUSED == event?.eventType && keyboardEvents.size == 0 -> {
                    try {
                        cachedHintText = event.text[0].toString()
                        Log.i(TAG, "caching hint text: $cachedHintText")
                    } catch (e: Exception) {
                        Log.w(TAG, "could not fetch hint text from event: $event", e)
                    }
                }
                // leaving a textfield
                AccessibilityEvent.TYPE_VIEW_FOCUSED == event?.eventType && keyboardEvents.size > 0 -> {
                    onFinishInput(event, time)
                }

                else -> {
                    LogEvent(
                        LogEventName.ACCESSIBILITY,
                        timestamp = time,
                        event = getEventType(event),
                        description = getEventText(event),
                        name = event?.className.toString(),
                        packageName = event?.packageName.toString()
                    ).saveToDataBase()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "exception in onAccessibilityEvent() for event " + (event?.toString() ?: ""), e)
        }
    }

    private fun onFinishInput(event: AccessibilityEvent?, time: Long) { //TODO public only for testing
        if (keyboardEvents.size < 1) {
            return
        }
        // take current events, and decouple this list from the actively used for collecting new events
        val currentKeyboardEvents = keyboardEvents
        val currentInitialContent: String? = initialContent
        keyboardEvents = mutableListOf()
        initialContent = null
        cachedHintText = null

        LogEvent(
            LogEventName.INPUT,
            timestamp = time,
            event = getEventType(event),
            description = getEventText(event),
            name = event?.className.toString(),
            packageName = event?.packageName.toString()
        ).saveToDataBase(metadataList = currentKeyboardEvents)


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
            AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> return "TYPE_ASSIST_READING_CONTEXT"
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> return "TYPE_NOTIFICATION_STATE_CHANGED"
            AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> return "TYPE_VIEW_CONTEXT_CLICKED"
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> return "TYPE_WINDOWS_CHANGED"
        }
        return "default"
    }

    private fun getWindowChangeType(event: AccessibilityEvent?): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return when (event?.windowChanges) {
                AccessibilityEvent.WINDOWS_CHANGE_ADDED -> "WINDOWS_CHANGE_ADDED"
                AccessibilityEvent.WINDOWS_CHANGE_REMOVED -> "WINDOWS_CHANGE_REMOVED"
                AccessibilityEvent.WINDOWS_CHANGE_TITLE -> "WINDOWS_CHANGE_TITLE"
                AccessibilityEvent.WINDOWS_CHANGE_BOUNDS -> "WINDOWS_CHANGE_BOUNDS"
                AccessibilityEvent.WINDOWS_CHANGE_LAYER -> "WINDOWS_CHANGE_LAYER"
                AccessibilityEvent.WINDOWS_CHANGE_ACTIVE -> "WINDOWS_CHANGE_ACTIVE"
                AccessibilityEvent.WINDOWS_CHANGE_FOCUSED -> "WINDOWS_CHANGE_FOCUSED"
                AccessibilityEvent.WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED -> "WINDOWS_CHANGE_ACCESSIBILITY_FOCUSED"
                AccessibilityEvent.WINDOWS_CHANGE_PARENT -> "WINDOWS_CHANGE_PARENT"
                AccessibilityEvent.WINDOWS_CHANGE_CHILDREN -> "WINDOWS_CHANGE_CHILDREN"
                AccessibilityEvent.WINDOWS_CHANGE_PIP -> "WINDOWS_CHANGE_PIP"
                else -> "WINDOW_CHANGE"
            }
        } else return " "
    }

    companion object {
        val TAG: String = "ACCESSIBILITYSERVICE" // AccessibilityLogService::class.java.simpleName
        //val classTAG = AccessibilityLogService::class.java.simpleName

        @JvmStatic
        var isRunning: Boolean = false


    }
}