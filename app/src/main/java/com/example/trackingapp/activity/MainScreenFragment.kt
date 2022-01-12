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
            text =
                if (LoggingManager.isServiceRunning(mContext)) getString(R.string.logging_stop_button) else getString(
                    R.string.logging_start_button)
            setOnClickListener {
                Log.d(TAG, "startLoggingButton Click: running")
                val isServiceRunning = LoggingManager.isServiceRunning(mContext)
                if (!isServiceRunning) {
                    LoggingManager.startLoggingService(mContext as Activity)
                    Event(EventName.LOGIN, CONST.dateTimeFormat.format(System.currentTimeMillis()), "startLoggingService","test").saveToDataBase()
                    //text = getString(R.string.logging_stop_button)
                } else {
                    LoggingManager.stopLoggingService(mContext)
                   // text = getString(R.string.logging_start_button)
                }
            }
        }

        binding.signOut.setOnClickListener {
            Firebase.auth.signOut()
            navigate(ScreenType.Welcome, ScreenType.HomeScreen)
        }

        this.activity?.let {
            val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)
            managePermissions.checkPermissions()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


}