package com.example.trackingapp.sensor.implementation

import android.content.Context
import android.provider.Settings
import android.view.View
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.Event
import com.example.trackingapp.models.EventName
import com.example.trackingapp.models.ONOFFSTATE
import com.example.trackingapp.sensor.AbstractSensor

class AirplaneModeSensor: AbstractSensor(
    "AIRPLANE_MODE_SENSOR",
    "airplane"
) {

    override fun getSettingsView(context: Context?): View? {
        return null
    }

    override fun isAvailable(context: Context?): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        if (!m_isSensorAvailable) return
        isRunning = true
        val timestamp = System.currentTimeMillis()
        if(isAirplaneModeOn(context)){
            saveEntry(ONOFFSTATE.ON, timestamp)
        } else {
            saveEntry(ONOFFSTATE.OFF, timestamp)
        }
    }

    override fun saveSnapshot(context: Context) {
        super.saveSnapshot(context)
        val timestamp = System.currentTimeMillis()
        if(isAirplaneModeOn(context)){
            saveEntry(ONOFFSTATE.ON, timestamp)
        } else {
            saveEntry(ONOFFSTATE.OFF, timestamp)
        }
    }

    override fun stop() {
        if (isRunning)
            isRunning = false

    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.System.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
    }

    private fun saveEntry(state: ONOFFSTATE, timestamp: Long) {
        Event(EventName.AIRPLANEMODE, timestamp, state.name).saveToDataBase()
    }
}