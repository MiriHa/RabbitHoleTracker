package com.example.trackingapp.activity.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.trackingapp.databinding.FragmentPermissionBinding
import com.example.trackingapp.util.*

class PermissionFragment : Fragment() {

    private val TAG = "PERMISSIONFRAGMENT"

    private lateinit var binding: FragmentPermissionBinding
    lateinit var viewModel: PermissionViewModel
    private lateinit var mContext: Context

    lateinit var permissionView: PermissionView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)

        permissionView = viewModel.permission

        binding.imageviewPermissionIcon.setImageDrawable(ResourcesCompat.getDrawable(resources, permissionView.backgroundResourceId, null))
        binding.textViewPermissionFragmentTitle.text = mContext.getString(permissionView.titleLabelResourceId)
        binding.textViewPermissionFragmentDescription.text = mContext.getString(permissionView.descriptionLabelResourceId)
        binding.buttonPermissionFragent.text = mContext.getString(permissionView.primaryButtonLabelResourceId)

        if(permissionView == PermissionView.QUESTIONNAIRE){
            NotificationHelper.createSurveyNotification(mContext, SurveryType.SURVEY_START)
            binding.layoutLinkPermissions.visibility = View.VISIBLE
            binding.textviewLinkQuestionnaire.text = NotificationHelper.createSurveyLink(SurveryType.SURVEY_START).toString()
        }

        binding.buttonPermissionFragent.setOnClickListener {
            continueClicked()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsOrProceed()
    }

    private fun checkPermissionsOrProceed(){
        Log.d(TAG, "checkPermissionOrProceed")
        this.activity?.let {
            val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)
            when (viewModel.permission) {
                PermissionView.PERMISSIONS -> {
                    if(managePermissions.arePermissionsGranted() == PackageManager.PERMISSION_GRANTED){
                        viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                    }
                }
                PermissionView.NOTIFICATION_LISTENER -> {
                    if (Settings.Secure.getString(it.contentResolver, "enabled_notification_listeners")
                            .contains(it.applicationContext.packageName)){
                        viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                    }
                }
                PermissionView.ACCESSIBILITY_SERVICE -> {
                    if(PermissionManager.isAccessibilityServiceEnabled(it)){
                        viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                    }
                }
                PermissionView.USAGE_STATS -> {
                    if(PermissionManager.isUsageInformationPermissionEnabled(it)) {
                        viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                    }
                }
                else -> { /*Do nothing */ }
            }
        }
    }

    private fun continueClicked() {
        Log.d(TAG, "continueClicked")
        this.activity?.let {
            val managePermissions = PermissionManager(it, CONST.PERMISSION_REQUEST_CODE)

            when (viewModel.permission) {
                PermissionView.PERMISSIONS -> {
                    if(managePermissions.checkPermissions()){
                        checkPermissionsOrProceed()
                    }
                }
                PermissionView.NOTIFICATION_LISTENER -> {
                    if(managePermissions.checkForNotificationListenerPermissionEnabled()) {
                        checkPermissionsOrProceed()
                    }
                }
                PermissionView.ACCESSIBILITY_SERVICE -> {
                   if(managePermissions.checkAccessibilityPermission()){
                       checkPermissionsOrProceed()
                   }
                }
                PermissionView.USAGE_STATS -> {
                    if(managePermissions.checkForUsageStatsPermissions()){
                        checkPermissionsOrProceed()
                    }
                }
                PermissionView.ONBOARDING -> {
                    SharedPrefManager.saveBoolean(CONST.PREFERENCES_USER_FINISHED_ONBAORDING, true)
                    viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                }
                PermissionView.QUESTIONNAIRE -> {
                    viewModel.userResponded(PermissionViewModel.UserResponse.ACCEPTED)
                }
            }
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        fun newInstance(viewModel: PermissionViewModel): PermissionFragment {
            return PermissionFragment().apply { this.viewModel = viewModel }
        }
    }
}