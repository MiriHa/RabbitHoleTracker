package com.example.trackingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.ui.theme.TrackingAppTheme

class HomeScreenFragment: Fragment() {

    private lateinit var viewModel: HomeScreenViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, HomeScreenViewModelFactory())[HomeScreenViewModel::class.java]
        
        return ComposeView(requireContext()).apply { 
            setContent { 
                TrackingAppTheme() {
                    
                }
            }
        }
    }

}