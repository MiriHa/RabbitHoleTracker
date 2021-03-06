package com.lmu.trackingapp.activity.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.lmu.trackingapp.R
import com.lmu.trackingapp.databinding.FragmentSignupBinding
import com.lmu.trackingapp.util.DatabaseManager.createEmailFromString
import com.lmu.trackingapp.util.PermissionManager
import com.lmu.trackingapp.util.ScreenType
import com.lmu.trackingapp.util.navigate

class SignUpFragment: Fragment() {
    private lateinit var viewModel: LoginSignUpViewModel
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginSignUpViewModel::class.java]

        val studyID = binding.signUpNickname
        val passwordEditText = binding.signUpPassword
        val passwordRepeatEditText = binding.signUpRepeatPassword
        val createAccountButton = binding.signUpButton
        val loadingProgressBar = binding.loading

        viewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                createAccountButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    studyID.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
                loginFormState.passwordMatchError?.let {
                    passwordRepeatEditText.error = getString(it)
                }
            })

        viewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let { showSignUpFailed(it) }
                loginResult.success?.let { goToMainScreen() }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                viewModel.loginDataChanged(
                    studyID.text.toString(),
                    passwordEditText.text.toString(),
                    passwordRepeatEditText.text.toString()
                )
            }
        }
        studyID.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordRepeatEditText.addTextChangedListener(afterTextChangedListener)
        passwordRepeatEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.createEmailPasswordAccount(
                    studyID.text.toString().createEmailFromString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        createAccountButton.setOnClickListener {
            if(viewModel.isButtonEnabled) {
                loadingProgressBar.visibility = View.VISIBLE
                viewModel.createEmailPasswordAccount(
                    studyID.text.toString().createEmailFromString(),
                    passwordEditText.text.toString()
                )
            } else {
                showSignUpFailed(R.string.invalid_data_error)
            }
        }
        return binding.root
    }

    private fun goToMainScreen() {
        if(PermissionManager.areAllPermissionGiven(this.activity)) {
            navigate(ScreenType.HomeScreen, ScreenType.SignUp)
        } else {
            navigate(ScreenType.Permission, ScreenType.SignUp)
        }
    }

    private fun showSignUpFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }
}