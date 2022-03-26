package com.lmu.trackingapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lmu.trackingapp.R
import com.lmu.trackingapp.databinding.FragmentMainscreenBinding
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.service.LoggingManager
import com.lmu.trackingapp.util.*
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase

class MainScreenFragment : Fragment() {

    private lateinit var binding: FragmentMainscreenBinding
    private lateinit var mContext: Context
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (PermissionManager.areAllPermissionGiven(this.activity)) {
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE, true)
        }

        binding = FragmentMainscreenBinding.inflate(inflater)
        val view = binding.root

        setHasOptionsMenu(true)

        notificationManager = NotificationManagerCompat.from(mContext)
        NotificationHelper.dismissESMNotification(mContext)

        if (!SharedPrefManager.getBoolean(CONST.PREFERENCES_LOGGING_FIRST_STARTED)) {
            NotificationHelper.dismissSurveyNotification(mContext)
            val currentSessionID = LoggingManager.generateSessionID(System.currentTimeMillis())
            SharedPrefManager.saveCurrentSessionID(currentSessionID)
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE, true)
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_USER_PRESENT, true)
            LoggingManager.startLoggingService(mContext as Activity)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = MainScreenAdapter(this)
        binding.viewpagerMainScreenFragment.adapter = adapter
        TabLayoutMediator(binding.tabLayoutMainScreenFragment, binding.viewpagerMainScreenFragment) { tab, position ->
            tab.text = when (position) {
                0 -> mContext.getString(R.string.mainScreen_tab_sensor_title)
                else -> mContext.getString(R.string.mainScreen_tab_contact_title)
            }
        }.attach()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        LoggingManager.ensureLoggingManagerIsAlive(mContext)
        LoggingManager.isStudyOver(mContext)
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_setting, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbarOption_logout -> {
               loggout()
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loggout(): Boolean {
        val dialog = AlertDialog.Builder(mContext)
            .setTitle(mContext.getString(R.string.logout_dialogTitle))
            .setPositiveButton(
                mContext.getString(R.string.logout_menu_title)
            ) { _, _ ->
                Firebase.auth.signOut()
                SharedPrefManager.saveBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE, false)
                LoggingManager.stopLoggingService(mContext)
                LogEvent(LogEventName.ADMIN, System.currentTimeMillis(), "LOGOUT",).saveToDataBase()
                navigate(ScreenType.Welcome, ScreenType.HomeScreen)
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity?.startActivity(intent)
                //dialog.cancel()
            }
            .setNegativeButton(mContext.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setMessage(mContext.getString(R.string.logout_dialog_description))
            .create()
            .show()
        return true
    }

    inner class MainScreenAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position) {
                0 -> SensorOverviewFragment()
                else -> ContactFragment()
            }
            return fragment
        }
    }

}

