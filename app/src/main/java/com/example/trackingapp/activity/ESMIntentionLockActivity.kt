package com.example.trackingapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.databinding.ActivityLockscreenEsmBinding
import com.example.trackingapp.models.ESM_Intention_Lock_Answer
import com.example.trackingapp.models.LogActivity
import com.example.trackingapp.util.NotificationHelper.dismissNotification
import com.example.trackingapp.util.turnScreenOffAndKeyguardOn
import com.example.trackingapp.util.turnScreenOnAndKeyguardOff
import java.util.*

class ESMIntentionLockActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLockscreenEsmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockscreenEsmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val savedIntention = DatabaseManager.getLastSavedIntention(this@ESMIntentionLockActivity)
        binding.textViewEsmLockIntention.text = savedIntention

        this.turnScreenOnAndKeyguardOff()

        binding.buttonEsmLockNo.setOnClickListener {
           dismissFullScreenNotification()
            makeLogQuestion1(false)
        }

        binding.buttonEsmLockYes.setOnClickListener {
            dismissFullScreenNotification()
            makeLogQuestion1(true)
        }

        binding.buttonEsmLockQuestion2No.setOnClickListener {
            dismissFullScreenNotification()
            makeLogQuestion2(false)
        }

        binding.buttonEsmLockQuestion2Yes.setOnClickListener {
            dismissFullScreenNotification()
            makeLogQuestion2(true)
        }
    }

    private fun makeLogQuestion1(isIntentionFinished: Boolean){
        val answer = if(isIntentionFinished) ESM_Intention_Lock_Answer.ESM_INTENTION_FINISHED else ESM_Intention_Lock_Answer.ESM_INTENTION_UNFINISHED
        DatabaseManager.makeLog(Date(), LogActivity.ESM_LOCK, answer.toString())
    }

    private fun makeLogQuestion2(moreThanInitialIntention: Boolean){
        val answer = if(moreThanInitialIntention) ESM_Intention_Lock_Answer.ESM_MORE_THAN_INITIAL_INTENTION else ESM_Intention_Lock_Answer.ESM_NOT_MORE_THAN_INITIAL_INTENTION
        DatabaseManager.makeLog(Date(), LogActivity.ESM_LOCK, answer.toString())
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