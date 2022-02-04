package com.example.trackingapp.sensor.androidsensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
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

    override fun isAvailable(context: Context?): Boolean {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
    }

    override fun start(context: Context) {
        super.start(context)
        if (!m_isSensorAvailable) return
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

//    private val NS2S = 1.0f / 1000000000.0f
//    private val deltaRotationVector = FloatArray(4) { 0f }
//    private var timestamp: Float = 0f
//    private val EPSILON = 5120
//    private var rotationCurrent =  FloatArray(4) { 0f }

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
//        if (timestamp != 0f && event != null) {
//            val dT = (event.timestamp - timestamp) * NS2S
//            // Axis of the rotation sample, not normalized yet.
//            var axisX: Float = event.values[0]
//            var axisY: Float = event.values[1]
//            var axisZ: Float = event.values[2]
//
//            // Calculate the angular speed of the sample
//            val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)
//
//            // Normalize the rotation vector if it's big enough to get the axis
//            // (that is, EPSILON should represent your maximum allowable margin of error)
//            if (omegaMagnitude > EPSILON) {
//                axisX /= omegaMagnitude
//                axisY /= omegaMagnitude
//                axisZ /= omegaMagnitude
//            }
//
//            // Integrate around this axis with the angular speed by the timestep
//            // in order to get a delta rotation from this sample over the timestep
//            // We will convert this axis-angle representation of the delta rotation
//            // into a quaternion before turning it into the rotation matrix.
//            val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
//            val sinThetaOverTwo: Float = sin(thetaOverTwo)
//            val cosThetaOverTwo: Float = cos(thetaOverTwo)
//            deltaRotationVector[0] = sinThetaOverTwo * axisX
//            deltaRotationVector[1] = sinThetaOverTwo * axisY
//            deltaRotationVector[2] = sinThetaOverTwo * axisZ
//            deltaRotationVector[3] = cosThetaOverTwo
//        }
//        timestamp = event?.timestamp?.toFloat() ?: 0f
//        val deltaRotationMatrix = FloatArray(9) { 0f }
//        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
//        // User code should concatenate the delta rotation we computed with the current rotation
//        // in order to get the updated rotation.
//        //get currentrotation?
//        rotationCurrent = rotationCurrent * deltaRotationMatrix;


    }
}