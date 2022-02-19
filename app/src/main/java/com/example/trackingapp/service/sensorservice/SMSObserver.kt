package com.example.trackingapp.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.trackingapp.models.SmsEventType
import com.example.trackingapp.util.PhoneNumberHelper
import com.example.trackingapp.util.SmsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SmsObserver(handler: Handler?, context: Context) : ContentObserver(handler) {
    private val context: Context
    private var lastOnChangeCall = System.currentTimeMillis()

    init {
        Log.d(TAG, "constructor")
        this.context = context
    }

    override fun onChange(selfChange: Boolean) {
        if (!LoggingManager.isDataRecordingActive) {
            return
        }

        if (System.currentTimeMillis() - lastOnChangeCall < 3000) {
            Log.d(TAG, "no URI provided in onChange call")
            this.onChange(selfChange, null)
        }
        lastOnChangeCall = System.currentTimeMillis()
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        if (!LoggingManager.isDataRecordingActive) {
            return
        }
        if (System.currentTimeMillis() - lastOnChangeCall < 2000) {
            Log.d(TAG, "last onChange less than 2s ago, aborted")
            return
        }
        lastOnChangeCall = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            getSMS(context)
        }
    }

    private fun getSMS(context: Context) {
        Log.d(TAG, "getSMS()")
        var c: Cursor? = null
        val projection: Array<String>? = null
        val sortOrder: String = SmsHelper.DATE.toString() + " desc"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                c = context.contentResolver.query(
                    Uri.parse(SmsHelper.CONTENT_URI),
                    projection, null, null, sortOrder
                )
                if (c != null) {
                    val addressIndex = c.getColumnIndexOrThrow(SmsHelper.ADDRESS) //Telephony.TextBasedSmsColumns.ADDRESS
                    val bodyIndex = c.getColumnIndexOrThrow(SmsHelper.BODY) //Telephony.TextBasedSmsColumns.BODY
                    val dateIndex = c.getColumnIndexOrThrow(SmsHelper.DATE) //Telephony.TextBasedSmsColumns.DATE
                    val typeIndex = c.getColumnIndexOrThrow(SmsHelper.TYPE) //Telephony.TextBasedSmsColumns.TYPE

                    if (c.moveToFirst()) {
                        val type = c.getInt(typeIndex)
                        val eventType: SmsEventType = SmsHelper.getTypeByConstant(type)
                        val address = c.getString(addressIndex)
                        val body = c.getString(bodyIndex)
                        val timestamp = c.getLong(dateIndex)

                        //generate ID
                        val smsID: String = SmsHelper.generateSmsID(address, timestamp)
                        val partnerName: String? = SmsHelper.getContactNameByNumber(context, address)
                        val partnerUID: Int = SmsHelper.getContactUidByNumber(context, address) //if this is 0, then partner number is not saved in contacts
                        val messageLength = body.length
                        val countryCode = getCountryCode(address)
                        val hashedNumber: String = PhoneNumberHelper.formatNumber(address).hashCode().toString()

                        val save = true
                        SmsHelper.saveEntry(
                            save = save,
                            type = eventType,
                            timestamp= timestamp,
                            numberHashed= hashedNumber,
                            countryCode= countryCode,
                            contactName = partnerName.hashCode().toString(),
                            messageHash= body.hashCode().toString(),
                            messageLength= messageLength,
                            contactUID= partnerUID,
                            smsID= smsID
                        )
                        Log.i(TAG, "Checked sms was already saved, updated")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Problem with extracting data from sms. ")
            } finally {
                c?.close()
            }
        }
    }

    override fun deliverSelfNotifications(): Boolean {
        Log.d(TAG, "deliverSelfNotifications()")
        return true
    }

    private fun getCountryCode(number: String): String {
        return PhoneNumberHelper.extractCountryCodeFromNumber(number)
    }

    companion object {
        var TAG = "SmsObserver"
    }
}
