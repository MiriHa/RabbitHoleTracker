package com.example.trackingapp.activity


import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.databinding.CustomListItemBinding
import com.example.trackingapp.databinding.FragmentMainscreenBinding
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.*

class MainScreenFragment : Fragment() {

    private val TAG = "TRACKINGAPP_Main_Screen_Fragment"

    private lateinit var binding: FragmentMainscreenBinding
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var mContext: Context

    private lateinit var notificationManager: NotificationManagerCompat

    private lateinit var listAdapter: ListAdapter

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
        NotificationHelper.dismissESMNotification(mContext)

        LoggingManager.ensureLoggingManagerIsAlive(mContext)

        setAdapter()

        val loggingObserver = Observer<Boolean> {
            Log.d("xxx", "valuechanged")
            setAdapter()
        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        LoggingManager.isLoggingActive.observe(this, loggingObserver)

        val arePermissionGranted = checkPermissionsGranted(mContext as Activity)
        if (arePermissionGranted) {
            LoggingManager.isDataRecordingActive = true
            LoggingManager.userPresent = true
        }


        binding.buttonTest.apply {
            text = getString(
                R.string.mainScreen_logging_start_button
            )
            setOnClickListener {
                Log.d(TAG, "startLoggingButton Click: running")
                LoggingManager.stopLoggingService(mContext)
                LoggingManager.startLoggingService(mContext as Activity)
                LogEvent(LogEventName.LOGIN, System.currentTimeMillis(), "startLoggingService", "test").saveToDataBase()
                //text = getString(R.string.logging_stop_button)
            }

            binding.buttonTestStop.apply {
                text = getString(R.string.mainScreen_logging_stop_button)
                setOnClickListener {
                    LoggingManager.stopLoggingService(mContext)
                }
            }
        }

        binding.signOut.setOnClickListener {
            Toast.makeText(mContext, "IsRunning: ${LoggingManager.isLoggingActive.value}", Toast.LENGTH_LONG).show()
            /*Firebase.auth.signOut()
            LoggingManager.isDataRecordingActive = false
            LoggingManager.stopLoggingService(mContext)
            navigate(ScreenType.Welcome, ScreenType.HomeScreen)*/
        }

        return view
    }

    private fun setAdapter() {
        LoggingManager.sensorList?.let { sensors ->
            listAdapter = ListAdapter(sensors)
            binding.recyclerviewFragmentMainscreen.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(this@MainScreenFragment.mContext)
            }
        }
    }

    private fun checkPermissionsGranted(context: Activity): Boolean {
        val managePermissions = PermissionManager(context, CONST.PERMISSION_REQUEST_CODE)

        val permissionsGranted = managePermissions.arePermissionsGranted() == PackageManager.PERMISSION_GRANTED
        val notificationListenerEnabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            .contains(context.applicationContext.packageName)
        val accessibilityServiceEnabled = PermissionManager.isAccessibilityServiceEnabled(context)
        val usageStatsPermissionGranted = PermissionManager.isUsageInformationPermissionEnabled(context)

        return permissionsGranted && notificationListenerEnabled && accessibilityServiceEnabled && usageStatsPermissionGranted
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    inner class ListAdapter(val items: List<AbstractSensor>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: CustomListItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = CustomListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder) {
                with(items[position]) {
                    binding.textViewListItemTitle.text = this.sensorName + " Sensor"
                    Log.d("xxx", "bind: ${this.sensorName} running: ${this.isRunning}")
                    binding.textViewListItemSubtile.text =
                        if (LoggingManager.isLoggingActive.value == true) getString(R.string.mainScreen_sensorList_is_running) else getString(R.string.mainScreen_sensorList_not_running)
                    //  if (this.isRunning) getString(R.string.mainScreen_sensorList_is_running) else getString(R.string.mainScreen_sensorList_not_running)
                }
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

}