package com.example.trackingapp.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.databinding.FragmentSignupBinding

class SignUpFragment: Fragment() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentSignupBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        binding = FragmentSignupBinding.inflate(inflater)

        return binding.root
    }
}