package com.example.trackingapp.activity.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.PermissionManager
import com.example.trackingapp.util.SharedPrefManager

class PermissionHolderViewModel : ViewModel() {

    var permissions: List<PermissionView> = listOf()

    var currentPermission: PermissionView? = null

    private var permissionIterator: PermissionIterator? = null

    fun initPermissionIterator(activity: Activity?) {
        activity?.let { permissionIterator = PermissionIterator(activity, permissions) }
    }

    fun nextPermissionToAsk(): PermissionView? {
        currentPermission = permissionIterator?.next()
        return currentPermission
    }

    fun userFinishedOnboarding() {
        SharedPrefManager.saveBoolean(CONST.PREFERENCES_ONBOARDING_FINISHED, true)
    }

    fun reset() {
        permissionIterator?.reset()
    }


}

private class PermissionIterator(val activity: Activity, val permissions: List<PermissionView>) {

    private val askedPermission = mutableListOf<PermissionView>()

    fun next(): PermissionView? {
        val nextPermissionToAsk = nextPermission()

        if (nextPermissionToAsk != null) {
            askedPermission.add(nextPermissionToAsk)
        }

        return nextPermissionToAsk
    }

    fun reset() {
        askedPermission.clear()
    }

    private fun nextPermission(): PermissionView? {
        val p = permissions.firstOrNull { permission ->
            val permissionGranted =  isPermissionGranted(permission)
            val permissionAsked = askedPermission.contains(permission)
             !permissionGranted && !permissionAsked
        }
        Log.d("PERMISSIONITERATOR", "nextPermission: ${p?.name} ")
        return p
    }

    private fun isPermissionGranted(permissionView: PermissionView): Boolean {
        this.activity.let {
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
                    PermissionManager.isAccessibilityServiceEnabled(it) == 1
                }
                PermissionView.USAGE_STATS -> {
                    PermissionManager.isUsageInformationPermissionEnabled(it)
                }
            }
        }
    }
}

class PermissionHolderViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PermissionHolderViewModel::class.java)) {
            return PermissionHolderViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

