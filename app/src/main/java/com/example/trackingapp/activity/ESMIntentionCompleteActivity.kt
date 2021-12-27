package com.example.trackingapp.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.trackingapp.databinding.ActivityLockscreenEsmBinding
import com.example.trackingapp.util.NotificationHelper.dismissNotification
import com.example.trackingapp.util.turnScreenOffAndKeyguardOn
import com.example.trackingapp.util.turnScreenOnAndKeyguardOff

class ESMIntentionCompleteActivity : AppCompatActivity(){

    private lateinit var binding: ActivityLockscreenEsmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockscreenEsmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.turnScreenOnAndKeyguardOff()


        binding.buttonEsmLockNo.setOnClickListener {
           dismissFullScreenNotification()
        }

        binding.buttonEsmLockYes.setOnClickListener {
            dismissFullScreenNotification()
        }


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