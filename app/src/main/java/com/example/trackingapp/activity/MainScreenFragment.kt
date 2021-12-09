package com.example.trackingapp.activity


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.trackingapp.R
import com.example.trackingapp.SensorDatabaseHelper
import com.example.trackingapp.adapter.SensorAdapter
import com.example.trackingapp.databinding.FragmentMainscreenBinding
import com.example.trackingapp.sensor.SensorList
import com.example.trackingapp.service.AccessibilityLogService
import com.example.trackingapp.service.AccessibilityLogService.TAG
import com.example.trackingapp.service.LogService
import com.example.trackingapp.util.CONST

class MainScreenFragment: Fragment() {

    private val TAG = javaClass.name

    private lateinit var binding: FragmentMainscreenBinding
    private lateinit var m_List: ListView
    private lateinit var m_ButtonAccessibility: Button
    private lateinit var m_ButtonStart: Button
    private lateinit var m_ButtonStop: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //viewModel = ViewModelProvider(this, HomeScreenViewModelFactory())[HomeScreenViewModel::class.java]

        binding = FragmentMainscreenBinding.inflate(inflater)
        val view = binding.root

        m_List = binding.sensorList
        m_ButtonStart = binding.startButton
        m_ButtonStop = binding.stopButton
        m_ButtonAccessibility = binding.accessibilityButton
        setAccessibilityButtonState()
        val db = SensorDatabaseHelper(activity)
        SensorList.getList(activity)
        val adapter = SensorAdapter(activity, db.cursor)
        m_List.adapter = adapter
        m_List.itemsCanFocus = false
        m_ButtonStart.setOnClickListener(onStartButtonClick)
        m_ButtonStop.setOnClickListener(onStopButtonClick)
        m_ButtonAccessibility.setOnClickListener(onAccessibilityButtonClick)

        //Thread threadUdp = new Thread(new UDPReceiver(7001, view));
        //threadUdp.start();
        return view
    }

    override fun onResume() {
        super.onResume()
        m_List.isEnabled = true
        val context = activity
        if(context != null) {
            if (isLogServiceRunning(context)) {
                m_ButtonStart.visibility = View.GONE
                m_ButtonStop.visibility = View.VISIBLE
                Log.d(TAG, "RESUME: service active")
            } else {
                m_ButtonStart.visibility = View.VISIBLE
                m_ButtonStop.visibility = View.GONE
                Log.d(TAG, "RESUME: service inactive")
            }
        }
    }

    private val onStartButtonClick = View.OnClickListener {
        m_ButtonStart.visibility = View.GONE
        m_ButtonStop.visibility = View.VISIBLE
        Log.d(TAG, "START TRACKING!")
        activity?.let { it1 -> startLogService(it1) }
    }

    private val onStopButtonClick = View.OnClickListener {
        m_ButtonStart.visibility = View.VISIBLE
        m_ButtonStop.visibility = View.GONE
        activity?.let { it1 -> stopLogService(it1) }
    }

    private val onAccessibilityButtonClick = View.OnClickListener {
        val context = activity
        if(context != null) {
            if (!isAccessibilityServiceEnabled(context)) {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                val getResult =
                    registerForActivityResult(
                        ActivityResultContracts.StartActivityForResult()){
                        if(it.resultCode == Activity.RESULT_OK){
                            val value = it.data?.getStringExtra("input")
                        }
                    }
                getResult.launch(intent)

               // startActivityForResult(intent, 0)
            }
            setAccessibilityButtonState()
        }
    }

    private fun setAccessibilityButtonState() {
        val context = activity
        if(context != null) {
            if (!isAccessibilityServiceEnabled(context = context)) {
                m_ButtonAccessibility.setTextColor(Color.RED)
                m_ButtonAccessibility.setText(R.string.accessibility_button_Off)
            } else {
                m_ButtonAccessibility.setTextColor(Color.GREEN)
                m_ButtonAccessibility.setText(R.string.accessibility_button_On)
            }
        }
    }

    companion object{
        fun isLogServiceRunning(context: Context): Boolean {
            val sp = context.getSharedPreferences(
                CONST.SP_LOG_EVERYTHING,
                AppCompatActivity.MODE_PRIVATE
            )
            return sp.getBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false)
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val alarmIntent = Intent(context.applicationContext, LogService::class.java)
            return PendingIntent.getService(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        fun startLogService(context: Context) {
            val intent = Intent(context, LogService::class.java)
            context.startService(intent)
            val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val pendingIntent = getPendingIntent(context)
            val m_AlarmInterval = (60 * 1000).toLong()
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + m_AlarmInterval,
                m_AlarmInterval,
                pendingIntent
            )
            val sp = context.getSharedPreferences(
                CONST.SP_LOG_EVERYTHING,
                AppCompatActivity.MODE_PRIVATE
            )
            sp.edit().putBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, true).apply()
        }

        private fun stopLogService(context: Context) {
            val intent = Intent(context, LogService::class.java)
            context.stopService(intent)
            val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val pendingIntent = getPendingIntent(context)
            alarmManager.cancel(pendingIntent)
            val sp = context.getSharedPreferences(
                CONST.SP_LOG_EVERYTHING,
                AppCompatActivity.MODE_PRIVATE
            )
            sp.edit().putBoolean(CONST.KEY_LOG_EVERYTHING_RUNNING, false).apply()
        }

        private fun isAccessibilityServiceEnabled(context: Context): Boolean {
            var accessibilityEnabled = 0
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                )
            } catch (e: Settings.SettingNotFoundException) {
                Log.d(TAG, e.toString())
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
    }
}