package com.lmu.trackingapp.service.sensorservice

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.firebase.FirebaseApp
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase
import com.lmu.trackingapp.util.SharedPrefManager

class AccessibilityLogService : AccessibilityService() {

    val TAG = "ACCESSIBILITYLOGSERVICE"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        FirebaseApp.initializeApp(this)
        SharedPrefManager.init(this)
    }

    override fun onInterrupt() {
        Log.v(TAG, "onInterrupt")
    }

    override fun onDestroy() {
        Log.d(TAG, "service stopped")
        stopForeground(true)
        super.onDestroy()
    }

    var browserApp = ""
    var browserUrl = ""

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val time = System.currentTimeMillis()
        try {
            when (event?.eventType) {
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED, AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ->
                    trackBrowserURL(event)
                AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                    trackBrowserURL(event)
                    LogEvent(
                        LogEventName.ACCESSIBILITY,
                        timestamp = time,
                        event = getEventType(event),
                        description = getWindowChangeType(event)
                    ).saveToDataBase()
                }
                //represents and foreground change
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> trackBrowserURL(event)
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> return
                AccessibilityEvent.TYPE_ANNOUNCEMENT -> return
                AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> return
                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> return
                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> return
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> return
                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> return
                AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> return
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> return
                AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> return
                AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> return
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> return
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    LogEvent(
                        LogEventName.ACCESSIBILITY_KEYBOARD_INPUT,
                        timestamp = time,
                        event = getEventType(event),
                        description = getEventText(event).hashCode().toString(),
                        name = event.className.toString(),
                        packageName = event.packageName.toString()
                    ).saveToDataBase()
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

    private fun trackBrowserURL(event: AccessibilityEvent) {
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
                        LogEventName.ACCESSIBILITY_BROWSER_URL,
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
                            LogEventName.ACCESSIBILITY_BROWSER_URL,
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