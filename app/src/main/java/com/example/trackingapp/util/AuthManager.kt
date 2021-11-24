package com.example.trackingapp

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

sealed class AppUser {
    data class LoggedInUser(val email: String, val password: String): AppUser()
    object NoUserLoggedIn: AppUser()
}

object AuthManager {

    var user: AppUser = AppUser.NoUserLoggedIn

    fun signInWithEmail(email: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(email, password)
    }

    /*suspend fun signInWithEmail(email: String, password: String) : Unit = withContext(IO){
        Firebase.auth.signInWithEmailAndPassword(email, password)
    }
*/
    suspend fun signUpWithEmail(email: String, password: String) : Unit = withContext(IO) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task -> }
    }

}