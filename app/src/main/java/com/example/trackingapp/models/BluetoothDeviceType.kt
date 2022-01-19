package com.example.trackingapp.models

import android.bluetooth.BluetoothClass

typealias BluetoothClassType = Int

enum class BluetoothDeviceType(val constant: BluetoothClassType) {
    AUDIO_VIDEO_CAMCORDER(BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER),
    AUDIO_VIDEO_CAR_AUDIO(BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO),
    AUDIO_VIDEO_HANDSFREE(BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE),
    AUDIO_VIDEO_HEADPHONES(BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES),
    AUDIO_VIDEO_HIFI_AUDIO(BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO),
    AUDIO_VIDEO_LOUDSPEAKER(BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER),
    AUDIO_VIDEO_MICROPHONE(BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE),
    AUDIO_VIDEO_PORTABLE_AUDIO(BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO),
    AUDIO_VIDEO_SET_TOP_BOX(BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX),
    AUDIO_VIDEO_UNCATEGORIZED(BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED),
    AUDIO_VIDEO_VCR(BluetoothClass.Device.AUDIO_VIDEO_VCR),
    AUDIO_VIDEO_VIDEO_CAMERA(BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CAMERA),
    AUDIO_VIDEO_VIDEO_CONFERENCING(BluetoothClass.Device.AUDIO_VIDEO_VIDEO_CONFERENCING),
    AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER(BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER),
    AUDIO_VIDEO_VIDEO_GAMING_TOY(BluetoothClass.Device.AUDIO_VIDEO_VIDEO_GAMING_TOY),
    AUDIO_VIDEO_VIDEO_MONITOR(BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR),
    AUDIO_VIDEO_WEARABLE_HEADSET(BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET),
    COMPUTER_DESKTOP(BluetoothClass.Device.COMPUTER_DESKTOP),
    COMPUTER_HANDHELD_PC_PDA(BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA),
    COMPUTER_LAPTOP(BluetoothClass.Device.COMPUTER_LAPTOP),
    COMPUTER_PALM_SIZE_PC_PDA(BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA),
    COMPUTER_SERVER(BluetoothClass.Device.COMPUTER_SERVER),
    COMPUTER_UNCATEGORIZED(BluetoothClass.Device.COMPUTER_UNCATEGORIZED),
    COMPUTER_WEARABLE(BluetoothClass.Device.COMPUTER_WEARABLE),
    HEALTH_BLOOD_PRESSURE(BluetoothClass.Device.HEALTH_BLOOD_PRESSURE),
    HEALTH_DATA_DISPLAY(BluetoothClass.Device.HEALTH_DATA_DISPLAY),
    HEALTH_GLUCOSE(BluetoothClass.Device.HEALTH_GLUCOSE),
    HEALTH_PULSE_OXIMETER(BluetoothClass.Device.HEALTH_PULSE_OXIMETER),
    HEALTH_PULSE_RATE(BluetoothClass.Device.HEALTH_PULSE_RATE),
    HEALTH_THERMOMETER(BluetoothClass.Device.HEALTH_THERMOMETER),
    HEALTH_UNCATEGORIZED(BluetoothClass.Device.HEALTH_UNCATEGORIZED),
    HEALTH_WEIGHING(BluetoothClass.Device.HEALTH_WEIGHING),
    PHONE_CELLULAR(BluetoothClass.Device.PHONE_CELLULAR),
    PHONE_CORDLESS(BluetoothClass.Device.PHONE_CORDLESS),
    PHONE_ISDN(BluetoothClass.Device.PHONE_ISDN),
    PHONE_MODEM_OR_GATEWAY(BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY),
    PHONE_SMART(BluetoothClass.Device.PHONE_SMART),
    PHONE_UNCATEGORIZED(BluetoothClass.Device.PHONE_UNCATEGORIZED),
    TOY_CONTROLLER(BluetoothClass.Device.TOY_CONTROLLER),
    TOY_DOLL_ACTION_FIGURE(BluetoothClass.Device.TOY_DOLL_ACTION_FIGURE),
    TOY_GAME(BluetoothClass.Device.TOY_GAME),
    TOY_ROBOT(BluetoothClass.Device.TOY_ROBOT),
    TOY_UNCATEGORIZED(BluetoothClass.Device.TOY_UNCATEGORIZED),
    TOY_VEHICLE(BluetoothClass.Device.TOY_VEHICLE),
    WEARABLE_GLASSES(BluetoothClass.Device.WEARABLE_GLASSES),
    WEARABLE_HELMET(BluetoothClass.Device.WEARABLE_HELMET),
    WEARABLE_JACKET(BluetoothClass.Device.WEARABLE_JACKET),
    WEARABLE_PAGER(BluetoothClass.Device.WEARABLE_PAGER),
    WEARABLE_UNCATEGORIZED(BluetoothClass.Device.WEARABLE_UNCATEGORIZED),
    WEARABLE_WRIST_WATCH(BluetoothClass.Device.WEARABLE_WRIST_WATCH),  //Major Bluetooth Components
    AUDIO_VIDEO(BluetoothClass.Device.Major.AUDIO_VIDEO),
    COMPUTER(BluetoothClass.Device.Major.COMPUTER),
    HEALTH(BluetoothClass.Device.Major.HEALTH),
    IMAGING(BluetoothClass.Device.Major.IMAGING),
    MISC(BluetoothClass.Device.Major.MISC),
    NETWORKING(BluetoothClass.Device.Major.NETWORKING),
    PERIPHERAL(BluetoothClass.Device.Major.PERIPHERAL),
    PHONE(BluetoothClass.Device.Major.PHONE),
    TOY(BluetoothClass.Device.Major.TOY),
    UNCATEGORIZED(BluetoothClass.Device.Major.UNCATEGORIZED),
    WEARABLE(BluetoothClass.Device.Major.WEARABLE),
    UNKNOWN(-1);

    override fun toString(): String {
        return name
    }

    companion object {
        fun getTypeByConstant(constantValue: Int?): BluetoothDeviceType {
            for (BDT in values()) {
                if (BDT.constant == constantValue) {
                    return BDT
                }
            }
            return UNKNOWN
        }
    }
}
