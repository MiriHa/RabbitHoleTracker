package com.example.trackingapp.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.databinding.FragmentOnboradingBinding
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.SharedPrefManager
import com.example.trackingapp.util.navigate

class OnBoardingFragment: Fragment() {

    private lateinit var binding: FragmentOnboradingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboradingBinding.inflate(inflater)
        val view = binding.root

        binding.onboradingLoginButton.setOnClickListener {
            navigate(ScreenType.Login, ScreenType.Welcome)
        }
        binding.onboradingSignupButton.setOnClickListener {
            navigate(ScreenType.SignUp, ScreenType.Welcome)
        }
        binding.onboardingTestBUtton.setOnClickListener {
            if(SharedPrefManager.getBoolean(CONST.PREFERENCES_ONBOARDING_FINISHED)) {
                navigate(ScreenType.HomeScreen, ScreenType.Welcome)
            } else {
                navigate(ScreenType.Permission, ScreenType.Welcome)
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
       if(DatabaseManager.isUserLoggedIn){
           //G0 to the MainScreen
           navigate(ScreenType.HomeScreen, ScreenType.Welcome)
       }

    }
}