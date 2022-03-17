package com.lmu.trackingapp.models

class User(
    val account_email:String? = null,
    val account_userId: String? = null) {

    private var loggingList = listOf<LogEvent>()
}