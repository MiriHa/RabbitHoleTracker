package com.example.trackingapp.activity

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.R
import com.example.trackingapp.databinding.LayoutEsmIntentionOverlayBinding
import com.example.trackingapp.util.NotificationHelper.dismissNotification


class ESMIntentionActivity: AppCompatActivity(){
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
            this, R.layout.support_simple_spinner_dropdown_item, suggestions)

        binding.autoCompleteTextView.setAdapter(adapter)

        binding.buttonEsmIntetionStart.setOnClickListener {
            dismissFullScreenNotification()
        }
    }

    private fun dismissFullScreenNotification(){
        Log.d("xxx","DismissFullScreenNotification!")
        this.finish()
        moveTaskToBack(true)
        this.dismissNotification()
    }

}



/*
    Fragment() {

    private lateinit var viewModel: ESMIntentionViewModel
    private lateinit var binding: LayoutEsmIntentionOverlayBinding
    private lateinit var mContext: Context
    private var suggestions: Array<String> = arrayOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, ESMIntentionViewModelFactory())[ESMIntentionViewModel::class.java]

        binding = LayoutEsmIntentionOverlayBinding.inflate(inflater)

        suggestions = arrayOf(
            "Belgium", "France", "Italy", "Germany", "Spain"
        )


        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            mContext, R.layout.simple_dropdown_item_1line, suggestions)

        binding.autoCompleteTextView.setAdapter(adapter)

        binding.autoCompleteTextView


        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}*/
