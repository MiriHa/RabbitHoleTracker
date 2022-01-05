package com.example.trackingapp.models

class User(
    val account_email:String? = null,
    val account_userId: String? = null) {

    private var loggingList = listOf<ModelLog>()


}

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val userId: String,
    val displayName: String
)