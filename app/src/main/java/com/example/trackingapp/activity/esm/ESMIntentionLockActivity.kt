package com.example.trackingapp.activity.esm

import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackingapp.R
import com.example.trackingapp.databinding.ActivityLockscreenEsmBinding
import com.example.trackingapp.databinding.LayoutEsmLockItemButtonsBinding
import com.example.trackingapp.databinding.LayoutEsmLockItemScaleBinding
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.NotificationHelper.dismissESMNotification
import com.example.trackingapp.util.SharedPrefManager
import com.example.trackingapp.util.turnScreenOffAndKeyguardOn
import com.example.trackingapp.util.turnScreenOnAndKeyguardOff


class ESMIntentionLockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockscreenEsmBinding
    private lateinit var viewModel: ESMIntentionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLockscreenEsmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, ESMIntentionViewModelFactory())[ESMIntentionViewModel::class.java]
        SharedPrefManager.init(this.applicationContext)

        binding.textViewEsmLockIntention.text = viewModel.savedIntention
        val listAdapter = ListAdapter(viewModel.questionList)

        binding.recyclerviewFragmentMainscreen.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@ESMIntentionLockActivity)
        }

        this.turnScreenOnAndKeyguardOff()

    }

    private fun checkOrdismissFullScreenNotification() {
        Log.d("ESM", "${viewModel.questionList.size} ${viewModel.answeredQuestions.size}")
        if (viewModel.questionList.size == viewModel.answeredQuestions.size) {
            viewModel.questionList.forEach { item ->
                viewModel.makeLogQuestion(item.value, item.questionType, System.currentTimeMillis())
            }
            this.finish()
            dismissESMNotification(this)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("xxx"," presenT: ${LoggingManager.userPresent}")
  /*      if(LoggingManager.userPresent){
            this.finish()
            dismissESMNotification(this)
        }*/
    }

    override fun onBackPressed() {
        //Do nothing
    }

    override fun onDestroy() {
        super.onDestroy()

        this.turnScreenOffAndKeyguardOn()
    }

    inner class ListAdapter(private val items: List<ESMItem>) : RecyclerView.Adapter<ListAdapter.ListViewHolder<ESMItem>>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<ESMItem> {
            val inflater = LayoutInflater.from(parent.context)
            val viewHolder = when (viewType) {
                ItemViewType.ESM_BUTTON_ITEM.ordinal -> ESMButtonItemViewHolder(LayoutEsmLockItemButtonsBinding.inflate(inflater, parent, false))
                ItemViewType.ESM_SLIDER_ITEM.ordinal -> ESMSliderItemViewHolder(LayoutEsmLockItemScaleBinding.inflate(inflater, parent, false))
                else -> throw IllegalArgumentException("Unknown ViewType")
            }
            @Suppress("UNCHECKED_CAST")
            return viewHolder as ListViewHolder<ESMItem>
        }

        override fun getItemViewType(position: Int): Int {
            return when (items[position]) {
                is ESMRadioGroupItem -> ItemViewType.ESM_BUTTON_ITEM.ordinal
                is ESMSliderItem -> ItemViewType.ESM_SLIDER_ITEM.ordinal
            }
        }

        override fun onBindViewHolder(holder: ListViewHolder<ESMItem>, position: Int) {

            holder.bindData(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        abstract inner class ListViewHolder<T>(root: View) : RecyclerView.ViewHolder(root) {
            abstract fun bindData(item: T)
            fun setValue(item: ESMItem, value: String) {
                item.value = value
                if (!viewModel.answeredQuestions.contains(item.questionType)) viewModel.answeredQuestions.add(item.questionType)
            }
        }

        inner class ESMButtonItemViewHolder(private val itemBinding: LayoutEsmLockItemButtonsBinding) : ListViewHolder<ESMRadioGroupItem>(itemBinding.root) {

            override fun bindData(item: ESMRadioGroupItem) {
                itemBinding.esmLockIntentionQuestion.text = getString(item.question)

                item.buttonList.forEach { label ->
                    addButton(item, label)
                }
            }

            private fun addButton(item: ESMRadioGroupItem, label: Int){
                val radioButton = RadioButton(ContextThemeWrapper(this@ESMIntentionLockActivity, R.style.RadioButtonESM))
                radioButton.apply {
                    val buttonText = getString(label)
                    text = buttonText
                    setTextColor(context.resources.getColor(R.color.white, null))
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            setValue(item, buttonText)
                            checkOrdismissFullScreenNotification()
                        }
                    }
                }
                itemBinding.radioGroupEsmLock.addView(radioButton)

            }

        }

        inner class ESMSliderItemViewHolder(private val itemBinding: LayoutEsmLockItemScaleBinding) : ListViewHolder<ESMSliderItem>(itemBinding.root) {
            override fun bindData(
                item: ESMSliderItem,
            ) {
                //already add the question as it can be left on the lowest value
                viewModel.answeredQuestions.add(item.questionType)

                itemBinding.esmLockItemSliderQuestion.text = getString(item.question)
                itemBinding.textviewEsmLockItemSliderMin.text = item.sliderMinLabel
                itemBinding.textviewEsmLockItemSliderMax.text = item.sliderMaxLabel
                itemBinding.sliderEsmLockItemSlider.apply {
                    stepSize = item.sliderStepSize
                    valueFrom = item.sliderMin
                    valueTo = item.sliderMax
                    setLabelFormatter { this.value.toString() }
                    addOnChangeListener { _, value, _ ->
                        this.setLabelFormatter { value.toInt().toString() }
                        setValue(item, value.toString())
                        checkOrdismissFullScreenNotification()
                    }

                }


            }
        }
    }

    private enum class ItemViewType {
        ESM_BUTTON_ITEM,
        ESM_SLIDER_ITEM
    }
}


