package com.example.trackingapp.activity

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.R
import com.example.trackingapp.databinding.ActivityMainBinding
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.SharedPrefManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val TAG = "TRACKINGAPP_MAINACTIVITY"
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SharedPrefManager.init(this.applicationContext)
        DatabaseManager.initIntentionList()

        auth = Firebase.auth

        CONST.currentLocale = this.resources.configuration.locales.get(0);

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        //TODO
       // checkPermissions(PermissionManager.appPermissions)

       // val managePermissions = PermissionManager(this, CONST.PERMISSION_REQUEST_CODE)
       // managePermissions.checkPermissions()
    }

    private fun checkPermissions(givenPermissionsToVerify: List<String>) {
        givenPermissionsToVerify.forEach { permission ->
            if(permission.isNotBlank())
                isPermissionGrantedOrRequestIt(permission)
        }
    }

    fun hasAllRequestedPermissions(context: Context, givenPermissionsToVerify: List<String>): Boolean {
        Log.d(TAG, "Checking if all given permissions are present…")
        var hasPermissions = true
        for (onePermission in givenPermissionsToVerify) {
            if (!onePermission.isBlank()) {
                val permission = ContextCompat.checkSelfPermission(context, onePermission)
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    hasPermissions = false
                    break
                }
            }
        }
        return hasPermissions
    }

    /*
    private fun requestPermission(permission: String) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(permission)
                //intent.addCategory("android.intent.category.DEFAULT")
                //intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivity(intent)
            } catch (e: Exception) {
                //val intent = Intent()
                //intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
               // startActivity(intent)
            }
        } else {
            //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }*/

    fun isPermissionGrantedOrRequestIt(permission: String): Boolean {
        Log.d(TAG, "Check Permission")
        // ContextCompat.checkSelfPermission(AppApplication.applicationContext, permission)
        return if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted")
            true
        } else {
            Log.v(TAG, "Permission is revoked")
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
       // if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
                //resume tasks needing this permission
            }
       // }

    }

    /*fun isLogServiceRunning(context: Context): Boolean {
        val sp = context.getSharedPreferences(
            CONST.SP_LOG_EVERYTHING,
            AppCompatActivity.MODE_PRIVATE
        )
        return sp.getBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false)
    }*/


}