package com.example.trackingapp.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.trackingapp.AuthManager
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

        this.turnScreenOnAndKeyguardOff()


        binding.buttonEsmLockNo.setOnClickListener {
           dismissFullScreenNotification()
            makeLog(false)
        }

        binding.buttonEsmLockYes.setOnClickListener {
            dismissFullScreenNotification()
            makeLog(true)
        }
    }

    private fun makeLog(isIntentionFinished: Boolean){
        val answer = if(isIntentionFinished) ESM_Intention_Lock_Answer.ESM_INTENTION_FINISHED else ESM_Intention_Lock_Answer.ESM_INTENTION_UNFINISHED
        AuthManager.makeLog(Date(), LogActivity.ESM_LOCK, answer.toString())
    }

    private fun dismissFullScreenNotification(){
        Log.d("xxx","DismissFullScreenNotification!")
        this.finish()
        this.dismissNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.turnScreenOffAndKeyguardOn()
    }
}