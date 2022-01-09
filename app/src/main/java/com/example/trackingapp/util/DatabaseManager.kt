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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

object DatabaseManager {

    val user: FirebaseUser?
        get() = Firebase.auth.currentUser

    val userID: String
        get() = user?.uid ?: ""

    private val database: DatabaseReference
        get() = Firebase.database.reference

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
            database.child(CONST.firebaseReferenceUsers).child(userId).setValue(user)
            Event(
                EventName.LOGIN,
                CONST.dateTimeFormat.format(System.currentTimeMillis()),
                "Account created"
            ).saveToDataBase()
        }

    }

    fun Event.saveToDataBase(){
        user?.let {
            database.child(CONST.firebaseReferenceUsers)
                .child(it.uid)
                .child(CONST.firebaseReferenceLogs)
                .child("${this.timestamp} ${this.eventName}")
                .setValue(this)
        }
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