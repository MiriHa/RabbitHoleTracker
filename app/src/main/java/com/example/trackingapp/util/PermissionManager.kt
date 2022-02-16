package com.example.trackingapp.util

import android.Manifest
import android.app.Activity
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackingapp.activity.permissions.PermissionView


class PermissionManager(val activity: Activity, private val code: Int) {
    val TAG = "TRACKINGAPP_PERMISSION_MANAGER"

    private val appPermissions: List<String>
        get() {
            val l = arrayListOf<String>()
            l.add(Manifest.permission.INTERNET)
            l.add(Manifest.permission.ACCESS_WIFI_STATE)
            l.add(Manifest.permission.RECEIVE_BOOT_COMPLETED)
            l.add(Manifest.permission.READ_PHONE_STATE)
            l.add(Manifest.permission.READ_CALL_LOG)
            l.add(Manifest.permission.READ_PHONE_NUMBERS)
            l.add(Manifest.permission.READ_SMS)
            l.add(Manifest.permission.RECEIVE_SMS)
            // ?? l.add(Manifest.permission.WRITE_SMS)
            // l.add(Manifest.permission.WAKE_LOCK)
            l.add(Manifest.permission.READ_SMS)
            l.add(Manifest.permission.ACCESS_NETWORK_STATE)
            l.add(Manifest.permission.BLUETOOTH)
            l.add("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                l.add(Manifest.permission.FOREGROUND_SERVICE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                l.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                l.add(Manifest.permission.ACTIVITY_RECOGNITION)
                l.add(Manifest.permission.USE_FULL_SCREEN_INTENT)
            }
            return l
        }

    // Check permissions at runtime
    fun checkPermissions(): Boolean {
        Log.d(TAG, "checkPermissions")
        return if (arePermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            //showAlert()
            requestPermissions()
            false
        } else {
            Toast.makeText(activity, "Permissions already granted.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    fun checkForUsageStatsPermissions(): Boolean {
        return if (!isUsageInformationPermissionEnabled(activity)) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            false
        } else {
            Toast.makeText(activity, "Permissions already granted.", Toast.LENGTH_SHORT).show()
            true
        }
    }

    fun checkForNotificationListenerPermissionEnabled(): Boolean {
        return if (isNotificationListenerEnabled(activity)
        ) {
            Toast.makeText(activity, "Permissions already granted.", Toast.LENGTH_SHORT).show()
            true
        } else {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            activity.startActivity(intent)
            false
        }
    }

    // method to check is the user has permitted the accessibility permission
    // if not then prompt user to the system's Settings activity
    fun checkAccessibilityPermission(): Boolean {
        return if (!isAccessibilityServiceEnabled(activity)) {
            Log.d(TAG, "checkAccesibiltyPermission open settings")
            // if not construct intent to request permission
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // request permission via start activity for result
            activity.startActivity(intent)
            false
        } else {
            Log.d(TAG, "checkAccesibiltyPermission given")
            true
        }
    }

    fun isAccessibilityServiceEnabled2(context: Context, accessibilityService: Class<*>?): Boolean {
        accessibilityService?.let {
            val expectedComponentName = ComponentName(context, it)
            val enabledServicesSetting: String =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)
            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                val enabledService = ComponentName.unflattenFromString(componentNameString)
                if (enabledService != null && enabledService == expectedComponentName) return true
            }
        }
        return false
    }

    // Check permissions status
    fun arePermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0;
        for (permission in appPermissions) {
            val granted = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "IsPermissionGranted: $permission $granted")
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in appPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }


    // Show alert dialog to request permissions
    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Need permission(s)")
        builder.setMessage("Some permissions are required to do the task.")
        builder.setPositiveButton("OK") { _, _ -> requestPermissions() }
        builder.setNeutralButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }


    // Request the permissions at run time
    private fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an explanation asynchronously
            Toast.makeText(activity, "Should show an explanation.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(activity, appPermissions.toTypedArray(), code)
        }
    }

    companion object {

        fun isPermissionGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        fun isNotificationListenerEnabled(context: Context): Boolean {
            return Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
                    .contains(context.applicationContext.packageName)
        }

        fun isAccessibilityServiceEnabled(context: Context): Boolean {
            var accessEnabled = 0
            Log.d(TAG, "checkAccesibiltyPermission")
            try {
                accessEnabled = Settings.Secure.getInt(context.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
                Log.d(TAG, "checkAccesibiltyPermission enabed: $accessEnabled")
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            return accessEnabled == 1
        }

        fun isUsageInformationPermissionEnabled(context: Context): Boolean {
            return try {
                val packageManager: PackageManager = context.packageManager
                val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
                val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                var mode = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mode = appOpsManager.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid,
                        applicationInfo.packageName
                    )
                } else {
                    @Suppress("DEPRECATION")
                    mode = appOpsManager.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid,
                        applicationInfo.packageName
                    )
                }
                if (mode == AppOpsManager.MODE_DEFAULT) {
                    //ContextCompat.checkSelfPermission(context, Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
                    context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
                } else {
                    mode == AppOpsManager.MODE_ALLOWED
                }
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        fun checkPermission(permissionView: PermissionView, activity: Activity?): Boolean {
            activity?.let {
                val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)
                return when (permissionView) {
                    PermissionView.PERMISSIONS -> {
                        managePermissions.arePermissionsGranted() == PackageManager.PERMISSION_GRANTED
                    }
                    PermissionView.NOTIFICATION_LISTENER -> {
                        Settings.Secure.getString(it.contentResolver, "enabled_notification_listeners")
                            .contains(it.applicationContext.packageName)
                    }
                    PermissionView.ACCESSIBILITY_SERVICE -> {
                        isAccessibilityServiceEnabled(it)

                    }
                    PermissionView.USAGE_STATS -> {
                        isUsageInformationPermissionEnabled(it)
                    }
                }
            }
            return false
        }

        fun areAllPermissionGiven(activity: Activity?): Boolean {
            activity?.let {
                val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)

                val permissions = managePermissions.arePermissionsGranted() == PackageManager.PERMISSION_GRANTED

                val notificationListener =  Settings.Secure.getString(it.contentResolver, "enabled_notification_listeners")
                    .contains(it.applicationContext.packageName)

                val accessibilityService = isAccessibilityServiceEnabled(it)

                val usageStats = isUsageInformationPermissionEnabled(it)

                Log.d("PERMISSIONMANAGER","AreAllPermissionsGive: ${permissions && notificationListener && accessibilityService && usageStats}")
                return permissions && notificationListener && accessibilityService && usageStats
            }
            return false
        }
    }
}
