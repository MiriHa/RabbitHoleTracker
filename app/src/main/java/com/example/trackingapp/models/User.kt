package com.example.trackingapp.models

class User(
    val account_userName: String? = null,
    val account_email:String? = null,
    val account_userId: String? = null) {

    private var loggingList = listOf<LogEvent>()
}