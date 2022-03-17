package com.lmu.trackingapp.activity.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lmu.trackingapp.util.CONST
import com.lmu.trackingapp.util.PermissionManager

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
        return permissions.firstOrNull { permission ->
            val permissionGranted = isPermissionGranted(permission)
            val permissionAsked = askedPermission.contains(permission)
            !permissionGranted && !permissionAsked
        }
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
                    PermissionManager.isAccessibilityServiceEnabled(it)
                }
                PermissionView.USAGE_STATS -> {
                    PermissionManager.isUsageInformationPermissionEnabled(it)
                }
                else -> false
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

