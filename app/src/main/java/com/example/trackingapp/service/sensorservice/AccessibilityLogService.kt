package com.example.trackingapp.service.sensorservice

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.metadata.ContentChangeEvent
import com.google.firebase.FirebaseApp

class AccessibilityLogService : AccessibilityService() {

    val TAG = "ACCESSIBILITYLOGSERVICE"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        FirebaseApp.initializeApp(this)
    }

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt")
    }

    override fun onDestroy() {
        Log.d(TAG, "service stopped")
        stopForeground(true)
        super.onDestroy()
    }

    var keyboardEvents: Int = 0 //mutableListOf<String>()
    var initialContent: String? = null
    var cachedHintText: String? = null
    var chachedContentChangeEvent: ContentChangeEvent? = null

    var browserApp = ""
    var browserUrl = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val time = System.currentTimeMillis()
        try {
            when {
                event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||  event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ->
                    trackBrowserURL(event)

                event?.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                    trackBrowserURL(event)
                    LogEvent(
                        LogEventName.ACCESSIBILITY,
                        timestamp = time,
                        event = getEventType(event),
                        description = getWindowChangeType(event)
                    ).saveToDataBase()
                }
                //represents and foreground change
                event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> trackBrowserURL(event)
                event?.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> return
                event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED && !event.isPassword -> { return //TYPE VIEW TEXT CHANGED
                    // User starts input of text -> create new text event?
                    // do i need this or get the text via getEvetnText?
/*
                    // create ContentChangeEvents
                    if(chachedContentChangeEvent == null) chachedContentChangeEvent = ContentChangeEvent()

                    // first: try to get hint text property
                   *//* if (event.source != null *//**//*&& event.source.hintText != null*//**//*) {
                        chachedContentChangeEvent?.fieldHintText = event.source.hintText.toString()
                        chachedContentChangeEvent?.message += event.source.text.toString()
                        Log.d("xxx", "textN: ${event.source.text} ${event.source.hintText}")
                    } else {*//*
                    chachedContentChangeEvent?.message += getEventText(event)
                   // }
                    try {
                        chachedContentChangeEvent?.fieldPackageName = event.packageName.toString()
                    } catch (e: Exception) {
                        Log.i(TAG, "Could not fetch packageName of event source node", e)
                    }
                    keyboardEvents += 1
                    *//*if (keyboardEvents == 1) {
                        initialContent = if (event.beforeText != null) {
                            event.beforeText.toString()
                        } else {
                            ""
                        }
                    }*/
                }

              /*  // entering a new node -> cache the hint text, in case this is a textfield
                AccessibilityEvent.TYPE_VIEW_FOCUSED == event?.eventType && keyboardEvents == 0 -> {
                    Log.w("xxx", "ACCESSIBILITY TYPE_VIEW_FOCUSED keyevents 0: $keyboardEvents")
                    try {
                        cachedHintText = event.text.toString()
                        Log.i(TAG, "caching hint text: $cachedHintText")
                    } catch (e: Exception) {
                        Log.w(TAG, "could not fetch hint text from event: $event", e)
                    }
                }
                // leaving a textfield
                AccessibilityEvent.TYPE_VIEW_FOCUSED == event?.eventType && keyboardEvents > 0 -> {
                    Log.w("xxx", "ACCESSIBILITY TYPE_VIEW_FOCUSED keyevents over 0: $keyboardEvents")
                    onFinishInput(time)
                }*/
                else -> {
                   /* if(keyboardEvents > 0
                        && event?.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED
                        && event?.eventType != AccessibilityEvent.TYPE_VIEW_HOVER_EXIT
                        && event?.eventType != AccessibilityEvent.TYPE_VIEW_HOVER_ENTER
                    ){
                        onFinishInput(time)
                    }*/
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

    private fun onFinishInput(time: Long) { //TODO public only for testing
        if (keyboardEvents < 1) {
            return
        }
        chachedContentChangeEvent?.keyboardEvents = keyboardEvents
        chachedContentChangeEvent?.timestampEnd = time

        LogEvent(
            LogEventName.INPUT,
            timestamp = time,
            event = getEventType(chachedContentChangeEvent?.event),
//            description = chachedContentChangeEvent?.message,
            name = chachedContentChangeEvent?.event?.className.toString(),
            packageName = chachedContentChangeEvent?.event?.packageName.toString()
        ).saveToDataBase(metadata = chachedContentChangeEvent)

        keyboardEvents = 0
        initialContent = null
        cachedHintText = null
        chachedContentChangeEvent = null

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

    private fun trackBrowserURL(event: AccessibilityEvent){
        try {
            val parentNodeInfo = event.source ?: return
            val packageName = event.packageName.toString()
            var browserConfig: SupportedBrowserConfig? = null
            for (supportedConfig in getSupportedBrowsers()) {
                if (supportedConfig.packageName == packageName) {
                    browserConfig = supportedConfig
                }
            }
            //this is not supported browser, so exit
            if (browserConfig == null) {
                return
            }
            val capturedUrl = captureUrl(parentNodeInfo, browserConfig)
            parentNodeInfo.recycle()
            if (capturedUrl == null) {
                return
            }
            if (packageName != browserApp) {
                if (Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                    Log.d("Browser", "$packageName  :  $capturedUrl")
                    browserApp = packageName
                    browserUrl = capturedUrl
                    LogEvent(
                        LogEventName.ACCESIBILIITY_BROWSER_URL,
                        timestamp = event.eventTime,
                        event = getEventType(event),
                        description = browserUrl,
                        name = "BrowserURL",
                        packageName = packageName
                    ).saveToDataBase()
                }
            } else {
                if (capturedUrl != browserUrl) {
                    if (Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                        browserUrl = capturedUrl
                        Log.d("Browser", "$packageName   $capturedUrl")
                        LogEvent(
                            LogEventName.ACCESIBILIITY_BROWSER_URL,
                            timestamp = event.eventTime,
                            event = getEventType(event),
                            description = browserUrl,
                            name = "BrowserURL",
                            packageName = packageName
                        ).saveToDataBase()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to track browser url. event: ${event.eventType}", e)
        }
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

    private class SupportedBrowserConfig(var packageName: String, var addressBarId: String)

    private fun getSupportedBrowsers(): List<SupportedBrowserConfig> {
        val browsers: MutableList<SupportedBrowserConfig> = ArrayList()
        browsers.add(SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"))
        browsers.add(SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"))
        browsers.add(SupportedBrowserConfig("com.opera.browser", "com.opera.browser:id/url_field"))
        browsers.add(SupportedBrowserConfig("com.opera.mini.native", "com.opera.mini.native:id/url_field"))
        browsers.add(SupportedBrowserConfig("com.duckduckgo.mobile.android", "com.duckduckgo.mobile.android:id/omnibarTextInput"))
        browsers.add(SupportedBrowserConfig("com.microsoft.emmx", "com.microsoft.emmx:id/url_bar"))
        browsers.add(SupportedBrowserConfig("com.android.browser","com.android.browser:id/url_bar"))
        return browsers
    }

    private fun getChild(info: AccessibilityNodeInfo) {
        val i = info.childCount
        for (p in 0 until i) {
            val n = info.getChild(p)
            if (n != null) {
                val strres = n.viewIdResourceName
                if (n.text != null) {
                    val txt = n.text.toString()
                    Log.d("Track", "$strres  :  $txt")
                }
                getChild(n)
            }
        }
    }

    private fun captureUrl(info: AccessibilityNodeInfo, config: SupportedBrowserConfig): String? {

        //  getChild(info);
        val nodes = info.findAccessibilityNodeInfosByViewId(config.addressBarId)
        if (nodes == null || nodes.size <= 0) {
            return null
        }
        val addressBarNodeInfo = nodes[0]
        var url: String? = null
        if (addressBarNodeInfo.text != null) {
            url = addressBarNodeInfo.text.toString()
        }
        addressBarNodeInfo.recycle()
        return url
    }

}