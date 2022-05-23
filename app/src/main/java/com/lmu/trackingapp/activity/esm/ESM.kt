package com.lmu.trackingapp.activity.esm

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.lmu.trackingapp.R
import com.lmu.trackingapp.models.ESMQuestionType

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