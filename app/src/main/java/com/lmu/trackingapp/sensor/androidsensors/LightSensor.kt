package com.lmu.trackingapp.sensor.androidsensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.models.SensorAccuracy
import com.lmu.trackingapp.sensor.AbstractSensor
import com.lmu.trackingapp.service.LoggingManager
import com.lmu.trackingapp.util.CONST

class LightSensor: AbstractSensor(
    "LIGHT_SENSOR",
    "Light"
), SensorEventListener {

    private var sensorManager: SensorManager? = null

    override fun isAvailable(context: Context): Boolean {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null
    }

    override fun start(context: Context) {
        super.start(context)
        if (!isSensorAvailable) return
        val time = System.currentTimeMillis()
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT),
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
        LogEvent(LogEventName.LIGHT, timestamp, sensorData, accuracy).saveToDataBase()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

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