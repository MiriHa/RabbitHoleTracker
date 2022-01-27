package com.example.trackingapp.models

import com.example.trackingapp.models.metadata.MetaType

abstract class Event(var type: Type?): MetaType() {
    enum class Type {
        TOUCH_DOWN, TOUCH_MOVE, TOUCH_UP, AUTO_CORRECT, SUGGESTION_PICKED, SUGGESTIONS_UPDATE, PRIVATE_MODE, CONTENT_CHANGE, META, SUGGESTION_AMOUNT_CHANGE
    }

    var clientDbEventId: Long? = null
    var timestamp: Long = 0
    var userUuid: String? = null
    var keyboardStateUuid: String? = null
    var handPosture: String? = null
    var fieldHintText: String? = null
        set(fieldHintText) {
            if (fieldHintText != null) field = fieldHintText.toString()
        }
    var fieldPackageName: String? = null
    var fieldId: Int? = null
    var anonymized = false
    var sensors: Map<Int, FloatArray>? = null
    var relatedMessageStatisticsClientDbId: Long? = null
    var keyboardId: String? = null

    init {
        timestamp = System.currentTimeMillis()
        clientDbEventId = id
    }

    companion object {
        private var lastId: Long = 0

        @get:Synchronized
        private val id: Long
            get() {
                var newId = System.currentTimeMillis()
                if (newId <= lastId) {
                    newId = lastId + 1
                }
                lastId = newId
                return newId
            }
    }
}
