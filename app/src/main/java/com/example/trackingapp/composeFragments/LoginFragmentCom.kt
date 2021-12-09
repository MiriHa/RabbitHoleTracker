package com.example.trackingapp.composeFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.trackingapp.composables.LoginScreen

enum class LoginSignUpScreenType{
    LOG_IN,
    SIGN_UP
}

class LoginFragmentCom(private val comViewModel: LoginComViewModel, val screenType: LoginSignUpScreenType) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LoginScreen()
            }
        }

    }
}