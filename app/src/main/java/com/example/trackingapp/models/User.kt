package com.example.trackingapp.models

class User {

    private val userID: String = ""
    private val userName = ""
    val displayName: String = ""
    private val email = ""

    private var loggingList = listOf<Log>()


}

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String
)