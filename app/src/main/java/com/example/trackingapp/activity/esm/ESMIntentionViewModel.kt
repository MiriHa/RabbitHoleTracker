package com.example.trackingapp.activity.esm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.R
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
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
            time,
            questionType.name,
            answer,
            savedIntention
        ).saveToDataBase()
    }

    fun checkDuplicateIntentionAnSave(newIntention: String) {
        SharedPrefManager.saveLastIntention(newIntention)
        if (!DatabaseManager.intentionList.contains(newIntention)) {
            //save new Intention to Firebase
            DatabaseManager.saveNewIntention(Date(), newIntention)
        }
    }

    fun createQuestionList(): List<ESMItem>{
        val list = arrayListOf(
            ESMSliderItem(
                R.string.esm_lock_intention_question_regret,
                ESMQuestionType.ESM_LOCK_Q_REGRET,
                sliderStepSize = 1F,
                sliderMin = 0F,
                sliderMax = 7F
            ),
            ESMButtonItem(
                R.string.esm_lock_intention_question_intention_finished,
                ESMQuestionType.ESM_LOCK_Q_FINISH,
            ),
            ESMButtonItem(
                R.string.esm_lock_intention_question_intention_more,
                ESMQuestionType.ESM_LOCK_Q_MORE,
            )
        )

        return list
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