package com.example.trackingapp.activity.esm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.util.CONST
import com.example.trackingapp.util.SharedPrefManager
import java.util.*




class ESMIntentionViewModel : ViewModel() {

    val questionList = createQuestionList()
    val answeredQuestions = mutableListOf<ESMQuestionType>()

    val savedIntention
        get() = SharedPrefManager.getLastSavedIntention()

    fun makeLogQuestion(answer: String, questionType: ESMQuestionType, time: Long) {
        answeredQuestions.add(questionType)
        LogEvent(
            LogEventName.ESM,
            timestamp = time,
            event = questionType.name,
            name = answer,
            description = savedIntention
        ).saveToDataBase()
    }

    fun checkDuplicateIntentionAnSave(newIntention: String) {
        SharedPrefManager.saveLastIntention(newIntention)
        if (!DatabaseManager.intentionList.contains(newIntention)) {
            //save new Intention to Firebase
            DatabaseManager.saveNewIntention(Date(), newIntention)
        }
    }



    private fun createQuestionList(): List<ESMItem> {
        val lastFullESM = SharedPrefManager.getLong(CONST.PREFERENCES_LAST_ESM_FULL_TIMESTAMP, 0L)
        return when {
            //Was Last full ESM over half an over ago?
            System.currentTimeMillis() - lastFullESM > CONST.ESM_FREQUENCY-> {
                SharedPrefManager.saveLong(CONST.PREFERENCES_LAST_ESM_FULL_TIMESTAMP, System.currentTimeMillis())
                arrayListOf(
                    ESMDropDownItem(
                        R.string.esm_lock_intention_question_emotion,
                        ESMQuestionType.ESM_LOCK_Q_EMOTION,
                        dropdownList = R.array.esm_emotionList
                    ),
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_agency,
                        ESMQuestionType.ESM_LOCK_Q_AGENCY,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = "very strong",
                        sliderMinLabel = "non existent"
                    ),
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_track_of_time,
                        ESMQuestionType.ESM_LOCK_Q_TRACK_OF_TIME,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = "very much",
                        sliderMinLabel = "not at all"
                    ),
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_track_of_space,
                        ESMQuestionType.ESM_LOCK_Q_TRACK_OF_SPACE,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = "very much",
                        sliderMinLabel = "not at all"
                    ),
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_regret,
                        ESMQuestionType.ESM_LOCK_Q_REGRET,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = "very strong",
                        sliderMinLabel = "non existent"
                    ),
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_finished,
                        ESMQuestionType.ESM_LOCK_Q_FINISH,
                    ),
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_more,
                        ESMQuestionType.ESM_LOCK_Q_MORE,
                    )
                )
            }
            else -> {
                arrayListOf(
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_regret,
                        ESMQuestionType.ESM_LOCK_Q_REGRET,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = "very strong",
                        sliderMinLabel = "non existent"
                    ),
                    ESMDropDownItem(
                        R.string.esm_lock_intention_question_emotion,
                        ESMQuestionType.ESM_LOCK_Q_EMOTION,
                        dropdownList = R.array.esm_emotionList
                    ),
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_finished,
                        ESMQuestionType.ESM_LOCK_Q_FINISH,
                    ),
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_more,
                        ESMQuestionType.ESM_LOCK_Q_MORE,
                    )
                )
            }
        }
    }
}

class ESMIntentionViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ESMIntentionViewModel::class.java)) {
            return ESMIntentionViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}