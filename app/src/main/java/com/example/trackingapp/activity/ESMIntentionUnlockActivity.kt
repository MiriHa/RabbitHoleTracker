package com.example.trackingapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.AuthManager
import com.example.trackingapp.R
import com.example.trackingapp.databinding.LayoutEsmIntentionOverlayBinding
import com.example.trackingapp.models.LogActivity
import com.example.trackingapp.util.NotificationHelper.dismissNotification
import java.util.*


class ESMIntentionUnlockActivity: AppCompatActivity(){
    private lateinit var viewModel: ESMIntentionViewModel
    private lateinit var binding: LayoutEsmIntentionOverlayBinding
    private var suggestions: Array<String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ESMIntentionViewModelFactory())[ESMIntentionViewModel::class.java]

        binding = LayoutEsmIntentionOverlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        suggestions = arrayOf(
            "Belgium", "France", "Italy", "Germany", "Spain"
        )
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.custom_list_item, R.id.text_view_list_item, suggestions)

        binding.esmUnlockAutoCompleteTextView.apply {
            setAdapter(adapter)
            completionHint = "test"
            threshold = 2
        }


        binding.esmUnlockButtonstart.setOnClickListener {
            dismissFullScreenNotification()
            makeLog()
        }

    }

    private fun makeLog(){
        val answer = binding.esmUnlockAutoCompleteTextView.text.toString()
        AuthManager.makeLog(Date(), LogActivity.ESM_UNLOCK, answer)

        //TODO save to preference
    }

    private fun dismissFullScreenNotification(){
        Log.d("xxx","DismissFullScreenNotification!")
        this.finish()
        moveTaskToBack(true)
        this.dismissNotification()
    }

}
