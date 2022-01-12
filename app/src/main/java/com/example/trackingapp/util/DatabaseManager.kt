package com.example.trackingapp

import android.util.Log
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.example.trackingapp.models.User
import com.example.trackingapp.util.CONST
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

    val intentionList: MutableSet<String?> = HashSet()

    val isUserLoggedIn: Boolean
        get() = user != null

    init {
        intentionList.addAll(arrayOf("Browsing", "Passing Time", "Search for Information"))
        getSavedIntentions()
    }

    fun saveUserToFirebase(email: String){
        val userId = Firebase.auth.currentUser?.uid
        val user = User(email, userId)
        if (userId != null) {
            database.child(CONST.firebaseReferenceUsers)
                .child(userId)
                .setValue(user)

            Event(
                EventName.LOGIN,
                CONST.dateTimeFormat.format(System.currentTimeMillis()),
                "Account created"
            ).saveToDataBase()
        }

    }

    fun Event.saveToDataBase(){
        Log.d(TAG, "SaveEntryToDataBase: ${this.eventName} ${this.event}")
        user?.let {
            Firebase.database.reference.child(CONST.firebaseReferenceUsers)
                .child(it.uid)
                .child(CONST.firebaseReferenceLogs)
                .child("${this.timestamp} ${this.eventName.name}")
                .setValue(this)
        }
    }

    fun saveEventToFireBase(eventName: EventName, timestamp: Long, name: String? = null, description: String? = null){
        Log.d("xxx", "SaveEntryToDataBase: ${eventName} ${name}")
        //Event(EventName.INTERNET, CONST.dateTimeFormat.format(timestamp), name, description).saveToDataBase()
    }

    fun saveIntentionToFirebase(time: Date, intention: String){
        val timestamp = CONST.dateTimeFormat.format(time)
        user?.let {
            database.child(CONST.firebaseReferenceUsers)
                .child(it.uid)
                .child(CONST.firebaseReferenceIntentions)
                .child("$timestamp $intention")
                .setValue(intention)
        }
    }

    fun getSavedIntentions(){
        val intentionRef = database.child(CONST.firebaseReferenceUsers).child(userID).child(CONST.firebaseReferenceIntentions)
        intentionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val value = postSnapshot.getValue(String::class.java)
                    intentionList.add(value)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled", error.toString())
            }
        })
    }
}