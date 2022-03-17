package com.lmu.trackingapp.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lmu.trackingapp.util.DatabaseManager
import com.lmu.trackingapp.databinding.FragmentOnboradingBinding
import com.lmu.trackingapp.util.*

class OnBoardingFragment: Fragment() {

    val TAG = "ONBORADING_FRAGMENT"
    private lateinit var binding: FragmentOnboradingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboradingBinding.inflate(inflater)
        val view = binding.root

        binding.onboradingLoginButton.setOnClickListener {
            navigate(to=ScreenType.Login, from=ScreenType.Welcome)
        }
        binding.onboradingSignupButton.setOnClickListener {
            navigate(to=ScreenType.SignUp, from=ScreenType.Welcome)
        }

        return view
    }

    override fun onStart() {
        super.onStart()
       if(DatabaseManager.isUserLoggedIn){
           //Go to the MainScreen or Permissionscreen
           if(PermissionManager.areAllPermissionGiven(this.activity)) {
               SharedPrefManager.saveBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE, true)
               navigate(to=ScreenType.HomeScreen, from=ScreenType.Welcome)
           } else {
               navigate(to=ScreenType.Permission, from=ScreenType.Welcome)
           }
       } else {
           SharedPrefManager.saveBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE, false)
       }

    }
}