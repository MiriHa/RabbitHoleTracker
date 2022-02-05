package com.example.trackingapp.sensor.usage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.trackingapp.DatabaseManager.saveToDataBase
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
            CoroutineScope(Dispatchers.Main).launch {
                doInBackgroundApps(context)
                doInBackgroundData()
            }
        }
    }

    private fun doInBackgroundApps(context: Context): String? {
        val pm = context.packageManager
        var applicationName = ""
        var packageName: String

        //get a list of installed apps
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        Log.d("xxx","packages: ${packages.size} $packages ")

        //add new activity to db for each list item
        for (packageInfo in packages) {
            packageName = packageInfo.packageName
            val timestamp = System.currentTimeMillis()
            try {
                val appInfo = pm.getApplicationInfo(packageInfo.packageName, 0)
                applicationName = (pm.getApplicationLabel(appInfo)) as String
                Log.d("xxx","package: $applicationName")
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            saveEntryApps(applicationName, packageName, timestamp)
        }
        return null
    }

    private fun saveEntryApps(applicationName: String?, packageName: String, timestamp: Long) {
        //generate UsageActivity and insert to DB
        Log.d("xxx","saveEntry: $applicationName")
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

        //create MetaDeviceInfo
//        val meta = MetaDeviceInfo()
//        meta
//            .studyBegin(startDate)
//            .studyEnd(endDate)
//            .manufacturer(manufacturer)
//            .model(model)
//            .sdkVersion(sdkVersion)
//            .releaseVersion(releaseVersion)

        LogEvent(LogEventName.DEVICE_INFO, timestamp, event= model, description = releaseVersion, name = sdkVersion.toString(), packageName = manufacturer ).saveToDataBase()
    }

}
