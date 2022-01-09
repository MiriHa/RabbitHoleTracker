package com.example.trackingapp.models

class Event(
    val eventName : EventName,
    val timestamp: String,
    val event: String?,
    val description: String? = null,
    val packageName: String? = null,
) {

}