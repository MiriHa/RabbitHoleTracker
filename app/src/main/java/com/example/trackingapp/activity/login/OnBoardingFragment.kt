package com.example.trackingapp.activity.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.databinding.FragmentOnboradingBinding
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.navigate

class OnBoardingFragment: Fragment() {

    private lateinit var viewModel: OnBoardingViewModel
    private lateinit var binding: FragmentOnboradingBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, WelcomeViewModelFactory())[OnBoardingViewModel::class.java]

        binding = FragmentOnboradingBinding.inflate(inflater)
        val view = binding.root

        /*//TODO
        if(AuthManager.user == AppUser.NoUserLoggedIn){
            Log.d("xxx","NoUserLoggedIN")
            viewModel.goToHomeScreen()
        } */

        binding.onboradingLoginButton.setOnClickListener {
            Log.d("xxx","LoginClick")
            navigate(ScreenType.Login, ScreenType.Welcome)
        }
        binding.onboradingSignupButton.setOnClickListener {
            Log.d("xxx","Signup")
            navigate(ScreenType.SignUp, ScreenType.Welcome)
        }
        binding.onboardingTestBUtton.setOnClickListener {
            Log.d("xxx","Home")
            navigate(ScreenType.HomeScreen, ScreenType.Welcome)
        }

        return view

        /*return ComposeView(requireContext()).apply {
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
        }*/
    }
}