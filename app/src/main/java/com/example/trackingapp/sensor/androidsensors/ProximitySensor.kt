package com.example.trackingapp.sensor.androidsensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.trackingapp.util.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.SensorAccuracy
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST

class ProximitySensor : AbstractSensor(
    "PROXIMITY_SENSOR",
    "Proximity"
), SensorEventListener {

    private var sensorManager: SensorManager? = null

    override fun isAvailable(context: Context): Boolean {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Log.d(TAG, "is Sensor available: ${sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null}")
        return sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null
    }

    override fun start(context: Context) {
        super.start(context)
        if (!isSensorAvailable) return
        val time = System.currentTimeMillis()
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            sensorManager?.unregisterListener(this)
            isRunning = false
        }
    }

    fun saveEntry(timestamp: Long, sensorData: String, accuracy: String) {
        LogEvent(LogEventName.PROXIMITY, timestamp, sensorData, accuracy).saveToDataBase()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    // The Proximity sensor returns a single value either 0 or 5 (also 1 depends on Sensor manufacturer).
    // 0 for near and 5 for far
    override fun onSensorChanged(event: SensorEvent?) {
        val time = System.currentTimeMillis()
        if (isRunning && LoggingManager.userPresent && event != null) {
            try {
                when (event.accuracy) {
                    SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                        val sensorData = event.values[0].toString()
                        saveEntry(time, sensorData, SensorAccuracy.ACCURACY_UNRELAIABLE.name)
                    }
                    else -> {
                        val sensorData = event.values[0].toString()
                        saveEntry(time, sensorData, SensorAccuracy.ACCURACY_ELSE.name)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}