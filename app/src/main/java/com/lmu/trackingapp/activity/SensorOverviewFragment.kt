package com.lmu.trackingapp.activity


import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lmu.trackingapp.R
import com.lmu.trackingapp.databinding.CustomListItemBinding
import com.lmu.trackingapp.databinding.FragmentSensorOverviewBinding
import com.lmu.trackingapp.sensor.AbstractSensor
import com.lmu.trackingapp.service.LoggingManager

class SensorOverviewFragment : Fragment() {

    private lateinit var binding: FragmentSensorOverviewBinding
    private lateinit var mContext: Context

    private lateinit var listAdapter: ListAdapter

    private val loggingObserver = Observer<Boolean> { setAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSensorOverviewBinding.inflate(inflater)
        val view = binding.root
        setAdapter()

        LoggingManager.isLoggingActive.observe(this.viewLifecycleOwner, loggingObserver)

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun setAdapter() {
            listAdapter = ListAdapter()
            binding.recyclerviewFragmentMainscreen.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(mContext)
            }
    }

    inner class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
        private val items: MutableList<AbstractSensor>
            get() = LoggingManager.sensorList

        inner class ViewHolder(val binding: CustomListItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = CustomListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            with(holder) {
                with(items[position]) {
                    binding.textViewListItemTitle.text = this.sensorName + " Sensor"
                    val runningText =  if (LoggingManager.isLoggingActive.value == true) getString(R.string.mainScreen_sensorList_is_running) else getString(R.string.mainScreen_sensorList_not_running)
                      //  if (this.isRunning) getString(R.string.mainScreen_sensorList_is_running) else getString(R.string.mainScreen_sensorList_not_running)
                    val drawable = if (LoggingManager.isLoggingActive.value == true) R.drawable.circle_green else R.drawable.circle_red
                    binding.textViewListItemSubtile.apply{
                        text = runningText
                        setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
                        compoundDrawablePadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics).toInt()
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }
}