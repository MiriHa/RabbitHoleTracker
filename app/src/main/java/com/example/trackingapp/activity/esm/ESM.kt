package com.example.trackingapp.activity.esm

import com.example.trackingapp.R

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
    override var value: String = "",
    val sliderStepSize: Float,
    val sliderMax: Float,
    val sliderMin: Float
) : ESMItem

class ESMButtonItem(
    override val question: Int,
    override val questionType: ESMQuestionType,
    override val visible: Boolean = true,
    override var value: String = "",
    val buttonOneText: Int = R.string.esm_button_yes,
    val buttonTwoText: Int = R.string.esm_button_no,
    val buttonThreeText: Int? = null
) : ESMItem



enum class ESMQuestionType{
    ESM_UNLOCK_INTENTION,
    ESM_LOCK_Q_FINISH,
    ESM_LOCK_Q_MORE,
    ESM_LOCK_Q_INTENTIONAL,
    ESM_LOCK_Q_EMOTION,
    ESM_LOCK_Q_REGRET,
    ESM_LOCK_Q_AGENCY,
}