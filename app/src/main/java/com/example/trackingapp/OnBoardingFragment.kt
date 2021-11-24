package com.example.trackingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.composables.OnBoardingScreen
import com.example.trackingapp.composables.WelcomeEvent
import com.example.trackingapp.ui.theme.TrackingAppTheme
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.navigate

class OnBoardingFragment: Fragment() {

    private lateinit var viewModel: OnBoardingViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, WelcomeViewModelFactory())[OnBoardingViewModel::class.java]

        /*//TODO
        if(AuthManager.user == AppUser.NoUserLoggedIn){
            Log.d("xxx","NoUserLoggedIN")
            viewModel.goToHomeScreen()
        } */

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, ScreenType.Welcome)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                TrackingAppTheme() {
                    OnBoardingScreen(
                        onEvent = { event ->
                            when (event) {
                                is WelcomeEvent.SignInSignUp -> viewModel.signup()
                                is WelcomeEvent.LoginIn -> viewModel.login()
                            }
                        }
                    )
                }
            }
        }
    }
}