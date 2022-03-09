package com.example.trackingapp.sensor.usage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.trackingapp.util.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * AsyncTask to retrieve all installed apps on the phone.
 */
object PhoneState {
    var TAG = "PhoneState"

    fun logCurrentPhoneState(context: Context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            CoroutineScope(Dispatchers.IO).launch {
                doInBackgroundApps(context)
                doInBackgroundData()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun doInBackgroundApps(context: Context): String? {
        val pm = context.packageManager
        var applicationName = ""
        var packageName: String

        //get a list of installed apps
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        //add new activity to db for each list item
        for (packageInfo in packages) {
            packageName = packageInfo.packageName
            val timestamp = System.currentTimeMillis()
            try {
                val appInfo = pm.getApplicationInfo(packageInfo.packageName, 0)
                applicationName = (pm.getApplicationLabel(appInfo)) as String
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            saveEntryApps(applicationName, packageName, timestamp)
        }
        return null
    }

    private fun saveEntryApps(applicationName: String?, packageName: String, timestamp: Long) {
        LogEvent(LogEventName.INSTALLED_APP, timestamp, name = applicationName, packageName = packageName).saveToDataBase()
    }


    private fun doInBackgroundData() {
        val timestamp = System.currentTimeMillis()
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val sdkVersion = Build.VERSION.SDK_INT
        val releaseVersion = Build.VERSION.RELEASE
        saveEntryData( timestamp, manufacturer, model, sdkVersion, releaseVersion)
    }

    private fun saveEntryData(
        timestamp: Long,
        manufacturer: String,
        model: String,
        sdkVersion: Int,
        releaseVersion: String
    ) {
        LogEvent(LogEventName.DEVICE_INFO, timestamp, event= model, description = releaseVersion, name = sdkVersion.toString(), packageName = manufacturer ).saveToDataBase()
    }

}
