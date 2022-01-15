package com.example.trackingapp.unused

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil

@SuppressLint("Registered")
open class AbstractService : ForegroundService() {
    private val mActivityRecognitionPendingIntent: PendingIntent? = null
    private var mInProgress = false

    enum class REQUEST_TYPE {
        START, STOP
    }

    private var mRequestType: REQUEST_TYPE? = null
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun servicesConnected(): Boolean {
        Log.d(TAG, "activity servicesConnected")
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
        return if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, "Google Play services is available.")
            true
        } else {
            false
        }
    }

    fun onConnectionFailed(connectionResult: ConnectionResult) {
        mInProgress = false
        if (connectionResult.hasResolution()) {
            try {
                //connectionResult.startResolutionForResult(
                //    null,
                //    CONNECTION_FAILURE_RESOLUTION_REQUEST
               // )
            } catch (e: SendIntentException) {
                Log.e(TAG, e.toString())
            }
        } else {
            val errorCode = connectionResult.errorCode
            Log.d(TAG, "ERROR: $errorCode")
        }
    }

    fun onConnected() {
        Log.d(TAG, "activity onConnected")
        mInProgress = false
    }

    fun onDisconnected() {
        mInProgress = false
    }

    fun startUpdates() {
        Log.d(TAG, "start activity updates")
        mRequestType = REQUEST_TYPE.START
        if (!servicesConnected()) {
            return
        }
        if (!mInProgress) {
            mInProgress = true
        }
    }

    fun stopUpdates() {
        Log.d(TAG, "stop activity updates")
        mRequestType = REQUEST_TYPE.STOP
        if (!servicesConnected()) {
            return
        }
        if (!mInProgress) {
            mInProgress = true
        }
    }

    companion object {
        private const val MILLISECONDS_PER_SECOND = 1000
        private const val DETECTION_INTERVAL_SECONDS = 20
        const val DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS
        private const val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
    }
}