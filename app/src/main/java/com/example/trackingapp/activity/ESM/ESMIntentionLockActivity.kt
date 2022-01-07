package com.example.trackingapp.activity.ESM

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.databinding.ActivityLockscreenEsmBinding
import com.example.trackingapp.util.NotificationHelper.dismissNotification
import com.example.trackingapp.util.turnScreenOffAndKeyguardOn
import com.example.trackingapp.util.turnScreenOnAndKeyguardOff
import java.util.*

class ESMIntentionLockActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLockscreenEsmBinding
    private lateinit var viewModel: ESMIntentionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockscreenEsmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ESMIntentionViewModelFactory())[ESMIntentionViewModel::class.java]

        val savedIntention = DatabaseManager.getLastSavedIntention(this@ESMIntentionLockActivity)
        binding.textViewEsmLockIntention.text = savedIntention

        this.turnScreenOnAndKeyguardOff()

        binding.buttonEsmLockNo.setOnClickListener {
           dismissFullScreenNotification()
            viewModel.makeLogQuestion1(false)
        }

        binding.buttonEsmLockYes.setOnClickListener {
            dismissFullScreenNotification()
            viewModel.makeLogQuestion1(true)
        }

        binding.buttonEsmLockQuestion2No.setOnClickListener {
            dismissFullScreenNotification()
            viewModel.makeLogQuestion2(false)
        }

        binding.buttonEsmLockQuestion2Yes.setOnClickListener {
            dismissFullScreenNotification()
            viewModel.makeLogQuestion2(true)
        }
    }

    private fun dismissFullScreenNotification(){
        this.finish()
        this.dismissNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.turnScreenOffAndKeyguardOn()
    }
}