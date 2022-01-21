package com.example.trackingapp.activity.ESM

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.databinding.ActivityLockscreenEsmBinding
import com.example.trackingapp.util.NotificationHelper.dismissESMNotification
import com.example.trackingapp.util.SharedPrefManager
import com.example.trackingapp.util.turnScreenOffAndKeyguardOn
import com.example.trackingapp.util.turnScreenOnAndKeyguardOff

class ESMIntentionLockActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLockscreenEsmBinding
    private lateinit var viewModel: ESMIntentionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockscreenEsmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ESMIntentionViewModelFactory())[ESMIntentionViewModel::class.java]

        val savedIntention = SharedPrefManager.getLastSavedIntention()
        binding.textViewEsmLockIntention.text = savedIntention

        this.turnScreenOnAndKeyguardOff()

        binding.buttonEsmLockNo.setOnClickListener {
            viewModel.makeLogQuestion1(false)
            it.isEnabled = false
            binding.buttonEsmLockYes.isEnabled = false
            dismissFullScreenNotification()
        }

        binding.buttonEsmLockYes.setOnClickListener {
            viewModel.makeLogQuestion1(true)
            it.isEnabled = false
            binding.buttonEsmLockNo.isEnabled = false
            dismissFullScreenNotification()
        }

        binding.buttonEsmLockQuestion2No.setOnClickListener {
            viewModel.makeLogQuestion2(false)
            it.isEnabled = false
            binding.buttonEsmLockQuestion2Yes.isEnabled = false
            dismissFullScreenNotification()
        }

        binding.buttonEsmLockQuestion2Yes.setOnClickListener {
            viewModel.makeLogQuestion2(true)
            it.isEnabled = false
            binding.buttonEsmLockQuestion2No.isEnabled = false
            dismissFullScreenNotification()
        }
    }

    private fun dismissFullScreenNotification(){
        if(viewModel.esmLockQuestion1answered && viewModel.esmLockQuestion2Answered) {
            this.finish()
            dismissESMNotification(this)
        }
    }

    override fun onBackPressed() {
        //Do nothing
    }

    override fun onDestroy() {
        super.onDestroy()
        this.turnScreenOffAndKeyguardOn()
    }
}