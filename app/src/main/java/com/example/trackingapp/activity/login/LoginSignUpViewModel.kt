package com.example.trackingapp.activity.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.util.DatabaseManager
import com.example.trackingapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val passwordMatchError: Int? = null,
    val isDataValid: Boolean = false
)

data class LoginResult(
    val success: Boolean? = null,
    val error: Int? = null
)

class LoginSignUpViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    var isButtonEnabled = false

    fun loginInWithEmailAndPassword(email: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.d("LoginSignUpViewModel:","Login successful")
                _loginResult.value =
                    LoginResult(success = true)
            } else{
                Log.d("LoginSignUpViewModel:","Login failed")
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun createEmailPasswordAccount(username: String, email: String, password: String){
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.d("LoginSignUpViewModel:","Create Account successful")
                DatabaseManager.saveUserToFirebase(username, email)
                _loginResult.value =
                    LoginResult(success = true)
            } else{
                Log.d("LoginSignUpViewModel:","Create Account failed")
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String, passwordRepeat: String? = null) {
        if (!isEmalValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if(!doPasswordsMatch(password, passwordRepeat)) {
            _loginForm.value = LoginFormState(passwordMatchError = R.string.invalid_password_no_match)
        } else {
            isButtonEnabled = true
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun doPasswordsMatch(password: String, passwordRepeat: String?):Boolean{
        return password == passwordRepeat
    }

    private fun isEmalValid(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}

class LoginViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginSignUpViewModel::class.java)) {
            return LoginSignUpViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}