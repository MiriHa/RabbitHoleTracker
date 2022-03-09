package com.example.trackingapp.activity.esm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.util.DatabaseManager
import com.example.trackingapp.util.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.databinding.LayoutEsmUnlockIntentionBinding
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.NotificationHelper.dismissESMNotification
import com.example.trackingapp.util.SharedPrefManager

class ESMIntentionUnlockActivity : AppCompatActivity() {
    private lateinit var viewModel: ESMIntentionViewModel
    private lateinit var binding: LayoutEsmUnlockIntentionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ESMIntentionViewModelFactory())[ESMIntentionViewModel::class.java]

        binding = LayoutEsmUnlockIntentionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SharedPrefManager.init(this.applicationContext)
        if(DatabaseManager.intentionList.isEmpty()) DatabaseManager.initIntentionList()

        viewModel.currentSessionID = intent.getStringExtra(CONST.ESM_SESSION_ID_MESSAGE)

        val adapter = ArrayAdapter(
            this@ESMIntentionUnlockActivity,
            R.layout.support_simple_spinner_dropdown_item,
            ArrayList(DatabaseManager.intentionList)
        )

        binding.esmUnlockAutoCompleteTextView.apply {
            setAdapter(adapter)
            threshold = 1
            //Show DropdownList when EditText gets selected
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    this.showDropDown()
            }
            setOnEditorActionListener { _, actionID, _ ->
                if (actionID == EditorInfo.IME_ACTION_DONE) {
                    actionManualInputDone(binding.esmUnlockAutoCompleteTextView.text.toString())
                    return@setOnEditorActionListener true
                }
                false
            }
            //When user selected item, close ESM
            setOnItemClickListener { adapterView, _, position, _ ->
                val item: String = adapterView.getItemAtPosition(position).toString()
                actionDone(item, System.currentTimeMillis())
            }
        }

        binding.buttonEsmUnlock1.apply {
            visibility = if (DatabaseManager.intentionExampleList.contains(viewModel.savedIntention)) View.GONE else View.VISIBLE
            configureRadioButton()
            text = viewModel.savedIntention
        }
        binding.buttonEsmUnlockNoIntention.configureRadioButton()
        binding.buttonEsmUnlock3.configureRadioButton()
        binding.buttonEsmUnlock4.configureRadioButton()

        binding.esmUnlockButtonstart.setOnClickListener {
            actionManualInputDone(binding.esmUnlockAutoCompleteTextView.text.toString())
        }
    }


    private fun RadioButton.configureRadioButton() {
        this.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                actionDone(this.text.toString(), System.currentTimeMillis())
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.currentSessionID = intent?.getStringExtra(CONST.ESM_SESSION_ID_MESSAGE)
    }

    override fun onBackPressed() {
        // Do Nothing
    }

    override fun onStop() {
        super.onStop()
        this.finish()
        viewModel.resetSessionID()
        dismissESMNotification(this)
    }

    private fun actionManualInputDone(intention: String) {
        if (intention.isNotBlank()) {
            actionDone(intention, System.currentTimeMillis())
        } else {
            Toast.makeText(this, R.string.esm_unlock_intention_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun actionDone(intention: String, time: Long) {
        if (intention == getString(R.string.esm_intention_example_noIntention)) {
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_IS_NO_CONCRETE_INTENTION, true)
        } else {
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_IS_NO_CONCRETE_INTENTION, false)
        }

        viewModel.checkDuplicateIntentionAnSave(intention)
        dismissFullScreenNotification()
        LogEvent(
            LogEventName.ESM,
            timestamp = time,
            event = ESMQuestionType.ESM_UNLOCK_INTENTION.name,
            description = intention,
            id = viewModel.currentSessionID
        ).saveToDataBase()
    }

    private fun dismissFullScreenNotification() {
        this.finish()
        //moveTaskToBack(true)
        dismissESMNotification(this)
    }
}
