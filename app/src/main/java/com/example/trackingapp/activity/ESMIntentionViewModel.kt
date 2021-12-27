package com.example.trackingapp.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ESMIntentionViewModel: ViewModel() {


}

class ESMIntentionViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ESMIntentionViewModel::class.java)) {
            return ESMIntentionViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}