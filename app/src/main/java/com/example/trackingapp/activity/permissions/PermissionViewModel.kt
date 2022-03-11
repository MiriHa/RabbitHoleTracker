package com.example.trackingapp.activity.permissions

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.trackingapp.R

class PermissionViewModel(
    val permission: PermissionView,
    private val userResponseHandler: (UserResponse) -> Unit
) : ViewModel() {

    fun userResponded(userResponse: UserResponse) {
        Log.d("PERMISSIONVIEWMODEL","userResponded: ${userResponse.name}")
        if(userResponse == UserResponse.ACCEPTED) {
            userResponseHandler(userResponse)
        }
    }


    enum class UserResponse {
        ACCEPTED,
        DENIED,
        SKIPPED;

        companion object {
            fun fromModelValue(response: Int): UserResponse {
                return when (response) {
                    0 -> ACCEPTED
                    -1 -> DENIED
                    else -> SKIPPED
                }
            }
        }
    }

}

enum class PermissionView(
    val titleLabelResourceId: Int,
    val descriptionLabelResourceId: Int,
    val backgroundResourceId: Int,
    val primaryButtonLabelResourceId: Int,
) {
    PERMISSIONS(
        R.string.permission_title,
        R.string.permission_explanation_description,
        R.drawable.ic_settings,
        R.string.permission_button_text,
    ),
    NOTIFICATION_LISTENER(
        R.string.permission_request_notification_listener_title,
        R.string.permission_request_notification_listener_description,
        R.drawable.ic_notifications,
        R.string.permission_settings_button_text,
    ),
    ACCESSIBILITY_SERVICE(
        R.string.permission_request_accessibility_service_title,
        R.string.permission_request_accessibility_service_description,
        R.drawable.ic_accessibility,
        R.string.permission_settings_button_text,
    ),
    USAGE_STATS(
        R.string.permission_request_usage_stats_title,
        R.string.permission_request_usage_stats_description,
        R.drawable.ic_query_stats,
        R.string.permission_settings_button_text,
    ),
  /*  OVERLAY(
        R.string.permission_request_usage_stats_title,
        R.string.permission_request_usage_stats_description,
        R.drawable.ic_query_stats,
        R.string.permission_settings_button_text,
    )*/
}
