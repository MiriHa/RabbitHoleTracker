package com.example.trackingapp.sensor.usage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.InstallEventType
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST

class AppInstallsSensor : AbstractSensor(
    "APP_INSTALLS_SENSOR",
    "App installs"
) {

    private lateinit var mContext: Context
    private lateinit var mReceiver: BroadcastReceiver

    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")


        //InstAppReceiver
        val filter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED)
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        filter.addDataScheme("package")

        mContext = context
        mReceiver = InstAppReceiver()
        try {
            context.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        context.registerReceiver(mReceiver, filter)
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext.unregisterReceiver(mReceiver)
        }
    }

    private fun saveEntry(type: InstallEventType, packageName: String, appLabel: String, timestamp: Long) {
        //generating UsageActivity and inserting it into db
        LogEvent(LogEventName.APPS_INSTALL, timestamp, packageName = packageName, event = type.name, name = appLabel).saveToDataBase()
    }

    inner class InstAppReceiver : BroadcastReceiver() {
        private val LABEL_UNKNOWN = "UNKNOWN"
        override fun onReceive(context: Context, intent: Intent) {
            if (!LoggingManager.isDataRecordingActive) {
                return
            }

            //get package name from intent
            val uri = intent.data
            val packageName = uri?.schemeSpecificPart
            val action = intent.action
            val timestamp = System.currentTimeMillis()
            if (packageName != null) {
                //get event type
                var type: InstallEventType = InstallEventType.UNKNOWN
                if (action == Intent.ACTION_PACKAGE_ADDED) {
                    Log.i(TAG, "$packageName Package was added")
                    // checkIfSpotiyGetInstalled(context, packageName);
                    type = InstallEventType.INSTALLED
                } else if (action == Intent.ACTION_PACKAGE_REPLACED) {
                    Log.i(TAG, "Package was replaced")
                    type = InstallEventType.UPDATED
                    //   setFlagForSpotifyInSharedPref();
                } else if (action == Intent.ACTION_PACKAGE_REMOVED) {
                    Log.i(TAG, "Package was removed")
                    type = InstallEventType.UNINSTALLED
                    //  removeFlagForSpotifyInSharedPref(packageName);
                } else if (action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {
                    Log.i(TAG, "Package was removed and data removed")
                    type = InstallEventType.UNINSTALLED_AND_DATA_REMOVED
                } else if (action == Intent.ACTION_PACKAGE_DATA_CLEARED) {
                    Log.i(TAG, "Package data was cleared")
                    type = InstallEventType.DATA_CLEARED
                } else {
                    Log.i(TAG, "Unkown action was detected: $action")
                }

                //get application label
                var label = LABEL_UNKNOWN
                if (type.equals(InstallEventType.UNINSTALLED) || type.equals(InstallEventType.UNINSTALLED_AND_DATA_REMOVED)) {
                    //TODO someway get uninstalled app packagename?
                } else {
                    try {
                        val packageManager = context.packageManager
                        val info = packageManager.getApplicationInfo(packageName, 0)
                        label = packageManager.getApplicationLabel(info).toString()
                        Log.i(TAG, "Resolved app name: $label")
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                        Log.e(TAG, "Couldn't resolve ApplicationInfo from supplied packageName")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(TAG, "Unexpected Exception")
                    }
                }
                saveEntry(type, packageName, label, timestamp)
            } else {
                Log.e(TAG, "Package name couldn't be resolved - ignoring event")
            }
        }
    }
}
