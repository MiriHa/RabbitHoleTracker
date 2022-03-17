package com.lmu.trackingapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.lmu.trackingapp.BuildConfig
import com.lmu.trackingapp.databinding.FragmentContactBinding
import com.lmu.trackingapp.service.LoggingManager
import com.lmu.trackingapp.util.NotificationHelper
import com.lmu.trackingapp.util.SurveryType

class ContactFragment: Fragment() {

    private lateinit var binding: FragmentContactBinding
    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentContactBinding.inflate(inflater)
        checkForStudyOver()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        checkForStudyOver()
    }

    private fun checkForStudyOver(){
        binding.cardViewQuestionnaire.visibility = if (LoggingManager.isStudyOver()) View.VISIBLE else View.GONE
        binding.textviewLinkQuestionnaire.text = NotificationHelper.createSurveyLink(SurveryType.SURVEY_END).toString()

    }

    fun testRecognition(){
        Log.d("xxx","testRecognition")
        /*val intent = Intent()
        intent.action = BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER"
        val events: MutableList<ActivityTransitionEvent> = ArrayList()
        var transitionEvent = ActivityTransitionEvent(
            DetectedActivity.STILL,
            ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos()
        )
        events.add(transitionEvent)
        transitionEvent = ActivityTransitionEvent(
            DetectedActivity.WALKING,
            ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos()
        )
        events.add(transitionEvent)
        val result = ActivityTransitionResult(events)
        SafeParcelableSerializer.serializeToIntentExtra(
            result, intent,
            "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT"
        )
        mContext.sendBroadcast(intent)
*/

        val intent = Intent()
        // Your broadcast receiver action
        intent.action = BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER"
        val events: ArrayList<ActivityTransitionEvent> = arrayListOf()
        // You can set desired events with their corresponding state
        val transitionEvent1 = ActivityTransitionEvent(DetectedActivity.WALKING, ActivityTransition.ACTIVITY_TRANSITION_EXIT, SystemClock.elapsedRealtimeNanos())
        val transitionEvent = ActivityTransitionEvent(DetectedActivity.IN_VEHICLE, ActivityTransition.ACTIVITY_TRANSITION_ENTER, SystemClock.elapsedRealtimeNanos())
        events.add(transitionEvent1)
        events.add(transitionEvent)
        val result = ActivityTransitionResult(events)
        SafeParcelableSerializer.serializeToIntentExtra(result, intent, "com.google.android.location.internal.EXTRA_ACTIVITY_TRANSITION_RESULT")
        activity?.sendBroadcast(intent)
    }
}