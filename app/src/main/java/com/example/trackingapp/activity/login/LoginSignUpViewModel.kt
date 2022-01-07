package com.example.trackingapp.activity.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String?
    //... other data fields that may be accessible to the UI
)

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val passwordMatchError: Int? = null,
    val isDataValid: Boolean = false
)

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)


class LoginSignUpViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    var isButtonEnabled = false

    fun loginInWithEmailandPassword(email: String, password: String){
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.d("LoginSignUpViewModel:","Login successful")
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = DatabaseManager.user?.uid))
            } else{
                Log.d("LoginSignUpViewModel:","Login failed")
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun createEmailPasswordAccount(email: String, password: String){
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.d("LoginSignUpViewModel:","Create Account successful")
                DatabaseManager.saveUserToFirebase(email)
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = DatabaseManager.user?.uid))
            } else{
                Log.d("LoginSignUpViewModel:","Create Account failed")
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String, passwordRepeat: String? = null) {
        if (!isUserNameValid(username)) {
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

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
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