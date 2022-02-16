package com.example.trackingapp.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.example.trackingapp.DatabaseManager.saveToDataBase
import com.example.trackingapp.models.LogEvent
import com.example.trackingapp.models.LogEventName
import com.example.trackingapp.models.SmsEventType
import com.example.trackingapp.models.metadata.MetaSMS

object SmsHelper {
    private const val TAG = "SmsHelper"

    // Replaced Telephony constants that the SmsObserver also works on devices prior Android 19
    const val CONTENT_URI = "content://sms/"
    const val ADDRESS = "address" //Telephony.TextBasedSmsColumns.ADDRESS
    const val BODY = "body" //Telephony.TextBasedSmsColumns.BODY
    const val DATE = "date" //Telephony.TextBasedSmsColumns.DATE
    const val TYPE = "type" //Telephony.TextBasedSmsColumns.TYPE
    const val _ID = "_id" //Telephony.BaseMmsColumns._ID

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
        } finally {
            c?.close()
            return uid
        }
    }

    /**
     * Gets contact name by number
     * @param context ApplicationContext
     * @param number telephone number / address of contact
     * @return contact display name
     */
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
        } finally {
            c?.close()
            return name
        }
    }

    /**
     * Method to save an SMS LogEvent to the database.
     *
     * @param save indicates wether dataset should be inserted or saved. Meaning of save: checks if exists, if true update, else insert.) see: https://agrosner.gitbooks.io/dbflow/content/StoringData.html
     * @param type SmsEventType
     * @param timestamp timestamp (sent or received or last edited)
     * @param numberHashed number of messaging partner
     * @param countryCode countryCode of the number
     * @param contactName name of messaging partner
     * @param messageLength length of message
     * @param contactUID internal UID of messaging partner
     * @param smsID ID of sms generated via SmsHelper
     */
    fun saveEntry(
        context: Context?,
        save: Boolean,
        type: SmsEventType,
        timestamp: Long,
        number: String?,
        numberHashed: String?,
        countryCode: String?,
        contactName: String?,
        messageHash: String?,
        messageLength: Int,
        contactUID: Int,
        smsID: String
    ) {
        if (!save) {
            val metaSms: MetaSMS = MetaSMS(
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

    // Method overload to make it work without save parameter. false indicates, that dataset should be inserted.
    fun saveEntry(
        context: Context?,
        type: SmsEventType,
        timestamp: Long,
        number: String?,
        numberHashed: String?,
        countryCode: String?,
        contactName: String?,
        messageHash: String?,
        messageLength: Int,
        contactUID: Int,
        smsID: String
    ) {
        Log.d(TAG, "saveEntry() overload")
        saveEntry(context, false, type, timestamp, number, numberHashed, countryCode, contactName, messageHash, messageLength, contactUID, smsID)
    }

    /**
     * Generates unique SMS identifier
     *
     * @param partnerNumber partner number
     * @param timestamp event's timestamp
     * @return unique sms id
     */
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
