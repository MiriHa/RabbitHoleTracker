package com.example.trackingapp.activity


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.databinding.FragmentMainscreenBinding
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.PermissionManager
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.navigate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainScreenFragment : Fragment() {

    private val TAG = "TRACKINGAPP_Main_Screen_Fragment"

    private lateinit var binding: FragmentMainscreenBinding
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var mContext: Context

    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this, MainScreenViewModelFactory())[MainScreenViewModel::class.java]

        binding = FragmentMainscreenBinding.inflate(inflater)
        val view = binding.root
        notificationManager = NotificationManagerCompat.from(mContext)

        DatabaseManager.getSavedIntentions()

        binding.buttonTest.apply {
            text = getString(
                    R.string.logging_start_button)
            setOnClickListener {
                Log.d(TAG, "startLoggingButton Click: running")
                LoggingManager.isDataRecordingActive = true
                val isServiceRunning = LoggingManager.isServiceRunning(mContext)
                if (!isServiceRunning) {
                    LoggingManager.stopLoggingService(mContext)
                    LoggingManager.userPresent = true
                    LoggingManager.startLoggingService(mContext as Activity)
                    Event(EventName.LOGIN, System.currentTimeMillis(), "startLoggingService","test").saveToDataBase()
                    //text = getString(R.string.logging_stop_button)
            }
        }

        binding.buttonTestStop.apply {
            text = getString(R.string.logging_stop_button)
            setOnClickListener {
                LoggingManager.isDataRecordingActive = false
                LoggingManager.stopLoggingService(mContext)
                }
            }
        }

        binding.signOut.setOnClickListener {
            Firebase.auth.signOut()
            LoggingManager.isDataRecordingActive = false
            LoggingManager.stopLoggingService(mContext)
            navigate(ScreenType.Welcome, ScreenType.HomeScreen)
        }

        //TODO move to Onboarding
        this.activity?.let {
            val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)
            managePermissions.checkPermissions()
           val notifiperission = managePermissions.checkForNotificationListenerPermissionEnabled()
           //val accesibilityperission = managePermissions.checkAccessibilityPermission()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


}