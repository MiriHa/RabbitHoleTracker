package com.example.trackingapp.activity.login

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
import com.example.trackingapp.R
import com.example.trackingapp.databinding.FragmentLoginBinding
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.navigate

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginSignUpViewModel
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginSignUpViewModel::class.java]

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val loadingProgressBar = binding.loading

        viewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })

        viewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showLoginFailed(it)
                }
                loginResult.success?.let {
                    goToMainScreen(it)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.loginInWithEmailandPassword(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            if(viewModel.isButtonEnabled) {
                loadingProgressBar.visibility = View.VISIBLE
                viewModel.loginInWithEmailandPassword(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            } else {
                showLoginFailed(R.string.invalid_data_error)
            }
        }
    }

    private fun goToMainScreen(model: LoggedInUserView) {
        if(viewModel.hasUserFinishedOnboarding()) {
            val welcome = getString(R.string.welcome)
            val appContext = context?.applicationContext ?: return
            Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
            navigate(to = ScreenType.HomeScreen, from = ScreenType.Login)
        } else {
            navigate(to = ScreenType.Permission, from = ScreenType.Login)
        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}