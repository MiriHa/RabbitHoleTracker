package com.example.trackingapp.sensor.connection

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.BluetoothDeviceType
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.sensor.AbstractSensor
import com.example.trackingapp.service.LoggingManager
import com.example.trackingapp.util.CONST

class BluetoothSensor : AbstractSensor(
    "BLUETOOTH_SENSOR",
    "Bluetooth"
) {

    private var mReceiver: BroadcastReceiver? = null
    private var mContext: Context? = null

   override fun isAvailable(context: Context): Boolean {
        return true
    }

    override fun start(context: Context) {
        super.start(context)
        val time = System.currentTimeMillis()
        if (!m_isSensorAvailable) return
        Log.d(TAG, "StartSensor: ${CONST.dateTimeFormat.format(time)}")

        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)

        mContext = context
        mReceiver = BTReceiver()
        try {
            context.unregisterReceiver(mReceiver)
        } catch (e: Exception) {
            //Not Registered
        }
        context.registerReceiver(mReceiver, filter)
        isRunning = true
    }

    override fun stop() {
        if (isRunning) {
            isRunning = false
            mContext?.unregisterReceiver(mReceiver)
        }
    }

    inner class BTReceiver : BroadcastReceiver() {
        var state = -1

        override fun onReceive(context: Context?, intent: Intent) {
            if (LoggingManager.isDataRecordingActive == false) {
                return
            }

            val timestamp = System.currentTimeMillis()
            val action = intent.action
            val extras = intent.extras

            Log.d(TAG, "Action: " + intent.action)
            val deviceName: String?
            val deviceType: String?

            when (action) {

                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    // resolve adapter's power state (on / off).
                    try {
                        extras?.let {
                            state = it.getInt(BluetoothAdapter.EXTRA_STATE)
                        }
                        Log.d(TAG, "EXTRA_STATE: $state")
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                    // save entry only if state could be resolved and adapter is not one of turning on/off,
                    // because the turning on/off states are always occurring before the state is finally on/off.
                    if (state != -1 && state != BluetoothAdapter.STATE_TURNING_ON && state != BluetoothAdapter.STATE_TURNING_OFF) {
                        saveEntry("null", "null", getReadableState(state), timestamp)
                    }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    deviceName = resolveRemoteDeviceName(intent)
                    deviceType = resolveRemoteDeviceType(intent)
                    extras?.let { state = resolveConnectionState(it) }
                    if (!deviceName.isNullOrEmpty() && state != -1) {
                        saveEntry(deviceType, deviceName, getReadableBondState(state), timestamp)
                    }
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    state = CONNECTED
                    deviceName = resolveRemoteDeviceName(intent)
                    deviceType = resolveRemoteDeviceType(intent)
                    if (!deviceName.isNullOrEmpty() && state != -1) {
                        saveEntry(deviceType, deviceName, getReadableState(state), timestamp)
                    }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    state = DISCONNECTED
                    deviceName = resolveRemoteDeviceName(intent)
                    deviceType = resolveRemoteDeviceType(intent)
                    if (!deviceName.isNullOrEmpty() && state != -1) {
                        saveEntry(deviceType, deviceName, getReadableState(state), timestamp)
                    }
                }
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                    deviceName = resolveRemoteDeviceName(intent)
                    deviceType = resolveRemoteDeviceType(intent)
                    extras?.let {
                        state = resolveConnectionState(it)
                    }
                    if (!deviceName.isNullOrEmpty() && state != -1) {
                        saveEntry(deviceType, deviceName, getReadableState(state), timestamp)
                    }
                }
                else -> {
                    Log.d(TAG, "Ignoring action: " + action.toString())
                }
            }
        }

        private fun resolveRemoteDeviceName(intent: Intent)
                : String {
            Log.d(TAG, "resolveRemoteDeviceName()")
            var deviceName = ""
            try {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                deviceName = device?.name.toString()
                Log.d(TAG, "Connected device name: $deviceName")
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            return deviceName
        }


        /**
         * Resolves remote device type.
         *
         * @param intent    Intent from onReceiver
         * @return          Device type
         */
        private fun resolveRemoteDeviceType(intent: Intent)
                : String {
            Log.d(TAG, "resolveRemoteDeviceType()")
            var deviceType = ""
            try {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                //If the Bluetooth Device has a Minor Component btClass will be the Minor Component. Otherwise btClass will be the Major Bluetooth Component
                val btClass = device?.bluetoothClass?.deviceClass
                deviceType = BluetoothDeviceType.getTypeByConstant(btClass).toString()
                Log.d(TAG, "Connected DeviceClass: $deviceType")
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            return deviceType
        }

        /**
         * Resolves connection state.
         *
         * @param extras    Intent's extras from onReceiver
         * @return          State integer
         */
        private fun resolveConnectionState(extras: Bundle)
                : Int {
            Log.d(TAG, "resolveConnectionState()")
            var state = -1
            try {
                state = extras.getInt(BluetoothDevice.EXTRA_BOND_STATE)
                Log.d(TAG, "CONNECTION_STATE: $state")
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            return state
        }


        fun saveEntry(type: String, name: String, event: String, timestamp: Long) {
            LogEvent(LogEventName.BLUETOOTH, timestamp, event, type, name.hashCode().toString()).saveToDataBase()
        }


        private fun getReadableState(stateId: Int): String {
            Log.d(TAG, "getReadableState()")
            var state = ""
            when (stateId) {
                0 -> state = "DISCONNECTED"
                1 -> state = "CONNECTING"
                2 -> state = "CONNECTED"
                3 -> state = "DISCONNECTING"
                10 -> state = "OFF"
                11 -> state = "TURNING_ON"
                12 -> state = "ON"
                13 -> state = "TURNING_OFF"
                else -> {
                }
            }
            return state
        }


        private fun getReadableBondState(stateId: Int): String {
            Log.d(TAG, "getReadableBondState()")
            var state = ""
            when (stateId) {
                10 -> state = "BOND_NONE"
                11 -> state = "BOND_BONDING"
                12 -> state = "BOND_BONDED"
                else -> {
                }
            }
            return state
        }


    }

    companion object {
        const val DISCONNECTED = 0
        const val CONNECTED = 2

    }
}