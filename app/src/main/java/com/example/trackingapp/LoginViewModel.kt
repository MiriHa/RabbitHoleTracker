package com.example.trackingapp

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    fun login(email: String, password: String){
        AuthManager.signInWithEmail(email, password)
    }

}

/*
class LoginViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/
