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

class GyroscopeSensor : AbstractSensor(
    "GYROSCOPE_SENSOR",
    "Gyroscope"
), SensorEventListener {

    private var sensorManager: SensorManager? = null

    override fun isAvailable(context: Context): Boolean {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
    }

    override fun start(context: Context) {
        super.start(context)
        if (!isSensorAvailable) return
        val time = System.currentTimeMillis()
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
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
        LogEvent(LogEventName.GYROSCOPE, timestamp, sensorData, accuracy).saveToDataBase()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val time = System.currentTimeMillis()
        if (isRunning && LoggingManager.userPresent && event != null) {
            try {
                when (event.accuracy) {
                    SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                        val sensorData = "${event.values[0]}, ${event.values[1]}, ${event.values[2]}"
                        saveEntry(time, sensorData, SensorAccuracy.ACCURACY_UNRELAIABLE.name)
                    }
                    else -> {
                        val sensorData = "${event.values[0]}, ${event.values[1]}, ${event.values[2]}"
                        saveEntry(time, sensorData, SensorAccuracy.ACCURACY_ELSE.name)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}
