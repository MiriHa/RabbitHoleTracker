package com.example.trackingapp.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.trackingapp.R
import com.example.trackingapp.databinding.FragmentMainscreenBinding
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.*
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainScreenFragment: Fragment() {

    private lateinit var binding: FragmentMainscreenBinding
    private lateinit var mContext: Context
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if(PermissionManager.areAllPermissionGiven(this.activity)) {
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE, true)
        }

        binding = FragmentMainscreenBinding.inflate(inflater)
        val view = binding.root

        setHasOptionsMenu(true)

        notificationManager = NotificationManagerCompat.from(mContext)
        NotificationHelper.dismissESMNotification(mContext)

        if (!SharedPrefManager.getBoolean(CONST.PREFERENCES_LOGGING_FIRST_STARTED)) {
            NotificationHelper.dismissSurveyNotification(mContext)
            LoggingManager.startLoggingService(mContext as Activity)
        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = MainScreenAdapter(this)
        binding.viewpagerMainScreenFragment.adapter = adapter
        TabLayoutMediator(binding.tabLayoutMainScreenFragment, binding.viewpagerMainScreenFragment) { tab, position ->
            tab.text = when(position){
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
                Firebase.auth.signOut()
                SharedPrefManager.saveBoolean(CONST.PREFERENCES_DATA_RECORDING_ACTIVE, false)
                LoggingManager.stopLoggingService(mContext)
                navigate(ScreenType.Welcome, ScreenType.HomeScreen)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class MainScreenAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            val fragment = when (position){
                0 -> SensorOverviewFragment()
                else -> ContactFragment()
            }
            return fragment
        }
    }

}

