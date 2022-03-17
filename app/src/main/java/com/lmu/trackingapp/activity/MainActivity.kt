package com.lmu.trackingapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lmu.trackingapp.util.DatabaseManager
import com.lmu.trackingapp.R
import com.lmu.trackingapp.databinding.ActivityMainBinding
import com.lmu.trackingapp.util.CONST
import com.lmu.trackingapp.util.SharedPrefManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        try {
            Firebase.database.setPersistenceEnabled(true)
        } catch (e: Exception){ }

        SharedPrefManager.init(this.applicationContext)
        DatabaseManager.initIntentionList()

        auth = Firebase.auth

        CONST.currentLocale = this.resources.configuration.locales.get(0);

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }


}