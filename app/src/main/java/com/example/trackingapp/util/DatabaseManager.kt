package com.example.trackingapp.util

import android.util.Log
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.User
import com.example.trackingapp.models.metadata.MetaType
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

object DatabaseManager {

    const val TAG = "TRACKINGAPP_DATABASE_MANAGER"

    val user: FirebaseUser?
        get() = Firebase.auth.currentUser

    private val database = Firebase.database.reference

    var intentionList: MutableSet<String?> = HashSet()
    var intentionExampleList: List<String> = listOf()

    val isUserLoggedIn: Boolean
        get() = user != null

    fun initIntentionList() {
        Log.d(TAG, "initDatabaseManager")
        intentionList = SharedPrefManager.getIntentionList()
        intentionExampleList = listOf(
            "No concrete intention",
            "Messaging",
            "Search for information"
        )
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
