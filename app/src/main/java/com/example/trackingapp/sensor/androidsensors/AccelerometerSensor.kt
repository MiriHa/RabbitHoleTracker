package com.example.trackingapp.sensor.androidsensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.SensorAccuracy
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST

class AccelerometerSensor : AbstractSensor(
    "ACCELEROMETER_SENSOR",
    "accelerometer"
), SensorEventListener {
    private var sensorManager: SensorManager? = null

    override fun getSettingsView(context: Context?): View {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(20, 10, 20, 10)
        val textView = TextView(context)
        textView.text = "Sensor delay"
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
        val radioGroup = RadioGroup(context)
        val r1 = RadioButton(context)
        r1.text = "Normal"
        radioGroup.addView(r1)
        val r2 = RadioButton(context)
        r2.text = "Game"
        radioGroup.addView(r2)
        val r3 = RadioButton(context)
        r3.text = "Fastest"
        radioGroup.addView(r3)
        r3.isChecked = true
        linearLayout.addView(textView)
        linearLayout.addView(radioGroup)
        return linearLayout
    }

    override fun isAvailable(context: Context?): Boolean {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    }

    override fun start(context: Context) {
        super.start(context)
        if (!m_isSensorAvailable) return
        val time = System.currentTimeMillis()
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager?.registerListener(
            this,
            sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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
        LogEvent(LogEventName.ACCELEROMETER, timestamp, sensorData, accuracy).saveToDataBase()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val time = System.currentTimeMillis()
        if (isRunning && LoggingManager.userPresent) {
            try {
                when (event?.accuracy) {
                    SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                        val sensorData = "${CONST.numberFormat.format(event.values[0])}, " +
                                "${CONST.numberFormat.format(event.values[1])}, ${CONST.numberFormat.format(event.values[2])}"
                        saveEntry(time, sensorData, SensorAccuracy.ACCURACY_UNRELAIABLE.name)
                    }
                    //TOOD SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
                    else -> {
                        val sensorData = "${CONST.numberFormat.format(event?.values?.get(0))}, " +
                                "${CONST.numberFormat.format(event?.values?.get(1))}, ${CONST.numberFormat.format(event?.values?.get(2))}"
                        saveEntry(time, sensorData, SensorAccuracy.ACCURACY_ELSE.name)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}