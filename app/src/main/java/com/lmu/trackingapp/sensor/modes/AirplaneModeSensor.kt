package com.lmu.trackingapp.sensor.modes

import android.content.Context
import android.provider.Settings
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.models.ONOFFSTATE
import com.lmu.trackingapp.sensor.AbstractSensor

class AirplaneModeSensor: AbstractSensor(
    "AIRPLANE_MODE_SENSOR",
    "Airplane"
) {

    override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        if (!isSensorAvailable) return
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
        LogEvent(LogEventName.AIRPLANEMODE, timestamp, state.name).saveToDataBase()
    }
}