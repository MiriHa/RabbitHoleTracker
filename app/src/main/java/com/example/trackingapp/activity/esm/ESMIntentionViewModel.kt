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

    var questionList = createQuestionList()
    val answeredQuestions = mutableListOf<ESMQuestionType>()

    val savedIntention
        get() = SharedPrefManager.getLastSavedIntention()

    fun makeLogESMLockQuestion(answer: String, questionType: ESMQuestionType, time: Long) {
        answeredQuestions.add(questionType)
        SharedPrefManager.saveBoolean(CONST.PREFERENCES_ESM_LOCK_ANSWERED, true)
        LogEvent(
            LogEventName.ESM,
            timestamp = time,
            event = questionType.name,
            name = answer,
            description = savedIntention,
        ).saveToDataBase()
    }

    fun checkDuplicateIntentionAnSave(newIntention: String) {
        SharedPrefManager.saveLastIntention(newIntention)
        if (!DatabaseManager.intentionList.contains(newIntention) && !DatabaseManager.intentionExampleList.contains(newIntention)) {
            //save new Intention to Firebase
            DatabaseManager.saveNewIntention(Date(), newIntention)
        }
    }


    fun createQuestionList(): List<ESMItem> {
        val lastFullESM = SharedPrefManager.getLong(CONST.PREFERENCES_LAST_ESM_FULL_TIMESTAMP, 0L)
        val sessionHadNoIntention = SharedPrefManager.getBoolean(CONST.PREFERENCES_IS_NO_CONCRETE_INTENTION)
        return when {
            //Was Last full ESM over xxx min ago?
            System.currentTimeMillis() - lastFullESM > CONST.ESM_FREQUENCY -> {
                SharedPrefManager.saveLong(CONST.PREFERENCES_LAST_ESM_FULL_TIMESTAMP, System.currentTimeMillis())
                listOfNotNull(
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_regret,
                        ESMQuestionType.ESM_LOCK_Q_REGRET,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = R.string.esm_lock_label_stronglyAgree,
                        sliderMinLabel = R.string.esm_lock_label_stronglyDisagree
                    ),
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
                        sliderMaxLabel = R.string.esm_lock_label_stronglyAgree,
                        sliderMinLabel = R.string.esm_lock_label_stronglyDisagree
                    ),
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_track_of_time,
                        ESMQuestionType.ESM_LOCK_Q_TRACK_OF_TIME,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = R.string.esm_lock_label_stronglyAgree,
                        sliderMinLabel = R.string.esm_lock_label_stronglyDisagree
                    ),
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_track_of_space,
                        ESMQuestionType.ESM_LOCK_Q_TRACK_OF_SPACE,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = R.string.esm_lock_label_stronglyAgree,
                        sliderMinLabel = R.string.esm_lock_label_stronglyDisagree
                    ),
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_finished,
                        ESMQuestionType.ESM_LOCK_Q_FINISH,
                    ).takeIf { !sessionHadNoIntention },
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_more,
                        ESMQuestionType.ESM_LOCK_Q_MORE,
                    ).takeIf { !sessionHadNoIntention },
                )
            }
            else -> {
                listOfNotNull(
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_finished,
                        ESMQuestionType.ESM_LOCK_Q_FINISH,
                    ).takeIf { !sessionHadNoIntention },
                    ESMRadioGroupItem(
                        R.string.esm_lock_intention_question_intention_more,
                        ESMQuestionType.ESM_LOCK_Q_MORE,
                    ).takeIf { !sessionHadNoIntention},
                    ESMSliderItem(
                        R.string.esm_lock_intention_question_regret,
                        ESMQuestionType.ESM_LOCK_Q_REGRET,
                        sliderStepSize = 1F,
                        sliderMin = 0F,
                        sliderMax = 7F,
                        sliderMaxLabel = R.string.esm_lock_label_stronglyAgree,
                        sliderMinLabel = R.string.esm_lock_label_stronglyDisagree
                    ),
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