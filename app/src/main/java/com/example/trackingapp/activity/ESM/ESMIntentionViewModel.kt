package com.example.trackingapp.activity.ESM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.DatabaseManager
import com.example.trackingapp.models.ESM_Intention_Lock_Answer
import com.example.trackingapp.models.LogActivity
import java.util.*

class ESMIntentionViewModel: ViewModel() {

    var esmLockQuestion1answered = false
    var esmLockQuestion2Answered = false

    fun makeLogQuestion1(isIntentionFinished: Boolean){
        esmLockQuestion1answered = true
        val answer = if(isIntentionFinished) ESM_Intention_Lock_Answer.ESM_INTENTION_FINISHED else ESM_Intention_Lock_Answer.ESM_INTENTION_UNFINISHED
        DatabaseManager.makeLog(Date(), LogActivity.ESM_LOCK, answer.toString())
    }

    fun makeLogQuestion2(moreThanInitialIntention: Boolean){
        esmLockQuestion2Answered = true
        val answer = if(moreThanInitialIntention) ESM_Intention_Lock_Answer.ESM_MORE_THAN_INITIAL_INTENTION else ESM_Intention_Lock_Answer.ESM_NOT_MORE_THAN_INITIAL_INTENTION
        DatabaseManager.makeLog(Date(), LogActivity.ESM_LOCK, answer.toString())
    }

    fun checkDuplicateIntentionAnSave(newIntention: String){
        //TODO val cleanedIntention = newIntention.lowercase().replace(" ", "")
        if(!DatabaseManager.intentionList.contains(newIntention)){
            //save new Intention to Firebase
            DatabaseManager.saveIntentionToFirebase(Date(), newIntention)
        }
    }

}

class ESMIntentionViewModelFactory() : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ESMIntentionViewModel::class.java)) {
            return ESMIntentionViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}