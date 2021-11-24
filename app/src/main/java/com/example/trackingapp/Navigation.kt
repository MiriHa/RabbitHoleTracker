package com.example.trackingapp

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.security.InvalidParameterException

enum class ScreenType {Welcome, SignUP, Login, HomeScreen}

fun Fragment.navigate(to: ScreenType, from: ScreenType) {
    if(to == from){
        throw InvalidParameterException("Can't navigate to same screen type!")
    }
    when(to){
        ScreenType.Welcome -> {
            findNavController().navigate(R.id.welcomeFragment)
        }
        ScreenType.SignUP -> {
            findNavController().navigate(R.id.signUpFragment)
        }
        ScreenType.Login -> {
            findNavController().navigate(R.id.loginFragment)
        }
        ScreenType.HomeScreen -> {
            findNavController().navigate(R.id.homeScreenFragment)
        }
    }

}