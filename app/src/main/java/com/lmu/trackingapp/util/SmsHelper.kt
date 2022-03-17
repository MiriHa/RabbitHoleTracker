package com.lmu.trackingapp.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.lmu.trackingapp.models.LogEvent
import com.lmu.trackingapp.models.LogEventName
import com.lmu.trackingapp.models.SmsEventType
import com.lmu.trackingapp.models.metadata.MetaSMS
import com.lmu.trackingapp.util.DatabaseManager.saveToDataBase

object SmsHelper {
    private const val TAG = "SmsHelper"

    const val CONTENT_URI = "content://sms/"
    const val ADDRESS = "address"
    const val BODY = "body"
    const val DATE = "date"
    const val TYPE = "type"

    /**
     * Gets contact uid by number - changed to return CONTACT_ID, previously got baseColumns _ID
     */
    fun getContactUidByNumber(context: Context, number: String?): Int {
        Log.d(TAG, "getContactUidByNumber()")
        var uid = 0
        var c: Cursor? = null
        val projection = arrayOf(ContactsContract.PhoneLookup.CONTACT_ID, ContactsContract.PhoneLookup.NUMBER)
        val contact = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(
                PhoneNumberHelper.formatNumber(
                    number!!
                )
            )
        )
        try {
            c = context.contentResolver.query(contact, projection, null, null, null)
            val uidIndex = c!!.getColumnIndex(ContactsContract.PhoneLookup.CONTACT_ID)
            if (c.moveToFirst()) {
                uid = c.getInt(uidIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        c?.close()
        return uid

    }

    fun getContactNameByNumber(context: Context, number: String?): String? {
        Log.d(TAG, "getContactNameByNumber()")
        var name: String? = null
        var c: Cursor? = null
        val selection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.NUMBER)
        val contact = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        try {
            c = context.contentResolver.query(contact, selection, null, null, null)
            val displayName = c!!.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            if (c.moveToFirst()) {
                name = c.getString(displayName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        c?.close()
        return name
    }

    fun saveEntry(
        save: Boolean,
        type: SmsEventType,
        timestamp: Long,
        numberHashed: String?,
        countryCode: String?,
        contactName: String?,
        messageHash: String?,
        messageLength: Int,
        contactUID: Int,
        smsID: String
    ) {
        if (!save) {
            val metaSms = MetaSMS(
                phoneNumber = numberHashed,
                countryCode = countryCode,
                partner = contactName,
                length = messageLength,
                messageHash = messageHash,
                contactId = contactUID
            )
            LogEvent(
                eventName = LogEventName.SMS,
                timestamp = timestamp,
                event = type.name,
                description = smsID,
            ).saveToDataBase(metaSms)
        } else {
            LogEvent(
                eventName = LogEventName.SMS,
                timestamp = timestamp,
                event = type.name,
                description = smsID,
            ).saveToDataBase()
        }
    }


    fun generateSmsID(partnerNumber: String, timestamp: Long): String {
        Log.d(TAG, "generateSmsID()")
        return "$partnerNumber:$timestamp"
    }

    fun getTypeByConstant(constantValue: Int): SmsEventType {
        return when (constantValue) {
            1 -> SmsEventType.INBOX
            2 -> SmsEventType.SENT
            3 -> SmsEventType.DRAFT
            4 -> SmsEventType.OUTBOX
            else -> SmsEventType.UNKNOWN
        }
    }
}
