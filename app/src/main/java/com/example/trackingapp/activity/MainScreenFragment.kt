package com.example.trackingapp.activity


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackingapp.databinding.FragmentMainscreenBinding
import com.example.trackingapp.sensor.SensorList
import com.example.trackingapp.util.ScreenType
import com.example.trackingapp.util.navigate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainScreenFragment: Fragment() {

    private val TAG = javaClass.name

    private lateinit var binding: FragmentMainscreenBinding
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var mContext: Context

    private lateinit var notificationManager: NotificationManagerCompat


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, MainScreenViewModelFactory())[MainScreenViewModel::class.java]

        binding = FragmentMainscreenBinding.inflate(inflater)
        val view = binding.root
        notificationManager = NotificationManagerCompat.from(mContext)

        binding.buttonTest.setOnClickListener {
           /* NotificationHelper.createFullScreenNotification(
                mContext,
                notificationManager,
                mContext.getString(
                R.string.esm_during_intention_question) )*/
            startTestSensors()
        }

        binding.signOut.setOnClickListener {
            Firebase.auth.signOut()
            navigate(ScreenType.Welcome, ScreenType.HomeScreen)
        }


        return view
    }

    fun startTestSensors(){
        val sensorList = SensorList.getList(mContext)
        Log.d("xxx", "size: " + sensorList.size)
        for (sensor in sensorList) {
            if (/*sensor.isEnabled && */sensor.isAvailable(mContext)) {
                sensor.start(mContext)
                //if(sensor instanceof MyAccelerometerSensor) ((MyAccelerometerSensor)sensor).start(this);
                //if(sensor instanceof AppSensor) ((AppSensor)sensor).start(this);
                Log.d(TAG, sensor.sensorName + " turned on")
            } else {
                Log.w(TAG, sensor.sensorName + " turned off")
            }
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


}