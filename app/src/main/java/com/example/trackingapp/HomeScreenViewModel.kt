package com.example.trackingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeScreenViewModel: ViewModel() {

    fun clickInfo(){

    }


}

class HomeScreenViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            return HomeScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}