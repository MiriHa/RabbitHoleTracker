package com.lmu.trackingapp.activity.esm

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.lmu.trackingapp.R

sealed interface ESMItem {
    val question: Int
    val questionType: ESMQuestionType
    var value: String
    val visible: Boolean
}

class ESMSliderItem(
    override val question: Int,
    override val questionType: ESMQuestionType,
    override val visible: Boolean  = true,
    override var value: String = "unanswered",
    val sliderStepSize: Float,
    val sliderMax: Float,
    val sliderMin: Float,
    @StringRes val sliderMinLabel: Int,
    @StringRes val sliderMaxLabel: Int
) : ESMItem

class ESMDropDownItem(
    override val question: Int,
    override val questionType: ESMQuestionType,
    override val visible: Boolean  = true,
    override var value: String = "unanswered",
    @ArrayRes val dropdownList: Int
) : ESMItem

class ESMRadioGroupItem(
    override val question: Int,
    override val questionType: ESMQuestionType,
    override val visible: Boolean = true,
    override var value: String = "unanswered",
    val buttonList: List<Int> = arrayListOf(R.string.esm_button_yes, R.string.esm_button_no)
) : ESMItem


enum class ESMQuestionType{
    ESM_UNLOCK_INTENTION,
    ESM_LOCK_Q_FINISH,
    ESM_LOCK_Q_MORE,
    ESM_LOCK_Q_TRACK_OF_TIME,
    ESM_LOCK_Q_TRACK_OF_SPACE,
    ESM_LOCK_Q_EMOTION,
    ESM_LOCK_Q_REGRET,
    ESM_LOCK_Q_AGENCY,
}