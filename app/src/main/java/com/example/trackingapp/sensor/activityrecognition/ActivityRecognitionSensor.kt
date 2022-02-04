package com.example.trackingapp.sensor.activityrecognition

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import com.example.trackingapp.BuildConfig
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.ActivityTransitionType
import com.example.trackingapp.models.ActivityType
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.util.CONST
import com.google.android.gms.location.*


class ActivityRecognitionSensor : AbstractSensor(
    "ACTIVITY_RECOGNITION_SENSOR",
    "Activity Recognition"
) {
    private var mContext: Context? = null
    private var mReceiver: BroadcastReceiver? = null
    val filter = BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER"

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        mContext = context

        val transitions = createTransitions()
        val request = ActivityTransitionRequest(transitions)

        val client = ActivityRecognition.getClient(context)
        Log.d("xxx", "client: $client")
        val task = client.requestActivityTransitionUpdates(request, getPendingIntent(context))
        //val task2 = client.requestActivityUpdates(1000, getPendingIntent(context))

        task.addOnSuccessListener {
            Log.d("xxx", "Successfully requested activity updates  ${task.result}")
            Toast.makeText(
                context,
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT
            ).show()
        }
        task.addOnFailureListener {
            Log.d("xxx", "Requesting activity updates failed to start ${task.result} ${it.message} ${it.cause?.message}")
            Toast.makeText(
                context,
                "Requesting activity updates failed to start",
                Toast.LENGTH_SHORT
            ).show()
        }

//        task2.addOnSuccessListener {
//            Log.d("xxx", "2 Successfully requested activity updates")
//            Toast.makeText(
//                context,
//                " 2 Successfully requested activity updates",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        task2.addOnFailureListener   {
//            Log.d("xxx", " 2 Requesting activity updates failed to start")
//                Toast.makeText(
//                    context,
//                    " 2 Requesting activity updates failed to start",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }


            isRunning = true
        }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext?.let {
                val pendingIntent = getPendingIntent(mContext)
                val task = ActivityRecognition.getClient(it).removeActivityTransitionUpdates(pendingIntent)
                task.addOnSuccessListener {
                    pendingIntent.cancel()
                    Toast.makeText(
                        mContext,
                        "Succesfully deregisterd from Activity Recognition.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                task.addOnFailureListener { e ->
                    Log.d(TAG, "Failed to remove ActivityTransitionUpdates. ${e.message}")
                    Toast.makeText(
                        mContext,
                        "Failed to deregister from activityRecognition.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun getPendingIntent(context: Context?): PendingIntent {
        val intent = Intent(context, ActivityRecognitionReceiver()::class.java)
        mReceiver = ActivityRecognitionReceiver()
        val intentFilter = IntentFilter(filter)
        try {
            context?.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        context?.registerReceiver(mReceiver, intentFilter)
        return PendingIntent.getBroadcast(context, REQUEST_CODE_INTENT_ACTIVITY_TRANSITION, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    }

    private fun createTransitions(): List<ActivityTransition> {
        val transitions = mutableListOf<ActivityTransition>()
        val activities: List<Int> = listOf(
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
//            DetectedActivity.ON_FOOT,
            DetectedActivity.RUNNING,
            DetectedActivity.STILL,
//            DetectedActivity.TILTING,
            DetectedActivity.WALKING
        )

        activities.forEach { activity ->
            transitions += transition(activity, ActivityTransition.ACTIVITY_TRANSITION_ENTER)
            transitions += transition(activity, ActivityTransition.ACTIVITY_TRANSITION_EXIT)
        }

        return transitions
    }

    private fun transition(activity: Int, transition: Int): ActivityTransition {
        return ActivityTransition.Builder()
            .setActivityType(activity)
            .setActivityTransition(transition)
            .build()
    }

    private fun userFacingTransition(type: Int): String {
        return when (type) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                ActivityTransitionType.ACTIVITY_TRANSITION_ENTER.name
            }
            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                ActivityTransitionType.ACTIVITY_TRANSITION_EXIT.name
            }
            else -> {
                ActivityTransitionType.ACTIVITY_TRANSITION_UNKNOWN.name
            }
        }
    }


    private fun userFacingActivity(type: Int): String {

        return when (type) {
                DetectedActivity.IN_VEHICLE -> ActivityType.IN_VEHICLE.name
                DetectedActivity.ON_BICYCLE -> ActivityType.ON_BICYCLE.name
                DetectedActivity.ON_FOOT -> ActivityType.ON_FOOT.name
                DetectedActivity.RUNNING -> ActivityType.RUNNING.name
                DetectedActivity.STILL -> ActivityType.STILL.name
                DetectedActivity.TILTING -> ActivityType.TILTING.name
                DetectedActivity.WALKING -> ActivityType.WALKING.name
                else -> ActivityType.UNKNOWN.name
            }
    }

    fun saveEntry(timestamp: Long, activity: String, transition: String, elapasedTime: Long) {
        LogEvent(LogEventName.ACTIVITY, timestamp, activity, transition, elapasedTime.toString()).saveToDataBase()
    }

    companion object {
        const val REQUEST_CODE_INTENT_ACTIVITY_TRANSITION = 122

    }


    inner class ActivityRecognitionReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val time = System.currentTimeMillis()

            intent?.let {
                if (ActivityTransitionResult.hasResult(intent)) {
                    val result = ActivityTransitionResult.extractResult(intent)
                    if (result != null) {
                        for (event in result.transitionEvents) {
                            val activity = userFacingActivity(event.activityType)
                            val transition = userFacingTransition(event.transitionType)
                            saveEntry(time, activity, transition, event.elapsedRealTimeNanos)
                        }
                    }
                }
            }

        }

    }

}