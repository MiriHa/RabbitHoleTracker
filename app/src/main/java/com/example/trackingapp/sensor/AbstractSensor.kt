package com.example.trackingapp.sensor

import android.content.Context
import android.util.Log
import android.view.View
import java.io.Serializable

abstract class AbstractSensor protected constructor() : Serializable {
    @JvmField
    protected var TAG: String? = null
    var sensorName: String? = null
        protected set
    var isEnabled = true

    @JvmField
    protected var m_isSensorAvailable = false

    var isRunning = false
        protected set

    val settingsState: Int
        get() = 0

    abstract fun getSettingsView(context: Context?): View?
    abstract fun isAvailable(context: Context?): Boolean
    open fun start(context: Context) {
        m_isSensorAvailable = isAvailable(context)
        if (!m_isSensorAvailable) Log.i(TAG, "Sensor not available")
        initFile(context)
    }

    fun initFile(context: Context) {
    }

    //TODO remoe streams

    abstract fun stop()

    companion object {
        private const val serialVersionUID = 1L
    }
}