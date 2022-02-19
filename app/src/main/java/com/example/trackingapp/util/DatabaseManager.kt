package com.example.trackingapp

import android.util.Log
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.User
import com.example.trackingapp.models.metadata.MetaType
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.SharedPrefManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

object DatabaseManager {

    val TAG = "TRACKINGAPP_DATABASE_MANAGER"

    val user: FirebaseUser?
        get() = Firebase.auth.currentUser

    val userID: String
        get() = user?.uid ?: ""

    val database = Firebase.database.reference

    var intentionList: MutableSet<String?> = HashSet()

    val isUserLoggedIn: Boolean
        get() = user != null

    fun initIntentionList() {
        Log.d(TAG, "initDatabaseManager")
        intentionList = SharedPrefManager.getIntentionList()
        if(!intentionList.contains("Browsing")) intentionList.add("Browsing")
        if(!intentionList.contains("Passing Time")) intentionList.add("Passing Time")
        if(!intentionList.contains("Search for Information")) intentionList.add("Search for Information")
    }

    fun saveUserToFirebase(userName: String, email: String) {
        val userId = Firebase.auth.currentUser?.uid
        val user = User(userName, email, userId)
        if (userId != null) {
            database.child(CONST.firebaseReferenceUsers)
                .child(userId)
                .setValue(user)
        }

    }

    fun LogEvent.saveToDataBase(metadata: MetaType? = null) {
        Log.d(TAG, "SaveEntryToDataBase: ${this.eventName} ${this.event} ${CONST.dateTimeFormat.format(this.timestamp)}")
        if (metadata != null) {
            user?.let {
                val logChild = Firebase.database.reference.child(CONST.firebaseReferenceUsers)
                    .child(it.uid)
                    .child(CONST.firebaseReferenceLogs)
                    .child("${CONST.dateTimeFormat.format(this.timestamp)} ${this.eventName.name}")

                logChild.setValue(this)
                logChild.child("metaData").setValue(metadata)
            }
        } else {
            user?.let {
                Firebase.database.reference.child(CONST.firebaseReferenceUsers)
                    .child(it.uid)
                    .child(CONST.firebaseReferenceLogs)
                    .child("${CONST.dateTimeFormat.format(this.timestamp)} ${this.eventName.name}")
                    .setValue(this)
            }
        }
    }

    fun saveNewIntention(time: Date, intention: String) {
        Log.d(TAG, "save new IntentionToDatabase: $intention")
        intentionList.add(intention)
        SharedPrefManager.saveIntentionList(intentionList)

        val timestamp = CONST.dateTimeFormat.format(time)
        user?.let {
            database.child(CONST.firebaseReferenceUsers)
                .child(it.uid)
                .child(CONST.firebaseReferenceIntentions)
                .child("$timestamp $intention")
                .setValue(intention)
        }
    }
}