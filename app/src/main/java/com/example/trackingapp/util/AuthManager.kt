package com.example.trackingapp

import android.util.Log

import com.example.trackingapp.models.LogActivity
import com.example.trackingapp.models.ModelLog
import com.example.trackingapp.models.User
import com.example.trackingapp.util.CONST
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.*

object AuthManager {

    //var auth: FirebaseAuth = Firebase.auth

    val user: FirebaseUser?
        get() = Firebase.auth.currentUser

    private val database: DatabaseReference
        get() = Firebase.database.reference

    fun isUserLoggedIn(): Boolean{
        return user != null
    }

    fun saveUserToFirebase(email: String){
        val userId = Firebase.auth.currentUser?.uid
        val user = User(email, userId)
        if (userId != null) {
            Log.d("AuthManager:","saveUserToFireBase: $userId")
            database.child("users").child(userId).setValue(user)
            makeLog(Date(), LogActivity.LOGIN, "account Created")
        }

    }

    fun makeLog(time: Date, logActivity: LogActivity, details: String? = null){
        val randomLogId = "LOG_${UUID.randomUUID()}"
        val localDate = LocalDate.parse(CONST.dateFormat.format(time))
        //TODO also save hour of day extra?
        val log = ModelLog(logActivity, details, CONST.dateTimeFormat.format(time), localDate.dayOfWeek.toString() )
        Log.d("AuthManager:","makeLog: $logActivity")
        user?.let {
            database.child("users")
            .child(it.uid)
            .child(CONST.firebaseReferenceLogs)
            .child("$logActivity $randomLogId")
            .setValue(log)
        }
    }

}