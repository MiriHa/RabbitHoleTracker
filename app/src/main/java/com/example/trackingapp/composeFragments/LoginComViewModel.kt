package com.example.trackingapp.composeFragments

import androidx.lifecycle.ViewModel
import com.example.trackingapp.AuthManager

class LoginComViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    fun login(email: String, password: String){
        AuthManager.signInWithEmail(email, password)
    }

}

/*
class LoginViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginComViewModel::class.java)) {
            return LoginComViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/
