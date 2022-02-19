package com.example.trackingapp.models.metadata

import android.view.accessibility.AccessibilityEvent

class ContentChangeEvent: MetaType() {

    var keyboardEvents: Int = 0

    var type: String? = "CONTENT_CHANGE"

    var timestamp: Long = 0
    var timestampEnd: Long = 0

    var fieldHintText: String? = null
        set(fieldHintText) {
            if (fieldHintText != null) field = fieldHintText.toString()
        }
    var message: String = ""
    var event: AccessibilityEvent? = null

    var fieldPackageName: String? = null


    init {
        timestamp = System.currentTimeMillis()
    }
}
