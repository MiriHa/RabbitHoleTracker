package com.example.trackingapp

import android.util.Log
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.example.trackingapp.models.User
import com.example.trackingapp.models.metadata.MetaType
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.SharedPrefManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    init {
        //TODO Calls to setPersistenceEnabled() must be made before any other usage of FirebaseDatabase instance.
        //Firebase.database.setPersistenceEnabled(true)
    }

    fun initIntentionList() {
        Log.d(TAG, "initDatabaseManager")
        //TODO getSavedIntentions()
        intentionList = SharedPrefManager.getIntentionList()
        intentionList.addAll(arrayOf("Browsing", "Passing Time", "Search for Information"))
    }

    fun saveUserToFirebase(email: String) {
        val userId = Firebase.auth.currentUser?.uid
        val user = User(email, userId)
        if (userId != null) {
            database.child(CONST.firebaseReferenceUsers)
                .child(userId)
                .setValue(user)

            Event(
                EventName.LOGIN,
                System.currentTimeMillis(),
                "Account created"
            ).saveToDataBase()
        }

    }

    fun Event.saveToDataBase(metadata: MetaType? = null) {
        Log.d(TAG, "SaveEntryToDataBase: ${this.eventName} ${this.event} ${CONST.dateTimeFormat.format(this.timestamp)}")
        if (metadata != null) {
            //val logID = "${CONST.dateTimeFormat.format(this.timestamp)} ${this.eventName.name}"
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

    fun getSavedIntentions() {
        val intentionRef = database.child(CONST.firebaseReferenceUsers).child(userID).child(CONST.firebaseReferenceIntentions)
//        intentionRef.addValueEventListener(object : ValueEventListener {
        intentionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "OnDataChange: IntentionChanged")
                for (postSnapshot in snapshot.children) {
                    val value = postSnapshot.getValue(String::class.java)
                    if (!intentionList.contains(value)) intentionList.add(value)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled $error")
            }
        })
    }
}