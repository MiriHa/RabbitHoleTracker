package com.example.trackingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.trackingapp.composables.LoginScreen

class LoginFragment(private val viewModel: LoginViewModel) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LoginScreen(viewModel = viewModel)
            }
        }

    }

    companion object {
        fun newInstance(viewModel: LoginViewModel) = LoginFragment(viewModel)
    }

}