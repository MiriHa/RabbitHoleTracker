package com.example.trackingapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.util.Event
import com.example.trackingapp.util.ScreenType

class OnBoardingViewModel: ViewModel() {
    private val mNavigateTo = MutableLiveData<Event<ScreenType>>()
    val navigateTo: LiveData<Event<ScreenType>> = mNavigateTo


    fun login(){
        //TODO mNavigateTo.value = Event(ScreenType.Login)
        goToHomeScreen()
    }

    fun signup() {
        mNavigateTo.value = Event(ScreenType.SignUpLogin)
    }

    fun goToHomeScreen(){
        mNavigateTo.value = Event(ScreenType.HomeScreen)
    }

}

class WelcomeViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnBoardingViewModel::class.java)) {
            return OnBoardingViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}