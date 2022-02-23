package com.example.trackingapp.activity.esm

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.databinding.LayoutEsmUnlockIntentionBinding
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.NotificationHelper.dismissESMNotification
import com.example.trackingapp.util.SharedPrefManager
import java.util.*

class ESMIntentionUnlockActivity : AppCompatActivity() {
    private lateinit var viewModel: ESMIntentionViewModel
    private lateinit var binding: LayoutEsmUnlockIntentionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ESMIntentionViewModelFactory())[ESMIntentionViewModel::class.java]

        binding = LayoutEsmUnlockIntentionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SharedPrefManager.init(this.applicationContext)

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
                actionDone(item)
            }
        }

        binding.buttonEsmUnlock1.apply {
            visibility = if(DatabaseManager.intentionExampleList.contains(viewModel.savedIntention)) View.GONE else View.VISIBLE
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
                actionDone(this.text.toString())
            }
        }
    }

    override fun onBackPressed() {
        // Do Nothing
    }

    private fun actionManualInputDone(intention: String) {
        if (intention.isNotBlank()) {
            actionDone(intention)
        } else {
            Toast.makeText(this, R.string.esm_unlock_intention_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun actionDone(intention: String) {
        if(intention == getString(R.string.esm_intention_example_noIntention)){
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_IS_NO_CONCRETE_INTENTION, true)
        } else {
            SharedPrefManager.saveBoolean(CONST.PREFERENCES_IS_NO_CONCRETE_INTENTION, false)
        }

        viewModel.checkDuplicateIntentionAnSave(intention)
        dismissFullScreenNotification()
        LogEvent(
            LogEventName.ESM,
            System.currentTimeMillis(),
            ESMQuestionType.ESM_UNLOCK_INTENTION.name,
            intention
        ).saveToDataBase()
    }

    private fun dismissFullScreenNotification() {
        this.finish()
        moveTaskToBack(true)
        dismissESMNotification(this)
    }

}
