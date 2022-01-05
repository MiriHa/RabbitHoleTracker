package com.example.trackingapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trackingapp.R
import com.example.trackingapp.databinding.ActivityMainBinding
import com.example.trackingapp.util.CONST
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.name
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        //firebaseAuth = Firebase.auth

        //val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        //setSupportActionBar(toolbar)

        CONST.setSavePath(this)
        //TODO
        /*
        isPermissionGranted(Manifest.permission.WAKE_LOCK)
        isPermissionGranted(Manifest.permission.RECORD_AUDIO)
        isPermissionGranted(Manifest.permission.ACCESS_WIFI_STATE)
        isPermissionGranted(Manifest.permission.RECEIVE_BOOT_COMPLETED)
        isPermissionGranted(Manifest.permission.READ_PHONE_STATE)
        isPermissionGranted(Manifest.permission.ACCESS_NETWORK_STATE)
        isPermissionGranted(Manifest.permission.FOREGROUND_SERVICE)
        if (checkPermission()) {
            requestPermission()
        }
         */
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val result1 = ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    fun isPermissionGranted(permission: String): Boolean {
        Log.d(TAG, "Check Permission")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v(TAG, "Permission is granted")
                true
            } else {
                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            //resume tasks needing this permission
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //return super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /*fun isLogServiceRunning(context: Context): Boolean {
        val sp = context.getSharedPreferences(
            CONST.SP_LOG_EVERYTHING,
            AppCompatActivity.MODE_PRIVATE
        )
        return sp.getBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false)
    }*/


/*
    private val TAG = javaClass.name

    private lateinit var binding: ActivityMainBinding
    private lateinit var m_List: ListView
    private lateinit var m_ButtonAccessibility: Button
    private lateinit var m_ButtonStart: Button
    private lateinit var m_ButtonStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setContentView(R.layout.activity_main)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        CONST.setSavePath(this)
        m_List = binding.sensorList
        m_ButtonStart = binding.startButton
        m_ButtonStop = binding.stopButton
        m_ButtonAccessibility = binding.accessibilityButton
        setAccessibilityButtonState()
        val db = SensorDatabaseHelper(this)
        SensorList.getList(this)
        val adapter = SensorAdapter(this, db.cursor)
        m_List.adapter = adapter
        m_List.itemsCanFocus = false
        m_ButtonStart.setOnClickListener(onStartButtonClick)
        m_ButtonStop.setOnClickListener(onStopButtonClick)
        m_ButtonAccessibility.setOnClickListener(onAccessibilityButtonClick)
        isPermissionGranted(Manifest.permission.WAKE_LOCK)
        isPermissionGranted(Manifest.permission.RECORD_AUDIO)
        isPermissionGranted(Manifest.permission.ACCESS_WIFI_STATE)
        isPermissionGranted(Manifest.permission.RECEIVE_BOOT_COMPLETED)
        isPermissionGranted(Manifest.permission.READ_PHONE_STATE)
        isPermissionGranted(Manifest.permission.ACCESS_NETWORK_STATE)
        isPermissionGranted(Manifest.permission.FOREGROUND_SERVICE)
        if (checkPermission()) {
            requestPermission()
        }

        //Thread threadUdp = new Thread(new UDPReceiver(7001, view));
        //threadUdp.start();
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val result1 = ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    fun isPermissionGranted(permission: String): Boolean {
        ModelLog.d(TAG, "Check Permission")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                ModelLog.v(TAG, "Permission is granted")
                true
            } else {
                ModelLog.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            ModelLog.v(TAG, "Permission is granted")
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ModelLog.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            //resume tasks needing this permission
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //return super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        m_List!!.isEnabled = true
        if (isLogServiceRunning(this)) {
            m_ButtonStart!!.visibility = View.GONE
            m_ButtonStop!!.visibility = View.VISIBLE
            ModelLog.d(TAG, "RESUME: service active")
        } else {
            m_ButtonStart!!.visibility = View.VISIBLE
            m_ButtonStop!!.visibility = View.GONE
            ModelLog.d(TAG, "RESUME: service inactive")
        }
    }

    private val onStartButtonClick = View.OnClickListener {
        m_ButtonStart!!.visibility = View.GONE
        m_ButtonStop!!.visibility = View.VISIBLE
        ModelLog.d(TAG, "START TRACKING!")
        startLogService(this@MainActivity)
    }

    private val onStopButtonClick = View.OnClickListener {
        m_ButtonStart!!.visibility = View.VISIBLE
        m_ButtonStop!!.visibility = View.GONE
        stopLogService(this@MainActivity)
    }

    private val onAccessibilityButtonClick = View.OnClickListener {
        if (!isAccessibilityServiceEnabled(this@MainActivity)) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivityForResult(intent, 0)
        }
        setAccessibilityButtonState()
    }

    private fun setAccessibilityButtonState() {
        if (!isAccessibilityServiceEnabled(this)) {
            m_ButtonAccessibility!!.setTextColor(Color.RED)
            m_ButtonAccessibility!!.setText(R.string.accessibility_button_Off)
        } else {
            m_ButtonAccessibility!!.setTextColor(Color.GREEN)
            m_ButtonAccessibility!!.setText(R.string.accessibility_button_On)
        }
    }


    private fun getPendingIntent(context: Context): PendingIntent {
        val alarmIntent = Intent(context.applicationContext, LogService::class.java)
        return PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    fun startLogService(context: Context) {
        val intent = Intent(context, LogService::class.java)
        context.startService(intent)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(context)
        val m_AlarmInterval = (60 * 1000).toLong()
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + m_AlarmInterval,
            m_AlarmInterval,
            pendingIntent
        )
        val sp = context.getSharedPreferences(CONST.SP_LOG_EVERYTHING, MODE_PRIVATE)
        sp.edit().putBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, true).apply()
    }

    private fun stopLogService(context: Context) {
        val intent = Intent(context, LogService::class.java)
        context.stopService(intent)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(context)
        alarmManager.cancel(pendingIntent)
        val sp = context.getSharedPreferences(CONST.SP_LOG_EVERYTHING, MODE_PRIVATE)
        sp.edit().putBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false).apply()
    }

    fun isLogServiceRunning(context: Context): Boolean {
        val sp = context.getSharedPreferences(CONST.SP_LOG_EVERYTHING, MODE_PRIVATE)
        return sp.getBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false)
    }

    private fun isAccessibilityServiceEnabled(context: Context): Boolean {
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            ModelLog.d(TAG, e.toString())
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(
                            AccessibilityLogService.SERVICE,
                            ignoreCase = true
                        )
                    ) {
                        return true
                    }
                }
            }
        }
        return false
    }

 */
}