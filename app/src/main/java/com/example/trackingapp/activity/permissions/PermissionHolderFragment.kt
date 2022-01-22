package com.example.trackingapp.activity.permissions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.R
import com.example.trackingapp.databinding.FragmentPermissionHolderBinding
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.SharedPrefManager
import com.example.trackingapp.util.navigate

class PermissionHolderFragmente: Fragment() {

    private val TAG = "PermissionHolderFragment"

    private lateinit var binding: FragmentPermissionHolderBinding
    private lateinit var viewModel: PermissionHolderViewModel
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentPermissionHolderBinding = FragmentPermissionHolderBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, PermissionHolderViewModelFactory())[PermissionHolderViewModel::class.java]
        viewModel.initPermissionIterator(this.activity)

        SharedPrefManager.init(mContext.applicationContext)

        binding.frameLayoutPermissionContainer.setOnClickListener {
            // This ClickListener catches clicks that are forwarded to the background
        }


        if(viewModel.currentPermission == null){
            Log.d(TAG, "showNextPermissionFragment()")
            showNextPermissionFragment()
        }

        return binding.root
    }

    private fun showNextPermissionFragment() {
        val nextPermissionFragment = nextPermissionFragment() as? PermissionFragment
        Log.d(TAG, "showNextPermissionFragment")

        if (nextPermissionFragment != null) {
            swapContent(nextPermissionFragment)
        } else {
            viewModel.userFinishedOnboarding()
            navigate(to = ScreenType.HomeScreen, from = ScreenType.Permission)
        }
    }

    private fun swapContent(nextPermissionFragment: PermissionFragment) {
        Log.d(TAG, "swap Content")
//        nextPermissionFragment.apply {
//            exitTransition = Fade().also {
//                it.duration = AnimationUtil.getScaledAnimationDuration(applicationContext, EXIT_ANIMATION_DURATION)
//            }
//
//            enterTransition = Fade().also {
//                it.duration = AnimationUtil.getScaledAnimationDuration(applicationContext, ENTER_ANIMATION_DURATION)
//                it.startDelay = 0
//            }
//        }

        childFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.frameLayout_permission_container, nextPermissionFragment)
        }
    }

    private fun nextPermissionFragment(): Fragment? {
        val permission = viewModel.nextPermissionToAsk()
        Log.d(TAG, "nextPermissionFragment ${permission?.name}")

        return permission?.let {
            PermissionFragment.newInstance(
                PermissionViewModel(viewModel, it) { showNextPermissionFragment() }
            )
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