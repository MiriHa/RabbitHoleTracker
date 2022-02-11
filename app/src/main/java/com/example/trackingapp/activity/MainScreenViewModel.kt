package com.example.trackingapp.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainScreenViewModel: ViewModel() {

    val _isLoggingActivetest = MutableLiveData<Boolean>()
    val isLoggingActivetest: LiveData<Boolean> = _isLoggingActivetest



}

class MainScreenViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            return MainScreenViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}